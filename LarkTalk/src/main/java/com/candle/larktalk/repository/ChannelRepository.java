package com.candle.larktalk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.candle.larktalk.model.Channel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {

     Optional<Channel> findByName(String name);
}
