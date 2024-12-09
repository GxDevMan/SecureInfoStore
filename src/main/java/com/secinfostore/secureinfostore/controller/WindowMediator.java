package com.secinfostore.secureinfostore.controller;

public interface WindowMediator {
    void switchTo(String screenName, Object data);
    void registerController(String screenName, BaseController controller);
    void windowMediaInfo();
}
