package ru.all_easy.push.common;

public enum ErrorType {
    not_room_member("not_room_member"),
    shape_exist("shape_exist");

    private final String name;

    ErrorType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
