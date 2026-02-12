package com.candle.larktalk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.candle.larktalk.model.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {


    List<Message> findByChannelId(Long channelId);

    List<Message> findByChannelIdOrderByTimestampAsc(Long channelId);
}

