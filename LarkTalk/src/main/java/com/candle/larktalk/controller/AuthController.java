package com.candle.larktalk.controller;

import com.candle.larktalk.model.*;
import com.candle.larktalk.repository.*;
import com.candle.larktalk.request.UserRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ChannelRepository channelRepository;
    private final UserChannelAccessRepository accessRepository;
    private final MessageRepository messageRepository;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository, ChannelRepository channelRepository,
                          UserChannelAccessRepository accessRepository,
                          MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.channelRepository = channelRepository;
        this.accessRepository = accessRepository;
        this.messageRepository = messageRepository;
    }

    record LoginRequest(String login, String password) {}

    record UserProfileDto(String login, String nickname, String email, String createdAt, String roles) {}

    record MessageRequest(Long chatId, String content) {}


    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<?> register(@RequestBody UserRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            return ResponseEntity.badRequest().body("Login already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setLogin(request.getLogin());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));


        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());

        Role userRole = roleRepository.findByName("user")
                .orElseThrow(() -> new RuntimeException("Error: Role 'user' is not found."));
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));


        User savedUser = userRepository.save(user);


        Channel defaultChannel = channelRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Error: Default channel (ID=1) not found."));


        UserChannelAccess access = new UserChannelAccess();
        access.setUser(savedUser);
        access.setChannel(defaultChannel);
        access.setJoinedAt(LocalDateTime.now());

        accessRepository.save(access);

        return ResponseEntity.ok("User registered and joined default channel!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByLogin(request.login());

        if (userOpt.isPresent()) {
            User user = userOpt.get();


            if (passwordEncoder.matches(request.password(), user.getPasswordHash())) {


                return ResponseEntity.ok(Map.of(
                        "token", "fake-jwt-token-for-" + user.getLogin(),
                        "username", user.getLogin(),
                        "nickname", user.getNickname()

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
                    user.getCreatedAt().toString().replace(" ", "T"),
                    user.getRoles().stream().map(Role::getName).collect(Collectors.joining())
            ));
        }

        return ResponseEntity.status(404).body("That user doesn't exist");
    }
    @PostMapping("/messages")
    public ResponseEntity<?> saveMessage(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody MessageRequest request
    ) {

        if (authHeader == null || !authHeader.startsWith("Bearer fake-jwt-token-for-")) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }

        String login = authHeader.replace("Bearer fake-jwt-token-for-", "");

        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "User not found"));
        }

        User sender = userOpt.get();

        System.out.println("User " + sender.getLogin() + " sends a message: " + request.content());

        Optional<Channel> channelOpt = channelRepository.findById(request.chatId());
        if (channelOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Channel not found"));
        }
        Channel channel = channelOpt.get();

        Message message = new Message();
        message.setContent(request.content());
        message.setSender(sender);
        message.setChannel(channel);
        message.setType(MessageType.TEXT); // Now we send text only!
        message.setTimestamp(LocalDateTime.now());

        Message savedMessage = messageRepository.save(message);

        System.out.println("Message send to: " + channel.getName() + " from: " + sender.getLogin());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "messageId", savedMessage.getId(),
                "timestamp", savedMessage.getTimestamp().toString()
        ));
    }
}