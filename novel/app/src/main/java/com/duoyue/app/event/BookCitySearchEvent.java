package com.duoyue.app.event;

public class BookCitySearchEvent {

    private String msg;

    public BookCitySearchEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
