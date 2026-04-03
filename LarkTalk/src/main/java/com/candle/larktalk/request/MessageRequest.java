package com.candle.larktalk.request;

import lombok.Data;

@Data
public class MessageRequest {

    private String content;

    private String timestamp;

    private String userName;

    private String channelName;

}
