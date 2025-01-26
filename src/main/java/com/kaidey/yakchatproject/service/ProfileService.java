package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.ProfileDto;
import com.kaidey.yakchatproject.entity.User;
import com.kaidey.yakchatproject.exception.EntityNotFoundException;
import com.kaidey.yakchatproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import com.kaidey.yakchatproject.dto.ImageDto;
import com.kaidey.yakchatproject.entity.Image;
import com.kaidey.yakchatproject.util.ImageUtils;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final ImageUtils imageUtils = new ImageUtils();

    @Value("${upload.dir}")
    private String uploadDir;

    @Autowired
    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;

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

        if (profileDto.getImages() != null && !profileDto.getImages().isEmpty()) {
            for (ImageDto imageDto : profileDto.getImages()) {
                Image image = new Image();
                image.setUrl(imageDto.getUrl());
                image.setFileName(imageDto.getFileName());
                image.setUser(user);
                user.getImages().add(image);
            }
        }

        return profileDto;
    }

    @Transactional
    public ProfileDto updateProfile(Long userId, ProfileDto profileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user details
        user.setUsername(profileDto.getUsername());
        user.setSchool(profileDto.getSchool());
        user.setGrade(profileDto.getGrade());
        user.setAge(profileDto.getAge());

        // Handle profile image
        if (profileDto.getImages() != null && !profileDto.getImages().isEmpty()) {
            user.getImages().clear();

            for (ImageDto imageDto : profileDto.getImages()) {
                Image image = new Image();
                System.out.println(image);
                image.setUrl(imageDto.getUrl());
                image.setFileName(imageDto.getFileName());
                image.setUser(user);
                user.getImages().add(image);
            }
        }



        userRepository.save(user);
        profileDto.setId(user.getId());
        return profileDto;
    }


}
