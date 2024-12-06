package com.secinfostore.secureinfostore.util;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ClipboardHandler {
    public static void pasteTextToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text.trim());
        clipboard.setContent(content);
    }
}
