package com.duoyue.app.bean;

/**
 * 书籍下载的单个章节
 */
public class BookDownloadChapterBean {

    /**
     * 章节id
     */
    private long id;

    /**
     * 章节序号
     */
    private int seqNum;

    /**
     * 章节名称
     */
    private String title;

    /**
     * 下载url密钥
     */
    private String secret;

    /**
     * 加密的下载地址
     */
    private String url;

    /**
     * 是否选中,非接口字段
     */
    private boolean isChecked;

    /**
     * 是否已经下载,非接口字段
     */
    private boolean isDownload;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }
}
