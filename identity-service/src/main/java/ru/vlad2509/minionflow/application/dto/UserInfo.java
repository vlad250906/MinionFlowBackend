package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.User;
import ru.vlad2509.minionflow.domain.enums.AccountStatus;

import java.util.UUID;

public record UserInfo(

        UUID userId,
        String email,
        String username,
        AccountStatus status

) {

    public static UserInfo fromDomain(User user){
        return new UserInfo(user.getId(), user.getEmail(), user.getUsername(), user.getStatus());
    }

}
