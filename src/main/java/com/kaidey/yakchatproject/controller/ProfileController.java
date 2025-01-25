package com.kaidey.yakchatproject.controller;

import com.kaidey.yakchatproject.dto.ProfileDto;
import com.kaidey.yakchatproject.dto.ImageDto;
import com.kaidey.yakchatproject.service.ProfileService;
import com.kaidey.yakchatproject.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public ProfileController(ProfileService profileService, JwtTokenProvider jwtTokenProvider) {
        this.profileService = profileService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 프로필 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable Long userId) {
        ProfileDto profile = profileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    // 자기 자신의 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<ProfileDto> getMyProfile(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        ProfileDto profile = profileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    // 프로필 업데이트 (이미지 및 기타 데이터)
    @PutMapping("/update")
    public ResponseEntity<ProfileDto> updateProfile(@RequestBody ProfileDto profileDto,
                                                    @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));  // 'Bearer ' 부분 제거
            ProfileDto updatedProfile = profileService.updateProfile(userId, profileDto);
            return ResponseEntity.ok(updatedProfile);  // 업데이트된 프로필 반환
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).build();  // 잘못된 요청 처리
        }
    }

}
