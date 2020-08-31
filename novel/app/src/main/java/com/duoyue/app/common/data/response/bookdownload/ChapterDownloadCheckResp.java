package com.duoyue.app.common.data.response.bookdownload;

public class ChapterDownloadCheckResp {

    /**
     * 0 可以下载 1书豆不足不能下载
     */
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
