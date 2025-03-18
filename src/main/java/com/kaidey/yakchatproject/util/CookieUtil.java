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


    public void addAccessToken(HttpServletResponse response, String token, HttpServletRequest request) {
        if (getAccessToken(request).isPresent()) {
            deleteAccessToken(response);
        }
        addCookie(response, "access_token", token, ACCESS_TOKEN_EXPIRATION);
    }


    public void addRefreshToken(HttpServletResponse response, String token) {
        deleteRefreshToken(response); // 기존 쿠키 삭제
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

    public void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }


    public void deleteAccessToken(HttpServletResponse response) {
        deleteCookie(response, "access_token");
    }

    public void deleteRefreshToken(HttpServletResponse response) {
        deleteCookie(response, "refresh_token");
    }
}
