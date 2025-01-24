package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.ProfileDto;
import com.kaidey.yakchatproject.entity.User;
import com.kaidey.yakchatproject.exception.EntityNotFoundException;
import com.kaidey.yakchatproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    @Autowired
    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public ProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        ProfileDto profileDto = new ProfileDto();
        profileDto.setUsername(user.getUsername());
        profileDto.setSchool(user.getSchool());
        profileDto.setGrade(user.getGrade());
        profileDto.setAge(user.getAge());
        profileDto.setProfileImage(user.getProfileImage());
        return profileDto;
    }

    @Transactional
    public void uploadProfileImage(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setProfileImage(file.getBytes());
        userRepository.save(user);
    }
}