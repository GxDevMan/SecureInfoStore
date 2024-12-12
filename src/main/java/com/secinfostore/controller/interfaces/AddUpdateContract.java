package com.secinfostore.controller.interfaces;

public interface AddUpdateContract<S> {
    void saveEntityToDB(S object);
}
