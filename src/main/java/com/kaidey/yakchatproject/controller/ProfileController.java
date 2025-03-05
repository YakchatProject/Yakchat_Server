package com.kaidey.yakchatproject.controller;

import com.kaidey.yakchatproject.dto.ProfileDto;
import com.kaidey.yakchatproject.service.ProfileService;
import com.kaidey.yakchatproject.security.JwtTokenProvider;
import com.kaidey.yakchatproject.util.ImageUtils;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ImageUtils imageUtils;

    @Autowired
    public ProfileController(ProfileService profileService, JwtTokenProvider jwtTokenProvider, ImageUtils imageUtils) {
        this.profileService = profileService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.imageUtils = imageUtils;
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
    @PutMapping(value = "/update", consumes = {"multipart/form-data"})
    public ResponseEntity<ProfileDto> updateProfile(
            @RequestParam(value = "username",required = false) String username,
            @RequestParam(value = "school",required = false) String school,
            @RequestParam(value = "grade",required = false) String grade,
            @RequestParam(value = "age",required = false) Integer age,
            @RequestParam(value = "images") List<String> images,
            @RequestHeader("Authorization") String token) {

        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        ProfileDto profileDto = new ProfileDto();
        profileDto.setUsername(username);
        profileDto.setSchool(school);
        profileDto.setGrade(grade);
        profileDto.setAge(age);

        if (images != null && !images.isEmpty()) {
            try {
                profileDto.setImages(imageUtils.processImages(images));
            } catch (MimeTypeException | IllegalArgumentException e) {
                return ResponseEntity.status(e instanceof MimeTypeException ? 500 : 400).body(null);
            }
        }

        ProfileDto updatedProfile = profileService.updateProfile(userId, profileDto);
        return ResponseEntity.ok(updatedProfile);
    }

}
