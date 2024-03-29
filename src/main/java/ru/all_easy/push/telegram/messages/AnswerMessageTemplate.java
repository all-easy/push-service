package ru.all_easy.push.telegram.messages;

public enum AnswerMessageTemplate {
    UNREGISTERED_ROOM("It seems virtual room is empty, send /addme command and ask others to do same 🙃"),
    UNSET_CURRENCY("Add currency to chat using /currency command. If no suitable currency in list, choose ANY ≡"),
    UNREGISTERED_USER(
            "I guess user: *%s* is not registered, ask him to call /start command in private messages with me please"),
    UNADDED_USER("I guess user: *%s* is not in virtual group, ask him to call /addme command"),
    INCORRECT_FORMAT("Incorrect format 🤔, try like this: /push @username 123 name 18%"),
    UNRECOGNIZED_USERNAME("Unrecognized username"),
    INCORRECT_MATH_EXPRESSION("You typed wrong amount expression, example: 10+10+10"),
    YOURSELF_PUSH("You can't push to yourself \uD83D\uDE22");

    private final String message;

    AnswerMessageTemplate(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
