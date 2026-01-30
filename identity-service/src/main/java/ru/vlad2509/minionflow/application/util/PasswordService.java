package ru.vlad2509.minionflow.application.util;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordService {

    public String hashNew(String password){
        return BcryptUtil.bcryptHash(password);
    }

    public boolean verifyPassword(String plainPassword, String hashStored){
        return BcryptUtil.matches(plainPassword, hashStored);
    }

}
