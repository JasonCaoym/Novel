package com.duoyue.app.bean;


public class BookRankItemBean {

    private Long id;
    private String name;
    private String authorName;
    private String cover;
    private String resume;
    private int rank;
    private float star;
    private long voteNum;
    private long wordCount;
    private long popularityNum;
    private long fansNum;
    private long weekDownPv;
    private int realWeekRead;
    private int realWeekCollect;

    public int getRealWeekRead() {
        return realWeekRead;
    }

    public void setRealWeekRead(int realWeekRead) {
        this.realWeekRead = realWeekRead;
    }

    public int getRealWeekCollect() {
        return realWeekCollect;
    }

    public void setRealWeekCollect(int realWeekCollect) {
        this.realWeekCollect = realWeekCollect;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public long getVoteNum() {
        return voteNum;
    }

    public void setVoteNum(long voteNum) {
        this.voteNum = voteNum;
    }

    public long getWordCount() {
        return wordCount;
    }

    public void setWordCount(long wordCount) {
        this.wordCount = wordCount;
    }

    public long getPopularityNum() {
        return popularityNum;
    }

    public void setPopularityNum(long popularityNum) {
        this.popularityNum = popularityNum;
    }

    public long getFansNum() {
        return fansNum;
    }

    public void setFansNum(long fansNum) {
        this.fansNum = fansNum;
    }

    public long getWeekDownPv() {
        return weekDownPv;
    }

    public void setWeekDownPv(long weekDownPv) {
        this.weekDownPv = weekDownPv;
    }
}
