package com.duoyue.app.common.data.response.bookdownload;

import android.os.Parcel;
import android.os.Parcelable;
import com.duoyue.app.bean.AllChapterDownloadBean;

import java.util.ArrayList;
import java.util.List;

public class AllChapterDownloadResp implements Parcelable {

    private List<AllChapterDownloadBean> chapters;

    /**
     * 书籍来源(1:运营上传;2:掌阅接口;3:掌阅爬虫接口)
     */
    private int from;

    public List<AllChapterDownloadBean> getChapters() {
        return chapters;
    }

    public void setChapters(List<AllChapterDownloadBean> chapters) {
        this.chapters = chapters;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.chapters);
        dest.writeInt(this.from);
    }

    public AllChapterDownloadResp() {
    }

    protected AllChapterDownloadResp(Parcel in) {
        this.chapters = new ArrayList<AllChapterDownloadBean>();
        in.readList(this.chapters, AllChapterDownloadBean.class.getClassLoader());
        this.from = in.readInt();
    }

    public static final Creator<AllChapterDownloadResp> CREATOR = new Creator<AllChapterDownloadResp>() {
        @Override
        public AllChapterDownloadResp createFromParcel(Parcel source) {
            return new AllChapterDownloadResp(source);
        }

        @Override
        public AllChapterDownloadResp[] newArray(int size) {
            return new AllChapterDownloadResp[size];
        }
    };
}
