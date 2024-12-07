package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.model.AccountObj;

public interface AddUpdateContract {
    void saveAccountToDB(AccountObj account);
}
