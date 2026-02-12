package com.candle.larktalk.repository;

import com.candle.larktalk.model.UserChannelAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChannelAccessRepository extends JpaRepository<UserChannelAccess, Long> {

    Optional<UserChannelAccess> findByUserIdAndChannelId(Long userId, Long channelId);

    List<UserChannelAccess> findByUserId(Long userId);
}
