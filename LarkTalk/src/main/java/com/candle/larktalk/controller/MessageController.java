package com.candle.larktalk.controller;

import com.candle.larktalk.model.Channel;
import com.candle.larktalk.model.Message;
import com.candle.larktalk.model.MessageType;
import com.candle.larktalk.model.User;
import com.candle.larktalk.repository.ChannelRepository;
import com.candle.larktalk.repository.MessageRepository;
import com.candle.larktalk.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public MessageController(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    record MessageDto(String content, String timestamp, String userName, String channelName) {}
    record MessageRequest(Long chatId, String content) {}


    @GetMapping("/messages")
    public ResponseEntity<?> getMessages(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam Long chatId) {

        if (authHeader == null || !authHeader.startsWith("Bearer fake-jwt-token-for-")) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }

        String login = authHeader.replace("Bearer fake-jwt-token-for-", "");
        System.out.println("User " + login + " pobiera historię czatu nr " + chatId);

        List<Message> messages = messageRepository.findByChannelIdOrderByTimestampAsc(chatId);

        List<MessageDto> response = messages.stream()
                .map(m -> new MessageDto(
                        m.getContent(),
                        m.getTimestamp().toString(),
                        m.getSender().getLogin(),
                        m.getChannel().getName()
                )).toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/messages")
    public ResponseEntity<?> saveMessage(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody MessageRequest request) {
        if (authHeader == null || !authHeader.startsWith("Bearer fake-jwt-token-for-")) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }

        String login = authHeader.replace("Bearer fake-jwt-token-for-", "");

        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "User not found"));
        }

        User sender = userOpt.get();
        Optional<Channel> channelOpt = channelRepository.findById(request.chatId());

        if (channelOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Channel not found"));
        }

        Channel channel = channelOpt.get();

        Message message = new Message();
        message.setContent(request.content());
        message.setSender(sender);
        message.setChannel(channel);
        message.setType(MessageType.TEXT);
        message.setTimestamp(LocalDateTime.now());

        Message savedMessage = messageRepository.save(message);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "messageId", savedMessage.getId(),
                "timestamp", savedMessage.getTimestamp().toString()
        ));
    }
}