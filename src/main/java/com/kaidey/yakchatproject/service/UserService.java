package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.UserDto;
import com.kaidey.yakchatproject.entity.User;
import com.kaidey.yakchatproject.repository.UserRepository;
import com.kaidey.yakchatproject.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.kaidey.yakchatproject.entity.enums.RoleType;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 사용자 등록
    public User registerUser(UserDto userDto) {
        try {
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setPassword(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
            user.setSchool(userDto.getSchool());
            user.setGrade(userDto.getGrade());
            user.setAge(userDto.getAge());

            Set<RoleType> roles = new HashSet<>();
            roles.add(RoleType.ROLE_USER); // 기본적으로 USER 역할 부여
            user.setRoles(roles);

            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error registering user: " + e.getMessage());
        }
    }
    // 사용자 로그인
    public String loginUser(UserDto userDto) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(userDto.getUsername());
            if (userOptional.isPresent() && passwordEncoder.matches(userDto.getPassword(), userOptional.get().getPassword())) {
                return jwtTokenProvider.generateToken(userDto.getUsername(), userOptional.get().getId());
            } else {
                throw new RuntimeException("Invalid username or password");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error logging in user: " + e.getMessage());
        }
    }


    // 특정 사용자 조회
    public User getUserById(Long id) {
        try {
            return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving user: " + e.getMessage());
        }
    }

    // 모든 사용자 조회
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all users: " + e.getMessage());
        }
    }

    // 사용자 정보 업데이트
    public User updateUser(Long id, UserDto userDto) {
        try {
            User user = getUserById(id);
            user.setUsername(userDto.getUsername());
            user.setPassword(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
            user.setSchool(userDto.getSchool());
            user.setGrade(userDto.getGrade());
            user.setAge(userDto.getAge());
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }

    // 사용자 삭제
    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage());
        }
    }
}