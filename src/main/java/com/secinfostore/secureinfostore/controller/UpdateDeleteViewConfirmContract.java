package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.model.AccountObj;

public interface UpdateDeleteViewConfirmContract {
    void viewUpdateAccount(AccountObj account);
    void confirmDeleteAccount(AccountObj account);
}
