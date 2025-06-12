package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        Optional<User> userOptional = userRepo.findByUsername(loginRequest.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        return ResponseEntity.ok(new UserResponse(user.getUsername(), user.getQrCode(), user.getBonusPoints()));
    }

    @PutMapping("/bonus")
    public ResponseEntity<String> updateBonusPoints(@RequestBody BonusUpdateRequest request) {
        Optional<User> userOptional = userRepo.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();
        user.setBonusPoints(request.getBonusPoints());
        userRepo.save(user);

        return ResponseEntity.ok("Bonus points updated");
    }

    // DTO для відповіді при логіні
    public static class UserResponse {
        private String username;
        private String qrCode;
        private int bonusPoints;

        public UserResponse(String username, String qrCode, int bonusPoints) {
            this.username = username;
            this.qrCode = qrCode;
            this.bonusPoints = bonusPoints;
        }

        public String getUsername() { return username; }
        public String getQrCode() { return qrCode; }
        public int getBonusPoints() { return bonusPoints; }
    }

    // DTO для оновлення бонусів
    public static class BonusUpdateRequest {
        private String username;
        private int bonusPoints;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public int getBonusPoints() { return bonusPoints; }
        public void setBonusPoints(int bonusPoints) { this.bonusPoints = bonusPoints; }
    }
}
