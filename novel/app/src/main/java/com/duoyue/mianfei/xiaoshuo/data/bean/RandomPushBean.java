package com.duoyue.mianfei.xiaoshuo.data.bean;

import java.io.Serializable;

public class RandomPushBean implements Serializable {


    /**
     * book : {"bookId":10793,"bookName":"九阳神王","cover":"http://rsayd.yule37.cn/img_test/201905/10/64/9ca425f2-d58f-430a-82e4-f1f382107b64.jpg","authorName":"寂小贼","state":1,"star":8.6,"wordCount":9523877,"popularityNum":252215830,"lastChapIssueTime":1557426383,"resume":"落魄皇子秦云，得九阳传承，觉醒惊世武魂，习得绝世炼器之术。从此，他在修武大道上，一路潇洒风流。各种武道强者，为求他炼器，甘愿充当他当小弟。各色神女圣女，为求他炼器，都愿意委身与他。但秦云却不满意，天下美女十斗，他就要占十斗。","from":2,"issueTime":1488865650,"lastChapter":3187,"updateTime":"2019-06-16 03:02","timeTip":"1月前更新"}
     */

    private BookBean book;

    public BookBean getBook() {
        return book;
    }

    public void setBook(BookBean book) {
        this.book = book;
    }

    public static class BookBean implements Serializable{
        /**
         * bookId : 10793
         * bookName : 九阳神王
         * cover : http://rsayd.yule37.cn/img_test/201905/10/64/9ca425f2-d58f-430a-82e4-f1f382107b64.jpg
         * authorName : 寂小贼
         * state : 1
         * star : 8.6
         * wordCount : 9523877
         * popularityNum : 252215830
         * lastChapIssueTime : 1557426383
         * resume : 落魄皇子秦云，得九阳传承，觉醒惊世武魂，习得绝世炼器之术。从此，他在修武大道上，一路潇洒风流。各种武道强者，为求他炼器，甘愿充当他当小弟。各色神女圣女，为求他炼器，都愿意委身与他。但秦云却不满意，天下美女十斗，他就要占十斗。
         * from : 2
         * issueTime : 1488865650
         * lastChapter : 3187
         * updateTime : 2019-06-16 03:02
         * timeTip : 1月前更新
         */

        private long bookId;
        private String bookName;
        private String cover;
        private String authorName;
        private int state;
        private double star;
        private int wordCount;
        private int popularityNum;
        private int lastChapIssueTime;
        private String resume;
        private String from;
        private int issueTime;
        private int lastChapter;
        private String updateTime;
        private String timeTip;
        private String catName;
        private int lastReadChapter;

        public int getLastReadChapter() {
            return lastReadChapter;
        }

        public void setLastReadChapter(int lastReadChapter) {
            this.lastReadChapter = lastReadChapter;
        }

        public String getCatName() {
            return catName;
        }

        public void setCatName(String catName) {
            this.catName = catName;
        }

        public long getBookId() {
            return bookId;
        }

        public void setBookId(long bookId) {
            this.bookId = bookId;
        }

        public String getBookName() {
            return bookName;
        }

        public void setBookName(String bookName) {
            this.bookName = bookName;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getAuthorName() {
            return authorName;
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public double getStar() {
            return star;
        }

        public void setStar(double star) {
            this.star = star;
        }

        public int getWordCount() {
            return wordCount;
        }

        public void setWordCount(int wordCount) {
            this.wordCount = wordCount;
        }

        public int getPopularityNum() {
            return popularityNum;
        }

        public void setPopularityNum(int popularityNum) {
            this.popularityNum = popularityNum;
        }

        public int getLastChapIssueTime() {
            return lastChapIssueTime;
        }

        public void setLastChapIssueTime(int lastChapIssueTime) {
            this.lastChapIssueTime = lastChapIssueTime;
        }

        public String getResume() {
            return resume;
        }

        public void setResume(String resume) {
            this.resume = resume;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public int getIssueTime() {
            return issueTime;
        }

        public void setIssueTime(int issueTime) {
            this.issueTime = issueTime;
        }

        public int getLastChapter() {
            return lastChapter;
        }

        public void setLastChapter(int lastChapter) {
            this.lastChapter = lastChapter;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getTimeTip() {
            return timeTip;
        }

        public void setTimeTip(String timeTip) {
            this.timeTip = timeTip;
        }
    }
}
