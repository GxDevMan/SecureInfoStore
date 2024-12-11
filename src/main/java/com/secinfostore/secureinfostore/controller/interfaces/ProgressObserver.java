package com.secinfostore.secureinfostore.controller.interfaces;

public interface ProgressObserver {
    void updateProgress(double progress);
    void updateStatus(String status);
}
