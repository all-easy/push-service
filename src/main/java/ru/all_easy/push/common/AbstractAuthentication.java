package ru.all_easy.push.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.all_easy.push.web.security.model.User;

public abstract class AbstractAuthentication {

    protected User getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
