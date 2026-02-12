package com.candle.larktalk.csv;

import com.candle.larktalk.model.*;
import com.candle.larktalk.repository.*;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Component
public class CsvDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ChannelRepository channelRepository;
    private final UserChannelAccessRepository userChannelAccessRepository;
    private final ChannelSettingRepository channelSettingRepository;
    private final MessageRepository messageRepository;

    private static final Logger log = LoggerFactory.getLogger(CsvDataLoader.class);

    public CsvDataLoader(UserRepository userRepository, RoleRepository roleRepository, ChannelRepository channelRepository,
                         UserChannelAccessRepository userChannelAccessRepository, ChannelSettingRepository channelSettingRepository,
                         MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.channelRepository = channelRepository;
        this.userChannelAccessRepository = userChannelAccessRepository;
        this.channelSettingRepository = channelSettingRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) return;

        // Dummy maps (CSV ID -> DB Entities) - to solve PostgreSQL ID Mapping
        Map<Long, Role> rolesMap = new HashMap<>();
        Map<Long, User> usersMap = new HashMap<>();
        Map<Long, Channel> channelsMap = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        List<String[]> roleRows = loadData("role.csv");
        for (String[] row : roleRows) {
            Role role = new Role();
            role.setName(row[1]);
            role.setDescription(row[2]);

            Role savedRole = roleRepository.save(role);
            rolesMap.put(Long.parseLong(row[0]), savedRole);
        }


        List<String[]> userRows = loadData("user.csv");
        for (String[] row : userRows) {
            User user = new User();
            user.setLogin(row[1]);
            user.setNickname(row[2]);
            user.setPasswordHash(row[3]);
            user.setEmail(row[4]);
            user.setCreatedAt(LocalDateTime.parse(row[5], formatter));
            user.setLastLogin(LocalDateTime.parse(row[6], formatter));

            User savedUser = userRepository.save(user);
            usersMap.put(Long.parseLong(row[0]), savedUser);
        }

        List<String[]> channelRows = loadData("channel.csv");
        for (String[] row : channelRows) {
            Channel channel = new Channel();
            channel.setName(row[1]);
            channel.setPasswordHash(row[2]);
            channel.setDescription(row[3]);
            channel.setCreatedAt(LocalDateTime.parse(row[4], formatter));

            Channel savedChannel = channelRepository.save(channel);
            channelsMap.put(Long.parseLong(row[0]), savedChannel);
        }


        List<String[]> userRoleRows = loadData("user_role.csv");
        for (String[] row : userRoleRows) {
            Long csvUserId = Long.parseLong(row[1]);
            Long csvRoleId = Long.parseLong(row[2]);

            User user = usersMap.get(csvUserId);
            Role role = rolesMap.get(csvRoleId);

            if (user != null && role != null) {
                user.getRoles().add(role);
                userRepository.save(user);
            }
        }


        List<String[]> settingRows = loadData("channel_setting.csv");
        for (String[] row : settingRows) {
            Long csvChannelId = Long.parseLong(row[3]);
            Channel channel = channelsMap.get(csvChannelId);

            if (channel != null) {
                ChannelSetting setting = new ChannelSetting();
                setting.setSettingKey(row[1]);
                setting.setSettingValue(row[2]);
                setting.setChannel(channel);
                channelSettingRepository.save(setting);
            }
        }

        List<String[]> accessRows = loadData("user_channel_access.csv");
        for (String[] row : accessRows) {
            Long csvUserId = Long.parseLong(row[2]);
            Long csvChannelId = Long.parseLong(row[3]);

            User user = usersMap.get(csvUserId);
            Channel channel = channelsMap.get(csvChannelId);

            if (user != null && channel != null) {
                UserChannelAccess access = new UserChannelAccess();
                access.setJoinedAt(LocalDateTime.parse(row[1], formatter));
                access.setUser(user);
                access.setChannel(channel);
                userChannelAccessRepository.save(access);
            }
        }

        List<String[]> messageRows = loadData("messages.csv");
        for (String[] row : messageRows) {
            // CSV: id, sender_id, channel_id, content, created_at
            Long csvSenderId = Long.parseLong(row[1]);
            Long csvChannelId = Long.parseLong(row[2]);

            User sender = usersMap.get(csvSenderId);
            Channel channel = channelsMap.get(csvChannelId);

            if (sender != null && channel != null) {
                Message msg = new Message();
                msg.setContent(row[3]);
                msg.setTimestamp(LocalDateTime.parse(row[4], formatter));
                msg.setSender(sender);
                msg.setChannel(channel);

                messageRepository.save(msg);
            } else {
                log.warn("Pominięto wiadomość ID: {} - brak nadawcy lub kanału", row[0]);
            }
        }
        log.info("Loaded {} messages.", messageRows.size());

        System.out.println("--- LOADING DATA SUCCEEDED ---");
    }

    private List<String[]> loadData(String fileName) {
        try (
                Reader reader = new InputStreamReader(new ClassPathResource("data/" + fileName).getInputStream());
                CSVReader csvReader = new CSVReaderBuilder(reader)
                        .withCSVParser(new CSVParserBuilder().withSeparator(',').build())
                        .withSkipLines(1) // Skip header
                        .build()
        ) {
            return csvReader.readAll();
        } catch (IOException | CsvException e) {
            log.error("CSV reading error: {}", fileName, e);
            throw new RuntimeException("Loading data failed: " + fileName, e);
        }
    }
}

