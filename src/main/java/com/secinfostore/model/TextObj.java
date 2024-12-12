package com.secinfostore.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "Text_Table")
public class TextObj {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long textId;

    private Timestamp timeModified;

    private String textTitle;

    @Column(columnDefinition = "TEXT")
    private String tags;

    @Column(columnDefinition = "TEXT")
    private String textInformation;

    public TextObj() {
    }

    public TextObj(TextObj other) {
        this.textId = other.textId;
        this.timeModified = other.timeModified;
        this.textTitle = other.textTitle;
        this.textInformation = other.textInformation;
        this.tags = other.tags;
    }

    public TextObj(long textId, Timestamp timeModified, String textTitle, String tags) {
        this.textId = textId;
        this.timeModified = timeModified;
        this.textTitle = textTitle;
        this.tags = tags;
    }

    public TextObj(long textId, Timestamp timeModified, String textTitle, String textInformation, String tags) {
        this.textId = textId;
        this.timeModified = timeModified;
        this.textTitle = textTitle;
        this.textInformation = textInformation;
        this.tags = tags;
    }

    public TextObj(Timestamp timeModified, String textTitle, String textInformation, String tags) {
        this.timeModified = timeModified;
        this.textTitle = textTitle;
        this.textInformation = textInformation;
        this.tags = tags;
    }

    public long getTextId() {
        return textId;
    }

    public void setTextId(long textId) {
        this.textId = textId;
    }

    public Timestamp getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(Timestamp timeModified) {
        this.timeModified = timeModified;
    }

    public String getTextTitle() {
        return textTitle;
    }

    public void setTextTitle(String textTitle) {
        this.textTitle = textTitle;
    }

    public String getTextInformation() {
        return textInformation;
    }

    public void setTextInformation(String textInformation) {
        this.textInformation = textInformation;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
