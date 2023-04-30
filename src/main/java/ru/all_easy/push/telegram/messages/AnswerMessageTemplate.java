package ru.all_easy.push.telegram.messages;

public enum AnswerMessageTemplate {

    UNREGISTERED_ROOM("It seems virtual room is empty, send /addme command and ask others to do same 🙃"),
    UNREGISTERED_USER("I guess user: *%s* is not registered, ask him to call /start command in private messages with me please"),
    UNADDED_USER("I guess user: *%s* is not in virtual group, ask him to call /addme command"),
    YOURSELF_PUSH("You can't push to yourself \uD83D\uDE22");

    private final String message;

    AnswerMessageTemplate(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
