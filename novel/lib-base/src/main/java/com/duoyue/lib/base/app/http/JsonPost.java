package com.duoyue.lib.base.app.http;

import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.compress.GZip;
import com.duoyue.lib.base.crash.CrashLogPresenter;
import com.duoyue.lib.base.crypto.NES;
import com.duoyue.lib.base.log.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import okhttp3.*;
import okio.BufferedSink;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public class JsonPost<T> {
    private static final String TAG = "Base#JsonPost";

    public static class SyncPost<T> {
        private JsonPost<T> post;

        public SyncPost() {
            post = new JsonPost<>();
        }

        public SyncPost<T> setRequest(JsonRequest jsonRequest) {
            post.jsonRequest = jsonRequest;
            return this;
        }

        public SyncPost<T> setResponseType(Class<T> type) {
            post.responseType = type;
            return this;
        }

        public JsonResponse<T> post() throws Throwable {
            return post.post();
        }
    }

    public static class AsyncPost<T> {
        private JsonPost<T> post;

        public AsyncPost() {
            post = new JsonPost<>();
        }

        public AsyncPost<T> setRequest(JsonRequest jsonRequest) {
            post.jsonRequest = jsonRequest;
            return this;
        }

        public AsyncPost<T> setResponseType(Class<T> type) {
            post.responseType = type;
            return this;
        }

        public AsyncPost<T> subscribeOn(Scheduler scheduler) {
            post.subscribeScheduler = scheduler;
            return this;
        }

        public AsyncPost<T> observeOn(Scheduler scheduler) {
            post.observeScheduler = scheduler;
            return this;
        }

        public void post(DisposableObserver<JsonResponse<T>> observer) {
            post.post(observer);
        }
    }


    private JsonRequest jsonRequest;
    private Class<T> responseType;
    private Scheduler subscribeScheduler;
    private Scheduler observeScheduler;
    private RequestParser parser;

    private JsonPost() {
    }

    private JsonResponse<T> post() throws Throwable {
        OkHttpClient client = buildHttpClient();
        Request request = buildHttpRequest();
        Call call = client.newCall(request);
        Response response = call.execute();
        int code = response.code();
        switch (code) {
            case 200:
                return parseResponse(response.body().bytes());
            case 204:
                return parseResponse();
            default:
                if (!parser.getUrl().equals(Constants.DOMAIN_BUSINESS + "/app/stats/v1/pull")) {
                    CrashLogPresenter.uploadPullData(parser.getUrl(), "2");
                }
                throw new Exception("Error Code: " + code);
        }
    }

    private void post(DisposableObserver<JsonResponse<T>> observer) {
        Observable.create(new ObservableOnSubscribe<JsonResponse<T>>() {
            @Override
            public void subscribe(ObservableEmitter<JsonResponse<T>> emitter) throws Exception {
                try {
                    JsonResponse<T> response = post();
                    emitter.onNext(response);
                } catch (Throwable throwable) {
                    Logger.e(TAG, "subscribe: {}, {}", (parser != null ? parser.getUrl() : "NULL"), throwable);
                    emitter.onError(throwable);
                    if (!parser.getUrl().equals(Constants.DOMAIN_BUSINESS + "/app/stats/v1/pull")) {
                        CrashLogPresenter.uploadPullData(parser.getUrl(), "2");
                    }
                }
            }
        })
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer);
    }

    private OkHttpClient buildHttpClient() {
        return HttpClient.getInstance();
    }

    private Request buildHttpRequest() throws Throwable {
        parser = RequestParser.parse(jsonRequest);
        Request.Builder builder = new Request.Builder();
        builder.url(parser.getUrl());
        Logger.i(TAG, "HttpRequest: url=", parser.getUrl());
        for (Map.Entry<String, String> entry : parser.getHeader().entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
            Logger.e(TAG, "HttpRequest: key={}, value={}", entry.getKey(), entry.getValue());
        }
        builder.post(new JsonRequestBody(parser.getRequest()));
        Logger.e(TAG, "HttpRequest: body=", parser.getRequest());
        return builder.build();
    }

    private JsonResponse<T> parseResponse(byte[] data) throws Throwable {
        byte[] response = NES.decode(data);
        response = GZip.unzip(response);
        return parseResponse(new JsonParser().parse(new String(response)).getAsJsonObject());
    }

    private JsonResponse<T> parseResponse() throws Throwable {
        JsonObject json = new JsonObject();
        json.addProperty("code", "200");
        json.addProperty("status", 1);
        return parseResponse(json);
    }

    private JsonResponse<T> parseResponse(JsonObject json) {
        Logger.e(TAG, "HttpResponse: url=", parser.getUrl());
        Logger.e(TAG, "HttpResponse: body=", json);

        JsonResponse jsonResponse = new Gson().fromJson(json, new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{responseType};
            }

            @Override
            public Type getRawType() {
                return JsonResponse.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });
        if ("200".equals(jsonResponse.code) && jsonResponse.status == 1) {
            if (!parser.getUrl().equals(Constants.DOMAIN_BUSINESS + "/app/stats/v1/pull")) {
                CrashLogPresenter.uploadPullData(parser.getUrl(), "1");
            }
        }else {
            if (!parser.getUrl().equals(Constants.DOMAIN_BUSINESS + "/app/stats/v1/pull")) {
                CrashLogPresenter.uploadPullData(parser.getUrl(), "2");
            }
        }
        return jsonResponse;
    }

    private class JsonRequestBody extends RequestBody {
        private byte[] body;

        private JsonRequestBody(JsonObject json) {
            body = json.toString().getBytes();
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse("application/json;charset=utf-8");
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            try {
                byte[] data = GZip.zip(body);
                sink.write(NES.encode(data));
            } catch (Throwable throwable) {
                throw new IOException(throwable.getMessage(), throwable.getCause());
            }
        }
    }
}
