package ru.vlad2509.minionflow.domain.enums;

public enum AccountStatus {
    CREATED,
    ACTIVE,
    SUSPENDED;

    public boolean mayUpdateTo(AccountStatus newStatus){
        return switch (this){
            case CREATED -> true;
            case ACTIVE, SUSPENDED -> newStatus != CREATED;
        };
    }
}
