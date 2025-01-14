package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.UserDto;
import com.kaidey.yakchatproject.entity.User;
import com.kaidey.yakchatproject.repository.UserRepository;
import com.kaidey.yakchatproject.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 빈

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // JWT 토큰 생성기

    public UserDto registerUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
        user.setSchool(userDto.getSchool());
        user.setGrade(userDto.getGrade());
        user.setAge(userDto.getAge());
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true); // 기본적으로 활성화 상태

        userRepository.save(user);
        return userDto; // 혹은 변환된 UserDto를 반환
    }

    public Optional<String> login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                user.updateLastLogin(); // 마지막 로그인 시간 업데이트
                userRepository.save(user); // 업데이트된 사용자 저장
                return Optional.of(jwtTokenProvider.generateToken(user.getUsername())); // JWT 토큰 반환
            }
        }
        return Optional.empty(); // 로그인 실패
    }
}
