package com.zydm.base.data.tools;

import android.text.TextUtils;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.zydm.base.data.base.IIdGetter;
import com.zydm.base.data.base.MtMap;
import com.zydm.base.utils.LogUtils;
import com.zydm.base.utils.StringUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    private static Gson sGson;

    static {
        sGson = new GsonBuilder()
                .registerTypeAdapter(int.class, new TypeAdapter<Number>() {
                    @Override
                    public Number read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) {
                            in.nextNull();
                            return 0;
                        }
                        try {
                            return in.nextInt();
                        } catch (NumberFormatException e) {
                            if (StringUtils.isBlank(in.nextString())) {
                                return 0;
                            } else {
                                throw e;
                            }
                        }
                    }

                    @Override
                    public void write(JsonWriter out, Number value) throws IOException {
                        out.value(value);
                    }
                })
                .registerTypeAdapter(double.class, new TypeAdapter<Number>() {
                    @Override
                    public Number read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) {
                            in.nextNull();
                            return 0d;
                        }
                        try {
                            return in.nextDouble();
                        } catch (NumberFormatException e) {
                            if (StringUtils.isBlank(in.nextString())) {
                                return 0d;
                            } else {
                                throw e;
                            }
                        }
                    }

                    @Override
                    public void write(JsonWriter out, Number value) throws IOException {
                        out.value(value);
                    }
                })
                .registerTypeAdapter(long.class, new TypeAdapter<Number>() {
                    @Override
                    public Number read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) {
                            in.nextNull();
                            return 0L;
                        }
                        try {
                            return in.nextLong();
                        } catch (NumberFormatException e) {
                            if (StringUtils.isBlank(in.nextString())) {
                                return 0L;
                            } else {
                                throw e;
                            }
                        }
                    }

                    @Override
                    public void write(JsonWriter out, Number value) throws IOException {
                        out.value(value);
                    }
                }).create();
    }

    public static <T> T fromJson(String dataJsonStr, Type dataType) {
        return sGson.fromJson(dataJsonStr, dataType);
    }

    public static <T> T parseJson(String jsonData, Type clazz) {
        if (TextUtils.isEmpty(jsonData)) {
            return null;
        }
        try {
            return sGson.fromJson(jsonData, clazz);
        } catch (Throwable e) {
            LogUtils.d(TAG, e.toString());
        }

        return null;
    }

    public static MtMap<String, String> parseJson(String jsonData) {
        return sGson.fromJson(jsonData, new TypeToken<MtMap<String, String>>() {
        }.getType());
    }

    public static <T> List<T> parseJsonArray(String jsonData, Type clazz) {

        JsonParser parser = new JsonParser();
        JsonArray Jarray = parser.parse(jsonData).getAsJsonArray();

        ArrayList<T> lcs = new ArrayList<T>();

        for (JsonElement obj : Jarray) {
            T cse = sGson.fromJson(obj, clazz);
            lcs.add(cse);
        }

        return lcs;
    }

    public static String toJson(Object src) {
        return sGson.toJson(src);
    }

    public static String extractIdToJsonArray(List<? extends IIdGetter> srcData) {
        return extractFieldToJsonArray(srcData, new IFieldExtractor<IIdGetter>() {
            @Override
            public String extract(IIdGetter item) {
                return item.getId();
            }
        });
    }

    public static String extractFieldToJsonArray(List srcData, IFieldExtractor fieldExtractor) {
        if (DataUtils.isEmptyList(srcData) || null == fieldExtractor) {
            return "";
        }
        JSONArray jsonArray = new JSONArray();
        for (Object item : srcData) {
            String fieldValue = fieldExtractor.extract(item);
            if (!StringUtils.isBlank(fieldValue)) {
                jsonArray.put(fieldValue);
            }
        }
        return jsonArray.length() > 0 ? jsonArray.toString() : "";
    }

    public interface IFieldExtractor<E> {

        String extract(E item);
    }

    public static String getJsonArrayStr(String... item) {
        if (item == null) {
            return "";
        }
        JSONArray jsonArray = new JSONArray();
        for (String str : item) {
            jsonArray.put(str);
        }
        return jsonArray.length() > 0 ? jsonArray.toString() : "";
    }
}
