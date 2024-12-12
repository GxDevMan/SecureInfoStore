package com.secinfostore.controller.interfaces;

public interface WindowMediator {
    void switchTo(String screenName, Object data);
    void registerFXMLName(String screenName, String fxmlName);
    void windowMediaInfo();
}
