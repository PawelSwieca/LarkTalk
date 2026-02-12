package com.candle.larktalk.controller;

import com.candle.larktalk.model.User;
import com.candle.larktalk.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    record LoginRequest(String login, String password) {}

    record UserProfileDto(String login, String nickname, String email, String createdAt) {}


    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        if (userRepository.existsByLogin(request.login())) {
            return ResponseEntity.badRequest().body("Login already exists");
        }

        User user = new User();
        user.setLogin(request.login());

        user.setPasswordHash(passwordEncoder.encode(request.password()));
        // to be continued...


        userRepository.save(user);
        return ResponseEntity.ok("Saved!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByLogin(request.login());

        if (userOpt.isPresent()) {
            User user = userOpt.get();


            if (passwordEncoder.matches(request.password(), user.getPasswordHash())) {


                return ResponseEntity.ok(Map.of(
                        "token", "fake-jwt-token-for-" + user.getLogin(),
                        "username", user.getLogin()
                ));
            }
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid login or password!"));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam String login) {

        Optional<User> userOpt = userRepository.findByLogin(login);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return ResponseEntity.ok(new UserProfileDto(
                    user.getLogin(),
                    user.getNickname(),
                    user.getEmail(),
                    user.getCreatedAt().toString().replace(" ", "T")
            ));
        }

        return ResponseEntity.status(404).body("That user doesn't exist");
    }
}