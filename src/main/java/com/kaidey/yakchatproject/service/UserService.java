package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.UserDto;
import com.kaidey.yakchatproject.entity.User;
import com.kaidey.yakchatproject.repository.UserRepository;
import com.kaidey.yakchatproject.security.JwtTokenProvider;
import com.kaidey.yakchatproject.entity.enums.RoleType;
import com.kaidey.yakchatproject.entity.enums.GradeType;
import com.kaidey.yakchatproject.repository.UserGradeRepository;
import com.kaidey.yakchatproject.entity.UserGrade;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserGradeRepository userGradeRepository;
    private final GradeService gradeService;


    public UserService(UserRepository userRepository, GradeService gradeService,
                       PasswordEncoder passwordEncoder,JwtTokenProvider jwtTokenProvider,
                       UserGradeRepository userGradeRepository) {
        this.userRepository = userRepository;
        this.gradeService = gradeService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userGradeRepository = userGradeRepository;
    }

    // 사용자 등록
    @Transactional
    public User registerUser(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
        user.setSchool(userDto.getSchool());
        user.setGrade(userDto.getGrade());
        user.setAge(userDto.getAge());

        Set<RoleType> roles = new HashSet<>();
        roles.add(RoleType.ROLE_USER); // 기본적으로 USER 역할 부여
        user.setRoles(roles);

        //  유저 저장 후 UserGrade 자동 생성
        User savedUser = userRepository.save(user);
        createUserGrade(savedUser);

        return savedUser;
    }

    // 사용자 로그인
    public Map<String, String> loginUser(UserDto userDto) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(userDto.getUsername());
            if (userOptional.isPresent() && passwordEncoder.matches(userDto.getPassword(), userOptional.get().getPassword())) {
                String token = jwtTokenProvider.generateToken(userDto.getUsername(), userOptional.get().getId());
                String refreshToken = jwtTokenProvider.generateRefreshToken(userDto.getUsername(), userOptional.get().getId());
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", token);
                tokens.put("refresh_token", refreshToken);
                return tokens;
            } else {
                throw new RuntimeException("Invalid username or password");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error logging in user: " + e.getMessage());
        }
    }




    // 토큰 갱신
    public Map<String, String> refreshToken(String refreshToken) {
        // 리프레시 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 리프레시 토큰에서 사용자 정보 추출
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // 새로운 액세스 토큰과 리프레시 토큰 생성
        String newAccessToken = jwtTokenProvider.generateToken(username, userId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username, userId);

        // 새로운 토큰들을 반환
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return tokens;
    }

    // 사용자 이름 중복 체크
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
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
            User user = getUserById(id);
            userGradeRepository.deleteById(user.getId()); // UserGrade도 함께 삭제
            userRepository.delete(user);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage());
        }
    }

    @Transactional
    public void updateUserActivity(User user, int questions, int accepted, int likes, int purchases, int sales) {
        UserGrade userGrade = userGradeRepository.findByUserId(user.getId())
                .orElseGet(() -> createUserGrade(user)); // userGrade가 없으면 생성

        userGrade.setQuestionCount(userGrade.getQuestionCount() + questions);
        userGrade.setAcceptedCount(userGrade.getAcceptedCount() + accepted);
        userGrade.setLikeCount(userGrade.getLikeCount() + likes);
        userGrade.setPurchasedMaterialCount(userGrade.getPurchasedMaterialCount() + purchases);
        userGrade.setSoldMaterialCount(userGrade.getSoldMaterialCount() + sales);

        // 등급 업데이트
        gradeService.updateUserGrade(userGrade);
        userGradeRepository.save(userGrade);
    }

    private UserGrade createUserGrade(User user) {
        UserGrade newUserGrade = new UserGrade();
        newUserGrade.setUser(user);
        newUserGrade.setGrade(GradeType.GRAY);
        newUserGrade.setQuestionCount(0);
        newUserGrade.setAcceptedCount(0);
        newUserGrade.setLikeCount(0);
        newUserGrade.setPurchasedMaterialCount(0);
        newUserGrade.setSoldMaterialCount(0);

        return userGradeRepository.save(newUserGrade);
    }
}
