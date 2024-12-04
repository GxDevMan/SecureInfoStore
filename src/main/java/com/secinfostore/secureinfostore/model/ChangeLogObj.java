package com.secinfostore.secureinfostore.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "ChangeLog")
public class ChangeLogObj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long changeId;

    @ManyToOne
    @JoinColumn(name = "accountId")
    private AccountObj account;

    @Column(nullable = false)
    private Timestamp timeChanged;

    private String platformName;

    @Column(columnDefinition = "TEXT")
    private String userName;

    @Column(columnDefinition = "TEXT")
    private String email;

    @Column(columnDefinition = "TEXT")
    private String password;

    public ChangeLogObj() {
    }

    public ChangeLogObj(long changeId, AccountObj account, Timestamp timeChanged, String platformName, String userName, String email, String password) {
        this.changeId = changeId;
        this.account = account;
        this.timeChanged = timeChanged;
        this.platformName = platformName;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public ChangeLogObj(AccountObj account, Timestamp timeChanged, String platformName, String userName, String email, String password) {
        this.account = account;
        this.timeChanged = timeChanged;
        this.platformName = platformName;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public long getChangeId() {
        return changeId;
    }

    public void setChangeId(long changeId) {
        this.changeId = changeId;
    }

    public AccountObj getAccount() {
        return account;
    }

    public void setAccount(AccountObj account) {
        this.account = account;
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

    public Timestamp getTimeChanged() {
        return timeChanged;
    }

    public void setTimeChanged(Timestamp timeChanged) {
        this.timeChanged = timeChanged;
    }
}
