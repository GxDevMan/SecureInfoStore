package com.secinfostore.model;

import javax.persistence.*;

@Entity
@Table(name = "Validation")
public class Validation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long valid_id;

    @Column(columnDefinition = "TEXT")
    private String testText;

    public Validation() {
    }

    public Validation(long valid_id, String testTest) {
        this.valid_id = valid_id;
        this.testText = testTest;
    }

    public Validation(String testText) {
        this.testText = testText;
    }

    public long getValid_id() {
        return valid_id;
    }

    public void setValid_id(long valid_id) {
        this.valid_id = valid_id;
    }

    public String getTestText() {
        return testText;
    }

    public void setTestText(String testText) {
        this.testText = testText;
    }
}
