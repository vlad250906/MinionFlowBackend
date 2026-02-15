package ru.vlad2509.minionflow.application.port.out;

public interface SmtpService {

    SendingResult sendMail(String to, String subject, String content);

}
