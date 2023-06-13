package ru.all_easy.push.telegram.api;

public enum ChatType {
    
    SUPER_GROUP("supergroup"),
    GROUP("group"),
    PRIVATE("private");

    private final String type;

    private ChatType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
