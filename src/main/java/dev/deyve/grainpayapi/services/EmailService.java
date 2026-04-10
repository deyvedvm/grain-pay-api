package dev.deyve.grainpayapi.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${notification.mail.from}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBudgetAlert(String toEmail, String userName, String categoryName,
                                BigDecimal spent, BigDecimal limit, BigDecimal percentage,
                                int month, int year) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject(String.format("Alerta de orçamento: %s atingiu %.0f%%", categoryName, percentage));
            helper.setText(buildHtml(userName, categoryName, spent, limit, percentage, month, year), true);

            mailSender.send(message);
            logger.info("GRAIN-API: Budget alert email sent to {} for category {}", toEmail, categoryName);
        } catch (MessagingException e) {
            logger.error("GRAIN-API: Failed to send budget alert email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildHtml(String userName, String categoryName, BigDecimal spent,
                              BigDecimal limit, BigDecimal percentage, int month, int year) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <body style="font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                  <h2 style="color: #e53e3e;">&#9888; Alerta de Orçamento</h2>
                  <p>Olá, <strong>%s</strong>!</p>
                  <p>O orçamento da categoria <strong>%s</strong> em <strong>%02d/%d</strong> atingiu <strong>%.1f%%</strong> do limite.</p>
                  <table style="border-collapse: collapse; width: 100%%; margin: 20px 0;">
                    <tr style="background-color: #f7fafc;">
                      <td style="padding: 10px; border: 1px solid #e2e8f0;">Gasto atual</td>
                      <td style="padding: 10px; border: 1px solid #e2e8f0; text-align: right;">R$ %.2f</td>
                    </tr>
                    <tr>
                      <td style="padding: 10px; border: 1px solid #e2e8f0;">Limite</td>
                      <td style="padding: 10px; border: 1px solid #e2e8f0; text-align: right;">R$ %.2f</td>
                    </tr>
                    <tr style="background-color: #fff5f5;">
                      <td style="padding: 10px; border: 1px solid #e2e8f0;"><strong>Percentual</strong></td>
                      <td style="padding: 10px; border: 1px solid #e2e8f0; text-align: right; color: #e53e3e;"><strong>%.1f%%</strong></td>
                    </tr>
                  </table>
                  <p style="color: #718096; font-size: 12px;">Este é um e-mail automático do Grain Pay. Não responda.</p>
                </body>
                </html>
                """.formatted(userName, categoryName, month, year, percentage, spent, limit, percentage);
    }
}
