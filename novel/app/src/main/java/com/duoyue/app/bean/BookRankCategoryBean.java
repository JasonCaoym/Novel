package com.duoyue.app.bean;

import com.google.gson.annotations.Expose;
import kotlinx.android.parcel.Parcelize;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Parcelize
@Entity
public class BookRankCategoryBean {

    @Id
    private Long id;
    private String name;
    @Expose(deserialize = false, serialize = false)
    private boolean selected;
    @Generated(hash = 1600184771)
    public BookRankCategoryBean(Long id, String name, boolean selected) {
        this.id = id;
        this.name = name;
        this.selected = selected;
    }
    @Generated(hash = 1880151899)
    public BookRankCategoryBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean getSelected() {
        return this.selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
