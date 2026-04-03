package com.candle.larktalk.controller;

import com.candle.larktalk.model.User;
import com.candle.larktalk.repository.ChannelRepository;
import com.candle.larktalk.repository.UserChannelAccessRepository;
import com.candle.larktalk.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ChannelController {
    private final UserRepository userRepository;
    private final UserChannelAccessRepository accessRepository;

    record ChannelDto(Long id, String name, String description) {}

    @RequestMapping("/channels/my")
    public ResponseEntity<?> getMyChannels(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer fake-jwt-token-for-")) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }

        String login = authHeader.replace("Bearer fake-jwt-token-for-", "");

        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "User not found"));
        }
        User user = userOpt.get();

        List<ChannelDto> channels = accessRepository.findByUserId(user.getId()).stream()
                .map(uca -> new ChannelDto(uca.getChannel().getId(),
                        uca.getChannel().getName(),
                        uca.getChannel().getDescription())).toList();

        return ResponseEntity.ok(channels);
    }
}
