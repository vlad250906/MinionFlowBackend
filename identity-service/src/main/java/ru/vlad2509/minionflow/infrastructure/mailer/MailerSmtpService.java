package ru.vlad2509.minionflow.infrastructure.mailer;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.vertx.ext.mail.SMTPException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.application.port.out.SendingResult;
import ru.vlad2509.minionflow.application.port.out.SmtpService;

@ApplicationScoped
public class MailerSmtpService implements SmtpService {

    private static final Logger LOG = LoggerFactory.getLogger(MailerSmtpService.class);

    @Inject
    Mailer mailer;

    @Override
    public SendingResult sendMail(String to, String subject, String content) {
        try {
            mailer.send(Mail.withText(to, subject, content));
        } catch (RuntimeException ex) {
            LOG.error("Error sending mail to {}", to, ex);
            SMTPException smtpException = findClause(ex, SMTPException.class);
            if (smtpException == null)
                return SendingResult.UNAVAILABLE;
            if (smtpException.isPermanent())
                return SendingResult.IMPOSSIBLE;
            return SendingResult.UNAVAILABLE;
        }
        return SendingResult.SUCCESS;
    }

    private <T extends Throwable> T findClause(Throwable throwable, Class<T> clazz) {
        Throwable cur = throwable;
        while (cur != null) {
            if (clazz.isInstance(cur))
                return clazz.cast(cur);
            cur = cur.getCause();
        }
        return null;
    }
}
