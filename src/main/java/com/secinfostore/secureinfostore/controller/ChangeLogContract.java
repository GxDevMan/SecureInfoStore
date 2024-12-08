package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.model.ChangeLogObj;

public interface ChangeLogContract<S> {
    void deleteChangeLog(S changeLogObj);
}
