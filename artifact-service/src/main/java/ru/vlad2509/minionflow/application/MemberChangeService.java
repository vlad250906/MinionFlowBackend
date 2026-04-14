package ru.vlad2509.minionflow.application;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import ru.vlad2509.minionflow.infrastructure.messaging.events.MemberChangeEventListener;
import ru.vlad2509.minionflow.application.dto.messaging.ProjectMemberChange;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.RemoteProjectMemberRepository;

@ApplicationScoped
public class MemberChangeService {

    @Inject
    RemoteProjectMemberRepository remoteProjectMemberRepository;

    @Inject
    MemberChangeEventListener memberChangeEventListener;

    public void onStartup(@Observes StartupEvent event) {
        memberChangeEventListener.setEventHandler(this::eventHandler);
        memberChangeEventListener.startListening();
    }

    public void eventHandler(ProjectMemberChange change) {
        if (change.newMemberRole() == null) {
            remoteProjectMemberRepository.delete(change.projectId(), change.userId());
        } else {
            remoteProjectMemberRepository.updateOrCreate(change.projectId(), change.userId(), change.newMemberRole());
        }
    }

}
