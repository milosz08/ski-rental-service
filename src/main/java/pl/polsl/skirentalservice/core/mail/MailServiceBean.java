package pl.polsl.skirentalservice.core.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.XMLConfigLoader;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static pl.polsl.skirentalservice.exception.ServletException.UnableToSendEmailException;

@Slf4j
@Singleton
public class MailServiceBean {
    private static final String MAIL_CFG = "/mail/mail.cfg.xml";
    private static final String MAIL_CFG_DEV = "/mail/mail.cfg.dev.xml";
    private static final String FREEMARKER_PATH = "/mail/templates";
    private final ServerConfigBean serverConfigBean;
    private final Session mailSession;
    private final Configuration freemarkerConfig;
    private final Properties configProperties;

    @Inject
    public MailServiceBean(ServerConfigBean serverConfigBean) {
        this.serverConfigBean = serverConfigBean;
        final String configFile = serverConfigBean.getEnvironment().isDevOrDocker() ? MAIL_CFG_DEV : MAIL_CFG;
        final XMLConfigLoader<XMLMailConfig> xmlConfigLoader = new XMLConfigLoader<>(configFile, XMLMailConfig.class);
        final Properties allConfigProperties = xmlConfigLoader.loadConfig();

        final Properties authConfigProperties = new Properties();
        configProperties = new Properties();
        for (final Map.Entry<Object, Object> property : allConfigProperties.entrySet()) {
            final String key = (String) property.getKey();
            Properties propertiesContainer = configProperties;
            if (key.equals("mail.smtp.user") || key.equals("mail.smtp.pass")) {
                propertiesContainer = authConfigProperties;
            }
            propertiesContainer.put(property.getKey(), property.getValue());
        }
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_22);
        freemarkerConfig.setClassForTemplateLoading(MailServiceBean.class, FREEMARKER_PATH);
        log.info("Successful loaded freemarker template engine cache path. Cache path: {}", FREEMARKER_PATH);

        final Authenticator authenticator = new JakartaMailAuthenticator(authConfigProperties);
        mailSession = Session.getInstance(configProperties, authenticator);
        log.info("Successful loaded JavaMail API properties with authentication. Props: {}", configProperties);
    }

    public void sendMessage(String sendTo, MailRequestPayload payload, WebServletRequest req) {
        sendMessage(List.of(sendTo), payload, req);
    }

    public void sendMessage(List<String> sendTo, MailRequestPayload payload, WebServletRequest req) {
        try {
            final Message message = new MimeMessage(mailSession);
            final Template bodyTemplate = freemarkerConfig.getTemplate(payload.getTemplate().getFullName());
            final Writer outWriter = new StringWriter();

            final Map<String, Object> addtlnPayloadProps = new HashMap<>(payload.getTemplateVars());
            addtlnPayloadProps.put("messageResponder", payload.getMessageResponder());
            addtlnPayloadProps.put("serverUtcTime", Instant.now().toString());
            addtlnPayloadProps.put("baseServletPath", req.getBaseRequestPath());
            addtlnPayloadProps.put("currentYear", String.valueOf(LocalDate.now().getYear()));
            addtlnPayloadProps.put("systemVersion", serverConfigBean.getSystemVersion());
            bodyTemplate.process(addtlnPayloadProps, outWriter);

            final Address[] sendToAddresses = new Address[sendTo.size()];
            for (int i = 0; i < sendToAddresses.length; i++) {
                sendToAddresses[i] = new InternetAddress(sendTo.get(i));
            }
            message.setFrom(new InternetAddress("noreply@" + configProperties.getProperty("mail.smtp.domain"),
                serverConfigBean.getTitlePageTag()));
            message.setRecipients(Message.RecipientType.TO, sendToAddresses);

            if (payload.getAttachments() != null) {
                final Multipart multipart = new MimeMultipart();
                final BodyPart bodyPart = new MimeBodyPart();

                bodyPart.setContent(outWriter.toString(), "text/html;charset=UTF-8");
                multipart.addBodyPart(bodyPart);

                for (final Attachment attachment : payload.getAttachments()) {
                    final BodyPart attachPart = new MimeBodyPart();
                    final DataSource dataSource = new ByteArrayDataSource(attachment.data(),
                        attachment.type().getMimeType());
                    attachPart.setDataHandler(new DataHandler(dataSource));
                    attachPart.setFileName(attachment.name());
                    multipart.addBodyPart(attachPart);
                }
                message.setContent(multipart);
            } else {
                message.setContent(outWriter.toString(), "text/html;charset=UTF-8");
            }
            message.setSubject(serverConfigBean.getTitlePageTag() + " | " + payload.getSubject());
            message.setSentDate(new Date());

            Transport.send(message);
            log.info("Successful send email message to the following recipent/s: {}", sendTo);
        } catch (IOException ex) {
            log.error("Unable to load freemarker template. Template name: {}", payload.getTemplate());
            throw new UnableToSendEmailException(String.join(", ", sendTo), payload);
        } catch (TemplateException ex) {
            log.error("Unable to process freemarker template. Exception: {}", ex.getMessage());
            throw new UnableToSendEmailException(String.join(", ", sendTo), payload);
        } catch (MessagingException | RuntimeException ex) {
            log.error("Unable to send message. Cause: {}", ex.getMessage());
            throw new UnableToSendEmailException(String.join(", ", sendTo), payload);
        }
    }

    public String getDomain() {
        return "@" + configProperties.getProperty("mail.smtp.domain");
    }
}
