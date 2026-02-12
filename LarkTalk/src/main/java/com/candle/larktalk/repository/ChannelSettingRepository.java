package com.candle.larktalk.repository;

import com.candle.larktalk.model.ChannelSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChannelSettingRepository extends JpaRepository<ChannelSetting, Long> {
    // 1. Get all settings of channel
    List<ChannelSetting> findByChannelId(Long channelId);

    // 2. Get specified setting of channel
    Optional<ChannelSetting> findByChannelIdAndSettingKey(Long channelId, String settingKey);
}
