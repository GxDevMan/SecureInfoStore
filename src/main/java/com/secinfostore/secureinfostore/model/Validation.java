package com.secinfostore.secureinfostore.model;

import javax.persistence.*;

@Entity
@Table(name = "Validation")
public class Validation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long valid_id;

    @Column(columnDefinition = "TEXT")
    private String testTest;

    public Validation() {
    }

    public Validation(long valid_id, String testTest) {
        this.valid_id = valid_id;
        this.testTest = testTest;
    }

    public Validation(String testTest) {
        this.testTest = testTest;
    }

    public long getValid_id() {
        return valid_id;
    }

    public void setValid_id(long valid_id) {
        this.valid_id = valid_id;
    }

    public String getTestTest() {
        return testTest;
    }

    public void setTestTest(String testTest) {
        this.testTest = testTest;
    }
}
