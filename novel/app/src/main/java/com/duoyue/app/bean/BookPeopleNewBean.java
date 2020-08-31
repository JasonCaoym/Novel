package com.duoyue.app.bean;

import java.util.List;

public class BookPeopleNewBean {


    private List<BookNewUserBagStatusesBean> newUserBagStatuses;

    private int type;


    public List<BookNewUserBagStatusesBean> getNewUserBagStatuses() {
        return newUserBagStatuses;
    }

    public void setNewUserBagStatuses(List<BookNewUserBagStatusesBean> newUserBagStatuses) {
        this.newUserBagStatuses = newUserBagStatuses;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
