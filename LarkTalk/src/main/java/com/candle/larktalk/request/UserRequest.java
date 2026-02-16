package com.candle.larktalk.request;


import lombok.Data;

@Data
public class UserRequest {

    private String login;

    private String nickname;

    private String password;

    private String email;
}
