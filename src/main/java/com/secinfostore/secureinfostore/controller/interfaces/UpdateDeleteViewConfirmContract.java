package com.secinfostore.secureinfostore.controller.interfaces;
public interface UpdateDeleteViewConfirmContract<S> {
    void viewUpdateObj(S object);
    void confirmDeleteObj(S object);
}
