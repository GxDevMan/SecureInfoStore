package com.secinfostore.secureinfostore.controller;

public interface AddUpdateContract<S> {
    void saveAccountToDB(S object);
}
