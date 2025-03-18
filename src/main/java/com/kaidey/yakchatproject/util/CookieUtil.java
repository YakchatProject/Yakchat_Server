package com.kaidey.yakchatproject.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Optional;

@Component
public class CookieUtil {

    @Value("${jwt.expiration}")
    private long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.refreshExpiration}")
    private long REFRESH_TOKEN_EXPIRATION;

    public void addCookie(HttpServletResponse response, String name, String value, long maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (maxAge / 1000)); // JWT 만료 시간이 밀리초라면 유지, 아니라면 수정 필요

        response.addCookie(cookie);


    }


    public void addAccessToken(HttpServletResponse response, String token) {
        addCookie(response, "access_token", token, ACCESS_TOKEN_EXPIRATION);
    }

    public void addRefreshToken(HttpServletResponse response, String token) {
        addCookie(response, "refresh_token", token, REFRESH_TOKEN_EXPIRATION);
    }


    public Optional<String> getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> name.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst();
        }
        return Optional.empty();
    }

    public Optional<String> getAccessToken(HttpServletRequest request) {
        return getCookieValue(request, "access_token");
    }

    public Optional<String> getRefreshToken(HttpServletRequest request) {
        return getCookieValue(request, "refresh_token");
    }

    public void deleteCookieIfExists(HttpServletResponse response, String name, HttpServletRequest request) {
        if (getCookieValue(request, name).isPresent()) {
            Cookie cookie = new Cookie(name, "");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    public void deleteAccessToken(HttpServletResponse response, HttpServletRequest request) {
        deleteCookieIfExists(response, "access_token", request);
    }

    public void deleteRefreshToken(HttpServletResponse response, HttpServletRequest request) {
        deleteCookieIfExists(response, "refresh_token", request);
    }

    private boolean isProductionEnvironment() {
        String environment = System.getenv("ENVIRONMENT"); // 환경 변수에서 값 가져오기
        return environment != null && environment.equals("production"); // 배포 환경이면 Secure=true
    }
}


