package com.secinfostore.model;
import java.util.Date;

public class TextObjDTO {
    private long textId;
    private Date timeModified;
    private String textTitle;
    private String tags;

    public TextObjDTO(long textId, Date timeModified, String textTitle, String tags) {
        this.textId = textId;
        this.timeModified = timeModified;
        this.textTitle = textTitle;
        this.tags = tags;
    }

    public long getTextId() {
        return textId;
    }

    public void setTextId(long textId) {
        this.textId = textId;
    }

    public Date getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(Date timeModified) {
        this.timeModified = timeModified;
    }

    public String getTextTitle() {
        return textTitle;
    }

    public void setTextTitle(String textTitle) {
        this.textTitle = textTitle;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
