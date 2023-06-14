package ru.all_easy.push.telegram.api;

public enum ParseMode {
    MARKDOWN("Markdown");

    private final String mode;

    private ParseMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return this.mode;
    }
}
