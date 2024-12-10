package com.secinfostore.secureinfostore.controller;
public interface UpdateDeleteViewConfirmContract<S> {
    void viewUpdateAccount(S account);
    void confirmDeleteAccount(S account);
}
