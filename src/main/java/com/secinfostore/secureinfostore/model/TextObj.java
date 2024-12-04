package com.secinfostore.secureinfostore.model;

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
    private String textInformation;

    public TextObj() {
    }

    public TextObj(long textId, Timestamp timeModified, String textTitle, String textInformation) {
        this.textId = textId;
        this.timeModified = timeModified;
        this.textTitle = textTitle;
        this.textInformation = textInformation;
    }

    public TextObj(Timestamp timeModified, String textTitle, String textInformation) {
        this.timeModified = timeModified;
        this.textTitle = textTitle;
        this.textInformation = textInformation;
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
}
