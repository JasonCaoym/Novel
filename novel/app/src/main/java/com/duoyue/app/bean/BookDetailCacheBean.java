package com.duoyue.app.bean;


import java.io.Serializable;

public class BookDetailCacheBean implements Serializable {

    private String bookId;
    private long randomTime;

    public BookDetailCacheBean(String bookId, long randomTime) {
        this.bookId = bookId;
        this.randomTime = randomTime;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public long getRandomTime() {
        return randomTime;
    }

    public void setRandomTime(long randomTime) {
        this.randomTime = randomTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookDetailCacheBean that = (BookDetailCacheBean) o;

        return bookId.equals(that.bookId);
    }

    @Override
    public int hashCode() {
        return bookId.hashCode();
    }
}
