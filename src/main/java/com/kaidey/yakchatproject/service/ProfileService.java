package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.ProfileDto;
import com.kaidey.yakchatproject.entity.User;
import com.kaidey.yakchatproject.exception.EntityNotFoundException;
import com.kaidey.yakchatproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kaidey.yakchatproject.dto.ImageDto;
import com.kaidey.yakchatproject.entity.Image;
import com.kaidey.yakchatproject.util.ImageUtils;
import org.apache.tika.mime.MimeTypeException;
import java.util.List;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final ImageUtils imageUtils;

    @Autowired
    public ProfileService(UserRepository userRepository, ImageUtils imageUtils) {
        this.userRepository = userRepository;
        this.imageUtils = imageUtils;
    }

    @Transactional
    public ProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ProfileDto profileDto = new ProfileDto();
        profileDto.setUsername(user.getUsername());
        profileDto.setSchool(user.getSchool());
        profileDto.setGrade(user.getGrade());
        profileDto.setAge(user.getAge());
        profileDto.setProfileImageUrl(user.getProfileImageUrl());

        return profileDto;
    }

    @Transactional
    public ProfileDto updateProfile(Long userId, ProfileDto profileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 프로필 이미지 업데이트 처리
        if (profileDto.getProfileImage() != null) {
            try {
                List<ImageDto> imageDtos = imageUtils.processImages(List.of(profileDto.getProfileImage()));
                if (!imageDtos.isEmpty()) {
                    String imageUrl = imageDtos.get(0).getUrl();
                    user.setProfileImageUrl(imageUrl);
                    userRepository.save(user);
                } else {
                    throw new RuntimeException("Failed to save profile image");
                }
            } catch (MimeTypeException e) {
                throw new RuntimeException("Error saving profile image", e);
            }
        }

        // 나머지 프로필 데이터 업데이트
        user.setUsername(profileDto.getUsername());
        user.setSchool(profileDto.getSchool());
        user.setGrade(profileDto.getGrade());
        user.setAge(profileDto.getAge());

        userRepository.save(user);

        return profileDto; // 프로필 업데이트 후 반환
    }
}
