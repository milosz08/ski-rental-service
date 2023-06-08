/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MailSocketSingleton.java
 * Last modified: 6/2/23, 11:48 PM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.core.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import jakarta.servlet.http.HttpServletRequest;

import freemarker.template.Template;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import java.util.*;
import java.io.File;
import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDate;

import pl.polsl.skirentalservice.core.JAXBProperty;
import pl.polsl.skirentalservice.core.ConfigSingleton;

import static pl.polsl.skirentalservice.exception.ServletException.UnableToSendEmailException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class MailSocketSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailSocketSingleton.class);
    private static final ConfigSingleton config = ConfigSingleton.getInstance();

    private static final String MAIL_CFG = "/mail/mail.cfg.xml";
    private static final String FREEMARKER_PATH = "/mail/templates";

    private Session mailSession;
    private Configuration freemarkerConfig;
    private List<JAXBProperty> configProperties;

    private static volatile MailSocketSingleton instance;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private MailSocketSingleton() {
        try {
            freemarkerConfig = new Configuration(Configuration.VERSION_2_3_22);
            freemarkerConfig.setClassForTemplateLoading(MailSocketSingleton.class, FREEMARKER_PATH);
            LOGGER.info("Successful loaded freemarker template engine cache path. Cache path: {}", FREEMARKER_PATH);

            final JAXBContext jaxbContext = JAXBContext.newInstance(JAXBMailConfig.class);
            final var config = (JAXBMailConfig) jaxbContext.createUnmarshaller()
                .unmarshal(MailSocketSingleton.class.getResource(MAIL_CFG));
            configProperties = config.getProperties();

            final Properties properties = new Properties();
            final List<JAXBProperty> withoutCredentials = config.getProperties().stream()
                .filter(p -> !p.getName().equals("mail.smtp.user") && !p.getName().equals("mail.smtp.pass"))
                .toList();

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
            addtlnPayloadProps.put("serverUtcTime", Instant.now().toString());
            addtlnPayloadProps.put("baseServletPath", getBaseReqPath(req));
            addtlnPayloadProps.put("currentYear", String.valueOf(LocalDate.now().getYear()));
            addtlnPayloadProps.put("systemVersion", config.getSystemVersion());
            bodyTemplate.process(addtlnPayloadProps, outWriter);

            final Address[] sendToAddresses = new Address[sendTo.size()];
            for (int i = 0; i < sendToAddresses.length; i++) {
                sendToAddresses[i] = new InternetAddress(sendTo.get(i));
            }
            message.setFrom(new InternetAddress(JakartaMailAuthenticator.findProperty(configProperties, "mail.smtp.user"),
                config.getDefPageTitle()));
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
            throw new UnableToSendEmailException(String.join(", ", sendTo), payload, LOGGER);
        } catch (TemplateException ex) {
            LOGGER.error("Unable to process freemarker template. Exception: {}", ex.getMessage());
            throw new UnableToSendEmailException(String.join(", ", sendTo), payload, LOGGER);
        } catch (MessagingException | RuntimeException ex) {
            throw new UnableToSendEmailException(String.join(", ", sendTo), payload, LOGGER);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getDomain() {
        return "@" + configProperties.stream()
            .filter(p -> p.getName().equals("mail.smtp.domain"))
            .findFirst().map(JAXBProperty::getValue).orElse("localhost");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private  String getBaseReqPath(HttpServletRequest req) {
        final boolean isHttp = req.getScheme().equals("http") && req.getServerPort() == 80;
        final boolean isHttps = req.getScheme().equals("https") && req.getServerPort() == 443;
        return req.getScheme() + "://" + req.getServerName() + (isHttp || isHttps ? "" : ":" + req.getServerPort());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static synchronized MailSocketSingleton getInstance() {
        if (Objects.isNull(instance)) {
            instance = new MailSocketSingleton();
        }
        return instance;
    }
}
