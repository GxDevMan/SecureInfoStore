package com.secinfostore.secureinfostore.controller.interfaces;

public interface AddUpdateContract<S> {
    void saveEntityToDB(S object);
}
