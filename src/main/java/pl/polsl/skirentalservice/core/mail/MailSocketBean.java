/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: MailFactory.java
 *  Last modified: 01/01/2023, 18:02
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.mail;

import org.slf4j.*;
import freemarker.template.*;

import jakarta.ejb.*;
import jakarta.mail.*;
import jakarta.xml.bind.*;
import jakarta.mail.internet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.*;
import java.text.*;
import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

import pl.polsl.skirentalservice.core.ConfigBean;
import pl.polsl.skirentalservice.core.JAXBProperty;
import pl.polsl.skirentalservice.exception.UnableToSendEmailException;

import static java.time.Instant.now;
import static freemarker.template.Configuration.VERSION_2_3_22;

import static pl.polsl.skirentalservice.util.Utils.DEF_TITLE;
import static pl.polsl.skirentalservice.core.mail.JakartaMailAuthenticator.findProperty;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Startup
@Singleton(name = "MailSocketFactoryBean")
public class MailSocketBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailSocketBean.class);

    @EJB private ConfigBean config;

    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd, kk:mm:ss", new Locale("pl"));
    private static final String MAIL_CFG = "/mail/mail.cfg.xml";
    private static final String FREEMARKER_PATH = "/mail/templates";

    private Session mailSession;
    private Configuration freemarkerConfig;
    private List<JAXBProperty> configProperties;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    MailSocketBean() {
        try {
            freemarkerConfig = new Configuration(VERSION_2_3_22);
            freemarkerConfig.setClassForTemplateLoading(MailSocketBean.class, FREEMARKER_PATH);
            LOGGER.info("Successful loaded freemarker template engine cache path. Cache path: {}", FREEMARKER_PATH);

            final JAXBContext jaxbContext = JAXBContext.newInstance(JAXBMailConfig.class);
            final var config = (JAXBMailConfig) jaxbContext.createUnmarshaller()
                .unmarshal(getClass().getResource(MAIL_CFG));
            configProperties = config.getProperties();

            final Properties properties = new Properties();
            final List<JAXBProperty> withoutCredentials = config.getProperties().stream()
                .filter(p -> !p.getName().equals("mail.smtp.user") && !p.getName().equals("mail.smtp.pass"))
                .collect(Collectors.toList());

            for (final JAXBProperty property : withoutCredentials) {
                properties.put(property.getName(), property.getValue());
            }
            final Authenticator authenticator = new JakartaMailAuthenticator(config.getProperties());
            mailSession = Session.getInstance(properties, authenticator);
            LOGGER.info("Successful loaded JavaMail API properties with authentication. Props: {}", properties);
        } catch (JAXBException ex) {
            LOGGER.error("Unable to load mail properties from extended XML file: {}", MAIL_CFG);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendMessage(String sendTo, MailRequestPayload payload, HttpServletRequest req) {
        sendMessage(List.of(sendTo), payload, req);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendMessage(List<String> sendTo, MailRequestPayload payload, HttpServletRequest req) {
        try {
            final Message message = new MimeMessage(mailSession);
            final Template bodyTemplate = freemarkerConfig.getTemplate(payload.getTemplateName());
            final Writer outWriter = new StringWriter();

            final Map<String, Object> addtlnPayloadProps = new HashMap<>(payload.getTemplateVars());
            addtlnPayloadProps.put("messageResponder", payload.getMessageResponder());
            addtlnPayloadProps.put("serverUtcTime", now().toString());
            addtlnPayloadProps.put("baseServletPath", getBaseReqPath(req));
            addtlnPayloadProps.put("currentYear", String.valueOf(LocalDate.now().getYear()));
            addtlnPayloadProps.put("systemVersion", config.getSystemVersion());
            bodyTemplate.process(addtlnPayloadProps, outWriter);

            Address[] sendToAddresses = new Address[sendTo.size()];
            for (int i = 0; i < sendToAddresses.length; i++) {
                sendToAddresses[i] = new InternetAddress(sendTo.get(i));
            }
            message.setFrom(new InternetAddress(findProperty(configProperties, "mail.smtp.user"), DEF_TITLE));
            message.setRecipients(Message.RecipientType.TO, sendToAddresses);

            if (!Objects.isNull(payload.getAttachmentsPaths())) {
                final Multipart multipart = new MimeMultipart();
                final BodyPart bodyPart = new MimeBodyPart();

                bodyPart.setContent(outWriter.toString(), "text/html;charset=UTF-8");
                multipart.addBodyPart(bodyPart);

                final MimeBodyPart attachementsPart = new MimeBodyPart();
                for (String filePath : payload.getAttachmentsPaths()) {
                    attachementsPart.attachFile(new File(filePath));
                }
                multipart.addBodyPart(attachementsPart);
                message.setContent(multipart);
            } else {
                message.setContent(outWriter.toString(), "text/html;charset=UTF-8");
            }
            message.setSubject(payload.getSubject());
            message.setSentDate(new Date());

            Transport.send(message);
            LOGGER.info("Successful send email message to the following recipent/s: {}", sendTo);
        } catch (IOException ex) {
            LOGGER.error("Unable to load freemarker template. Template name: {}", payload.getTemplateName());
            throw new UnableToSendEmailException(String.join(", ", sendTo), payload);
        } catch (TemplateException ex) {
            LOGGER.error("Unable to process freemarker template. Exception: {}", ex.getMessage());
            throw new UnableToSendEmailException(String.join(", ", sendTo), payload);
        } catch (MessagingException | RuntimeException ex) {
            throw new UnableToSendEmailException(String.join(", ", sendTo), payload);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getDomain() {
        return "@" + configProperties.stream().filter(p -> p.getName()
            .equals("mail.smtp.domain")).findFirst().map(JAXBProperty::getValue).orElse("localhost");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private  String getBaseReqPath(HttpServletRequest req) {
        final boolean isHttp = req.getScheme().equals("http") && req.getServerPort() == 80;
        final boolean isHttps = req.getScheme().equals("https") && req.getServerPort() == 443;
        return req.getScheme() + "://" + req.getServerName() + (isHttp || isHttps ? "" : ":" + req.getServerPort());
    }
}
