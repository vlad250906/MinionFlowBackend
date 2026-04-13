package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import ru.vlad2509.minionflow.infrastructure.persistence.model.RemoteUser;

public class RemoteRepository implements PanacheRepository<RemoteUser> {
}
