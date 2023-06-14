package ru.all_easy.push.user.controller;

import javax.security.auth.message.AuthException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.all_easy.push.common.AbstractAuthentication;
import ru.all_easy.push.common.Result;
import ru.all_easy.push.helper.GeneratorHelper;
import ru.all_easy.push.user.service.UserService;
import ru.all_easy.push.user.service.model.LoginResult;
import ru.all_easy.push.user.service.model.RegisterInfo;
import ru.all_easy.push.user.service.model.RegisterResult;
import ru.all_easy.push.user.service.model.UserServiceInfo;

@RestController
@RequestMapping("/v1/api/user")
public class UserController extends AbstractAuthentication {

    private final UserService userService;
    private final GeneratorHelper generatorHelper;

    public UserController(UserService userService, GeneratorHelper generatorHelper) {
        this.userService = userService;
        this.generatorHelper = generatorHelper;
    }

    @GetMapping("/current")
    public Result<String> user() {
        return new Result<>(
                getAuthentication().username() + " " + getAuthentication().uid());
    }

    @PostMapping("/register")
    public Result<RegisterResponse> register(@RequestBody RegisterRequest request) {
        RegisterInfo register =
                new RegisterInfo(request.username(), request.password(), generatorHelper.generateUUID());
        RegisterResult result = userService.register(register);

        RegisterResponse response = new RegisterResponse(result.username(), result.uid());
        return new Result<>(response, null, null, null);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) throws AuthException {
        UserServiceInfo userServiceInfo = new UserServiceInfo(loginRequest.username(), loginRequest.password());
        LoginResult result = userService.login(userServiceInfo);

        LoginResponse response = new LoginResponse(result.username(), result.accessToken(), result.uid());
        return new Result<>(response);
    }
}
