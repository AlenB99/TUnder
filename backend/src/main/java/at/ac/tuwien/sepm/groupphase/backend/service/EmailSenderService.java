package at.ac.tuwien.sepm.groupphase.backend.service;

import org.springframework.mail.SimpleMailMessage;

/**
 * Interface for the EmailSenderService.
 */
public interface EmailSenderService {
    /**
     * Sends an email to a user, since there is no SMTP service right now.
     *
     * @param email email object which is sent.
     */
    void sendEmail(SimpleMailMessage email);
}
