package com.secinfostore.secureinfostore.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.secinfostore.secureinfostore.util.Views;

import javax.persistence.*;
import java.util.List;

@Entity
public class AccountObj {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountId;

    @JsonView(Views.Public.class)
    private String platformName;

    @Column(columnDefinition = "TEXT")
    @JsonView(Views.Public.class)
    private String userName;


    @Column(columnDefinition = "TEXT")
    @JsonView(Views.Public.class)
    private String email;


    @Column(columnDefinition = "TEXT")
    @JsonView(Views.Public.class)
    private String password;

    private byte[] platformThumbnail;

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<ChangeLogObj> changeLog;

    public AccountObj() {
    }

    public AccountObj(String platformName, String userName, String email, String password, byte[] platformThumbnail) {
        this.platformName = platformName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.platformThumbnail = platformThumbnail;
    }

    public AccountObj(long accountId, String platformName, String userName, String email, String password, byte[] platformThumbnail) {
        this.accountId = accountId;
        this.platformName = platformName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.platformThumbnail = platformThumbnail;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getPlatformThumbnail() {
        return platformThumbnail;
    }

    public void setPlatformThumbnail(byte[] platformThumbnail) {
        this.platformThumbnail = platformThumbnail;
    }

    public List<ChangeLogObj> getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(List<ChangeLogObj> changeLog) {
        this.changeLog = changeLog;
    }

    @Override
    public String toString() {
        return "AccountObj{" +
                "accountId=" + accountId +
                ", platformName='" + platformName + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", changeLogSize=" + (changeLog != null ? changeLog.size() : 0) +
                '}';
    }
}
