package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/userFeedBack/v1/push", domain = DomainType.BUSINESS)
public class FeedCommitReq extends JsonRequest {

    @SerializedName("ideaId")
    private int ideaId;

    @SerializedName("content")
    private String content;

    @SerializedName("desc")
    private String desc;

    @SerializedName("concact")
    private String concact;

    public FeedCommitReq() {

    }

    public FeedCommitReq(int ideaId, String content, String desc, String concact) {
        this.ideaId = ideaId;
        this.content = content;
        this.desc = desc;
        this.concact = concact;
    }

    public int getIdeaId() {
        return ideaId;
    }

    public void setIdeaId(int ideaId) {
        this.ideaId = ideaId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getConcact() {
        return concact;
    }

    public void setConcact(String concact) {
        this.concact = concact;
    }
}
