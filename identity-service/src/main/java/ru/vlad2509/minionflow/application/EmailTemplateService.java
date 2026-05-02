package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.vlad2509.minionflow.application.util.EmailService;
import ru.vlad2509.minionflow.domain.VerificationTicket;

@ApplicationScoped
public class EmailTemplateService {

    @Inject
    EmailService emailService;

    public void registration(String email, VerificationTicket ticket) {
        String subject = "MinionFlow Registration";
        String content = """
                Welcome, %s! To complete your registration, please confirm your email address by clicking the button (TODO) below:
                
                accountId = %s
                verificationToken = %s
                
                This link will expire shortly for security reasons.
                
                If you didn’t sign up for an account, no action is needed.
                """.formatted(ticket.getUser().getUsername(), ticket.getUser().getId().toString(),
                ticket.getVerificationToken().toString());

        emailService.scheduleSending(email, subject, content);
    }

    public void recovery(String email, VerificationTicket ticket) {
        String subject = "MinionFlow account recovery";
        String content = """
                %s, we received a request to help you regain access to your account.
                
                To reset your password, please click the button (TODO) below:
                
                accountId = %s
                verificationToken = %s
                
                For security reasons, this link will expire shortly.
                
                If you didn’t request this, no action is needed and your account will remain secure.
                """.formatted(ticket.getUser().getUsername(), ticket.getUser().getId().toString(),
                ticket.getVerificationToken().toString());

        emailService.scheduleSending(email, subject, content);
    }

}
