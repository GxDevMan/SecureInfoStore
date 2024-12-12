package com.secinfostore.model;

public enum CharSet {
    LOWERCASE("abcdefghijklmnopqrstuvwxyz"),
    UPPERCASE("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    NUMBERS("0123456789"),
    SPECIALCHAR("!@#$%^&*()");
    
    private final String charset;

    CharSet(String charset){
        this.charset = charset;
    }

    public String getCharset(){return charset;}
}
