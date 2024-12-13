package com.secinfostore.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.secinfostore.util.Views;

import javax.persistence.*;
import java.util.List;

@Entity
public class AccountObj {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountId;

    @JsonView(Views.Public.class)
    private String userPlatform;

    @Column(columnDefinition = "TEXT")
    @JsonView(Views.Public.class)
    private String userName;


    @Column(columnDefinition = "TEXT")
    @JsonView(Views.Public.class)
    private String userEmail;


    @Column(columnDefinition = "TEXT")
    @JsonView(Views.Public.class)
    private String userPassword;

    private byte[] platformThumbnail;

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<ChangeLogObj> changeLog;

    public AccountObj() {
    }

    public AccountObj(String userPlatform, String userName, String userEmail, String userPassword, byte[] platformThumbnail) {
        this.userPlatform = userPlatform;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.platformThumbnail = platformThumbnail;
    }

    public AccountObj(long accountId, String userPlatform, String userName, String userEmail, String userPassword, byte[] platformThumbnail) {
        this.accountId = accountId;
        this.userPlatform = userPlatform;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.platformThumbnail = platformThumbnail;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getUserPlatform() {
        return userPlatform;
    }

    public void setUserPlatform(String userPlatform) {
        this.userPlatform = userPlatform;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
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
                ", platformName='" + userPlatform + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + userEmail + '\'' +
                ", changeLogSize=" + (changeLog != null ? changeLog.size() : 0) +
                '}';
    }
}
