package com.duoyue.app.upgrade.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.duoyue.app.upgrade.download.DownState;
import com.duoyue.app.upgrade.download.DownloadInfo;
import com.duoyue.lib.base.http.HttpUtils;
import com.duoyue.lib.base.log.Logger;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * 一个下载任务
 *
 * @author fanwentao
 */
public class DownTask {
    private int state;
    private Handler handler;
    private Context mContext;
    private DownloadInfo mBean;
    private boolean isPosting;

    public DownTask(DownloadInfo bean, Context context, Handler handler) {
        this.mBean = bean;
        this.handler = handler;
        this.mContext = context;
        this.state = DownState.WAITING;
    }

    public void setState(int state) {
        this.state = state;
        this.mBean.setDownState(state);
    }

    public void setHandle(Handler handler) {
        this.handler = handler;
    }

    public int getState() {
        return state;
    }

    public DownloadInfo getBean() {
        if (mBean != null) {
            return this.mBean;
        }
        return null;
    }

    public boolean doWork() {
        File file = null;
        sendMsg();
        //检查本地文件是否存在
        final File pathFile = new File(mBean.getDownPath());
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        file = new File(mBean.getDownPath(), mBean.getFileName());
        if (!file.exists()) {
            try {
                file.createNewFile();  // 新下载任务
            } catch (IOException e) {
                state = DownState.FILEERROR;
                e.printStackTrace();
            }
        }

        //如果是下载状态的任务就进行下载
        if (state == DownState.LINKING) {
            try {
                HttpUtils.getInstance().getContentLength(mBean.getUrl(), new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        state = DownState.NETERROR;
                        sendMsg();
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {
                        if (response.code() != 200) {
                            close(response.body());
                            state = DownState.NETERROR;
                            sendMsg();
                            return;
                        }
                        ResponseBody body = response.body();
                        if (body == null) {
                            state = DownState.NETERROR;
                            sendMsg();
                            return;
                        }
                        long length = body.contentLength();
                        mBean.setFileSize(length);

                        download(mBean.getDownPath() + File.separator + mBean.getFileName(),
                                mBean.getUrl(), length, mBean.getCompleteSize());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public void download(final String savePath, final String url, final long fileSize, final long startIndex) throws IOException {
        // 分段请求网络连接,分段将文件保存到本地.
        // 加载下载位置缓存数据文件
        final File cacheFile = new File(savePath);
        final RandomAccessFile cacheAccessFile = new RandomAccessFile(cacheFile, "rwd");
        HttpUtils.getInstance().downloadFileByRange(url, startIndex, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                state = DownState.NETERROR;
                mBean.setDownState(state);
                sendMsg();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.code() != 206) {// 206：请求部分资源成功码，表示服务器支持断点续传
                    state = DownState.NETERROR;
                    sendMsg();
                    return;
                }
                InputStream is = response.body().byteStream();// 获取流
                RandomAccessFile tmpAccessFile = new RandomAccessFile(cacheFile, "rw");// 获取前面已创建的文件.
                tmpAccessFile.seek(startIndex);// 文件写入的开始位置.
                /*  将网络流中的文件写入本地*/
                byte[] buffer = new byte[1024 << 5];
                int length = -1;
                long total = startIndex;// 记录下载文件的大小
                int curProgress = 0;
                try {
                    while ((length = is.read(buffer)) > 0) {//读取流
                        total += length;
                        curProgress = (int) (100 * total / fileSize);
//                        Logger.e("App#", "pecent: " + curProgress + "%" + ", completeSize:" + total);
                        tmpAccessFile.write(buffer, 0, length);
                        state = DownState.DOWNLOAD;
                        mBean.setCompleteSize(total);
                        sendMsg();
                        if (total == fileSize) {
                            Logger.e("App#", "pecent: " + curProgress + "% 下载完成");
                            break;
                        }
                    }
                } catch (Exception e) {
                    close(cacheAccessFile, is, response.body());
                    state = DownState.NETERROR;
                    sendMsg();
                    return;
                } finally {
                    //关闭资源
                    close(cacheAccessFile, is, response.body());
                }
                state = DownState.FINISH;
                mBean.setCompleteSize(total);
                sendMsg();
            }
        });

    }

    private void sendMsg() {
        DaoDownloadManager.getInstance(mContext).saveInfos(mBean);
        if (!isPosting) {
            isPosting = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message msg = Message.obtain();
                    msg.what = state;
                    msg.obj = mBean;
                    mBean.setDownState(state);
                    handler.sendMessage(msg);
                    isPosting = false;
                }
            }, 800);
        }

    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void close(Closeable... closeables) {
        int length = closeables.length;
        try {
            for (int i = 0; i < length; i++) {
                Closeable closeable = closeables[i];
                if (null != closeable) {
                    closeables[i].close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < length; i++) {
                closeables[i] = null;
            }
        }
    }

}
