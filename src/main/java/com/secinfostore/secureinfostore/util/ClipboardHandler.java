package com.secinfostore.secureinfostore.util;

import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ClipboardHandler {
    public static void pasteTextToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text.trim());
        clipboard.setContent(content);
    }

    public static String getTextFromClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            return clipboard.getString();
        } else {
            return null;
        }
    }

    public static Image getImageFromClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasImage()) {
            return clipboard.getImage();
        } else {
            return null;
        }
    }
}
