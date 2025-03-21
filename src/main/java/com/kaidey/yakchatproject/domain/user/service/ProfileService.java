package com.kaidey.yakchatproject.domain.user.service;

import com.kaidey.yakchatproject.domain.user.dto.ProfileDto;
import com.kaidey.yakchatproject.domain.user.entity.User;
import com.kaidey.yakchatproject.global.exception.EntityNotFoundException;
import com.kaidey.yakchatproject.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import com.kaidey.yakchatproject.domain.image.dto.ImageDto;
import com.kaidey.yakchatproject.domain.image.entity.Image;
import com.kaidey.yakchatproject.domain.image.util.ImageUtils;



@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final ImageUtils imageUtils;

    @Autowired
    public ProfileService(UserRepository userRepository, ImageUtils imageUtils) {
        this.userRepository = userRepository;
        this.imageUtils = imageUtils;
    }
    @Value("${upload.dir}")
    private String uploadDir;



    @Transactional
    public ProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return convertToProfileDto(user);
    }



    @Transactional
    public ProfileDto updateProfile(Long userId, ProfileDto profileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (profileDto.getImages() != null && !profileDto.getImages().isEmpty()) {
            user.getImages().clear();
            for (ImageDto imageDto : profileDto.getImages()) {
                Image image = new Image();
                image.setUrl(imageDto.getUrl());
                image.setFileName(imageDto.getFileName());
                image.setUser(user);
                user.getImages().add(image);
            }
        }

        userRepository.save(user);
        return convertToProfileDto(user);
    }


    private ProfileDto convertToProfileDto(User user) {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setId(user.getId());
        profileDto.setUsername(user.getUsername());
        profileDto.setSchool(user.getSchool());
        profileDto.setGrade(user.getGrade());
        profileDto.setAge(user.getAge());
        profileDto.setImages(imageUtils.convertToImageDtos(user.getImages()));
        return profileDto;
    }





}
