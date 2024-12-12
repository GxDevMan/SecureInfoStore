package com.secinfostore.controller.interfaces;

public interface ProgressObserver {
    void updateProgress(double progress);
    void updateStatus(String status);
}
