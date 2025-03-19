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
        cookie.setMaxAge((int) (maxAge / 1000));
//        cookie.setAttribute("SameSite", "Strict");
//        response.setHeader("Set-Cookie", name + "=" + value + "; Path=/; HttpOnly; Secure; SameSite=None");

//        response.addCookie(cookie);
        response.addHeader("Set-Cookie", name + "=" + value + "; Path=/; HttpOnly; Secure; SameSite=None");
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

    public void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.setHeader("Set-Cookie", name + "=; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=0");
//        response.addHeader("Set-Cookie", name + "=; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=0");
        response.addCookie(cookie);
    }

    public void deleteAccessToken(HttpServletResponse response) {
        deleteCookie(response, "access_token");
    }

    public void deleteRefreshToken(HttpServletResponse response) {
        deleteCookie(response, "refresh_token");
    }

    private boolean isProductionEnvironment() {
        String environment = System.getenv("ENVIRONMENT"); // 환경 변수에서 값 가져오기
        return environment != null && environment.equals("production"); // 배포 환경이면 Secure=true
    }
}


