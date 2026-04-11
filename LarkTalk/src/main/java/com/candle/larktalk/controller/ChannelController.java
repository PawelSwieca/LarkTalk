package com.candle.larktalk.controller;

import com.candle.larktalk.model.ChannelSetting;
import com.candle.larktalk.model.User;
import com.candle.larktalk.model.UserChannelAccess;
import com.candle.larktalk.repository.ChannelSettingRepository;
import com.candle.larktalk.repository.UserChannelAccessRepository;
import com.candle.larktalk.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ChannelController {
    private final UserRepository userRepository;
    private final UserChannelAccessRepository accessRepository;
    private final ChannelSettingRepository channelSettingRepository;

    record ChannelDto(Long id, String name, String description) {
    }

    @GetMapping("/channels/my")
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

        List<UserChannelAccess> userAccess = accessRepository.findByUserId(user.getId());

        List<ChannelDto> channels = new ArrayList<>();

        for (UserChannelAccess access : userAccess) {
            boolean isActive = true;
            int maxOccupancy = 1000;

            for (ChannelSetting setting : channelSettingRepository.findByChannelId(access.getChannel().getId())) {
                if("active".equals(setting.getSettingKey()) && "False".equals(setting.getSettingValue())) {
                    isActive = false;
                }
                if("max_occupancy".equals(setting.getSettingKey())) {
                    maxOccupancy = Integer.parseInt(setting.getSettingValue());
                }
            }

            long currentUsersCount = accessRepository.countByChannelId(access.getChannel().getId());
            if (isActive && currentUsersCount < maxOccupancy) {
                channels.add(new ChannelDto(access.getChannel().getId(),
                        access.getChannel().getName(),
                        access.getChannel().getDescription()
                ));
            }
        }


        return ResponseEntity.ok(channels);
    }
}
