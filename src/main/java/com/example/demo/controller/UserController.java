package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/register-step1")
    public ResponseEntity<String> registerStep1(@RequestBody Step1Request request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsComplete(false);
        user.setCreatedAt(Instant.now());
        userRepo.save(user);
        return ResponseEntity.ok("Step 1 completed");
    }

    @PutMapping("/register-step2")
    public ResponseEntity<String> registerStep2(@RequestBody Step2Request request) {
        Optional<User> userOptional = userRepo.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOptional.get();
        if (user.isComplete()) {
            return ResponseEntity.badRequest().body("Registration already completed");
        }
        user.setBirthdate(request.getBirthdate());
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());
        user.setIsComplete(true);
        userRepo.save(user);
        return ResponseEntity.ok("Step 2 completed");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepo.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        User user = userOptional.get();

        if (!user.isComplete()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please complete registration");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        return ResponseEntity.ok(new UserResponse(user.getEmail(), user.getQrCode(), user.getBonusPoints()));
    }
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestParam String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOptional.get();

        UserInfoResponse info = new UserInfoResponse(
                user.getId(),
                user.getFullName(),
                user.getBirthdate(),
                user.getPhone(),
                user.getBonusPoints()
        );
        return ResponseEntity.ok(info);
    }

    public static class UserInfoResponse {
        private Long id;
        private String fullName;
        private String birthdate;
        private String phone;
        private int bonusPoints;

        public UserInfoResponse(Long id, String fullName, String birthdate, String phone, int bonusPoints) {
            this.id = id;
            this.fullName = fullName;
            this.birthdate = birthdate;
            this.phone = phone;
            this.bonusPoints = bonusPoints;
        }

        public Long getId() { return id; }
        public String getFullName() { return fullName; }
        public String getBirthdate() { return birthdate; }
        public String getPhone() { return phone; }
        public int getBonusPoints() { return bonusPoints; }
    }

    @PutMapping("/bonus")
    public ResponseEntity<String> updateBonusPoints(@RequestBody BonusUpdateRequest request) {
        Optional<User> userOptional = userRepo.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOptional.get();
        user.setBonusPoints(request.getBonusPoints());
        userRepo.save(user);
        return ResponseEntity.ok("Bonus points updated");
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void cleanupUnfinishedRegistrations() {
        userRepo.findAll().stream()
                .filter(user -> !user.isComplete())
                .filter(user -> user.getCreatedAt() != null &&
                        Duration.between(user.getCreatedAt(), Instant.now()).toHours() >= 24)
                .forEach(userRepo::delete);
    }

    public static class UserResponse {
        private String  email;
        private String qrCode;
        private int bonusPoints;

        public UserResponse(String email, String qrCode, int bonusPoints) {
            this.email = email;
            this.qrCode = qrCode;
            this.bonusPoints = bonusPoints;
        }

        public String getEmail() { return email; }
        public String getQrCode() { return qrCode; }
        public int getBonusPoints() { return bonusPoints; }
    }

    public static class Step1Request {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class Step2Request {
        private String email;
        private String birthdate;
        private String phone;
        private String fullName;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getBirthdate() { return birthdate; }
        public void setBirthdate(String birthdate) { this.birthdate = birthdate; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class BonusUpdateRequest {
        private String email;
        private int bonusPoints;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public int getBonusPoints() { return bonusPoints; }
        public void setBonusPoints(int bonusPoints) { this.bonusPoints = bonusPoints; }
    }
}