package ru.all_easy.push.telegram.commands;

public enum Commands {

    START("/start"),
    PUSH("/push"),
    RESULT("/result"),
    HELP("/help"),
    HISTORY("/history"),
    ADD_ME("/addme");

    private final String command;

    private Commands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
