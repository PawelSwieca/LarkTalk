package com.candle.larktalk.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "user_channel_access")
public class UserChannelAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}
