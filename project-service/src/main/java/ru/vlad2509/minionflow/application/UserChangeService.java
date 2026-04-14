package ru.vlad2509.minionflow.application;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import ru.vlad2509.minionflow.application.dto.messaging.UserChange;
import ru.vlad2509.minionflow.infrastructure.messaging.events.UserChangeEventListener;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.RemoteUserRepository;

@ApplicationScoped
public class UserChangeService {

    @Inject
    RemoteUserRepository remoteUserRepository;

    @Inject
    UserChangeEventListener userChangeEventListener;

    public void onStart(@Observes StartupEvent ev) {
        userChangeEventListener.setEventHandler(this::eventHandler);
        userChangeEventListener.startListening();
    }

    public void eventHandler(UserChange event) {
        if (event.newUsername() == null) {
            remoteUserRepository.delete(event.userId());
        } else {
            remoteUserRepository.updateOrCreate(event.userId(), event.newUsername());
        }
    }

}
