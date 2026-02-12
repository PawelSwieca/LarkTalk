package com.candle.larktalk.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "channels")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "password_hash")
    private String passwordHash;

    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
