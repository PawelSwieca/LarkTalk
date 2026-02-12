package com.candle.larktalk.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "channel_settings")
public class ChannelSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "setting_key")
    private String settingKey; // np. "max_occupancy"

    @Column(name = "setting_value")
    private String settingValue; // np. "100"


    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;
}
