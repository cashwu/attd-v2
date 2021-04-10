package com.odde.atddv2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odde.atddv2.repo.UserRepo;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

import static com.odde.atddv2.config.TokenFilter.Token.parseToken;

@Component
@Order(1)
public class TokenFilter implements Filter {
    private final UserRepo userRepo;

    public TokenFilter(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (!isApiCall(req) || isValidToken(req))
            chain.doFilter(request, response);
        else
            ((HttpServletResponse) response).setStatus(401);
    }

    private boolean isValidToken(HttpServletRequest req) {
        try {
            return req.getHeader("Token") != null
                    && userRepo.existsByUserName(parseToken(req.getHeader("Token")).getUser());
        } catch (Exception ignore) {
            return false;
        }
    }

    private boolean isApiCall(HttpServletRequest req) {
        return req.getRequestURI().startsWith("/api/");
    }

    @Data
    @Accessors(chain = true)
    public static class Token {
        private String user;
        private long now = Instant.now().getEpochSecond();

        public static String makeToken(String userName) {
            return new Token().setUser(userName).generate();
        }

        @SneakyThrows
        public static Token parseToken(String token) {
            return new ObjectMapper().readValue(Base64.getDecoder().decode(token), Token.class);
        }

        @SneakyThrows
        public String generate() {
            return Base64.getEncoder().encodeToString(new ObjectMapper().writeValueAsBytes(this));
        }
    }
}
