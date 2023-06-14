package ru.all_easy.push.web.security;

import static org.springframework.util.StringUtils.hasText;

import io.micrometer.core.lang.Nullable;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.all_easy.push.common.Result;
import ru.all_easy.push.helper.JsonHelper;
import ru.all_easy.push.web.security.model.User;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Value("${api.key}")
    private String apiKey;

    public static final String AUTHORIZATION = "Authorization";
    public static final String API_KEY = "ApiKey";

    private final JwtProvider jwtProvider;
    private final JsonHelper jsonHelper;

    public JwtFilter(JwtProvider jwtProvider, JsonHelper jsonHelper) {
        this.jwtProvider = jwtProvider;
        this.jsonHelper = jsonHelper;
    }

    @Override
    protected void doFilterInternal(
            @Nullable HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable FilterChain filterChain)
            throws ServletException, IOException {

        if (request == null) {
            throw new JwtException("Request unexpected null", null);
        }

        String token = getTokenFromRequest(request);
        if (token != null && checkApiKey(request) && response != null) {
            try {
                jwtProvider.validateToken(token);
                User user = jwtProvider.getUser(token);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException ex) {
                unauthorized(response, ex.getMessage());
                return;
            }
        }

        if (filterChain != null) {
            filterChain.doFilter(request, response);
        }
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        var errorCode = HttpStatus.UNAUTHORIZED.value();
        String s = jsonHelper.toJson(new Result<>(message, errorCode));
        response.setStatus(errorCode);
        response.getWriter().write(s);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.split(" ")[1].trim();
        }
        return null;
    }

    private boolean checkApiKey(HttpServletRequest request) {
        String key = request.getHeader(API_KEY);
        return apiKey.equals(key);
    }
}
