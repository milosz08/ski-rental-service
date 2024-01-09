/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.slf4j.Logger;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalReqDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeValidatorPayloadDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    public static <T> T getFromSessionAndDestroy(HttpServletRequest req, String propertyName, Class<T> mappedClazz) {
        final HttpSession session = req.getSession();
        final T property = mappedClazz.cast(session.getAttribute(propertyName));
        if (property == null) {
            return null;
        }
        session.removeAttribute(propertyName);
        return property;
    }

    public static AlertTupleDto getAndDestroySessionAlert(HttpServletRequest req, SessionAlert sessionAlert) {
        final AlertTupleDto alert = getFromSessionAndDestroy(req, sessionAlert.getName(), AlertTupleDto.class);
        if (alert == null) {
            return new AlertTupleDto();
        }
        return alert;
    }

    public static AttributeModalResDto getAndDestroySessionModalData(
        HttpServletRequest req, SessionAttribute attribute
    ) {
        return getFromSessionAndDestroy(req, attribute.getName(), AttributeModalResDto.class);
    }

    public static String generateHash(String preCoded) {
        return BCrypt.withDefaults().hashToString(10, preCoded.toCharArray());
    }

    public static String getLoggedUserLogin(HttpServletRequest req) {
        final HttpSession httpSession = req.getSession();
        final LoggedUserDataDto loggedUser = (LoggedUserDataDto) httpSession
            .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());
        return loggedUser.getLogin();
    }

    public static AttributeValidatorPayloadDto validateEquipmentAttribute(
        HttpServletRequest req, ValidatorBean validator
    ) {
        final AlertTupleDto alert = new AlertTupleDto();
        final AttributeModalReqDto reqDto = new AttributeModalReqDto(req);
        final AttributeModalResDto resDto = new AttributeModalResDto(validator, reqDto, alert);

        resDto.setModalImmediatelyOpen(validator.someFieldsAreInvalid(reqDto));
        return AttributeValidatorPayloadDto.builder()
            .alert(alert)
            .reqDto(reqDto)
            .resDto(resDto)
            .isInvalid(validator.someFieldsAreInvalid(reqDto))
            .build();
    }

    public static void onHibernateException(Session session, Logger logger, RuntimeException ex) {
        if (session != null && session.getTransaction().isActive()) {
            logger.info("Some issues appears. Transaction rollback and revert previous state...");
            session.getTransaction().rollback();
        }
        throw ex;
    }

    public static String getBarcodeChecksum(String barcode) {
        int result = 0;
        for (int i = 0; i < barcode.length(); i++) {
            int barSign = Character.getNumericValue(barcode.charAt(i));
            result += barSign * (i % 2 == 0 ? 1 : 3);
        }
        result = (10 - result % 10) % 10;
        barcode += result;
        return barcode;
    }

    public static LocalDateTime truncateToTotalHour(LocalDateTime localDateTime) {
        int minutes = localDateTime.getMinute();
        if (minutes >= 30) {
            localDateTime = localDateTime.plusHours(1);
        }
        return localDateTime.truncatedTo(ChronoUnit.HOURS);
    }
}
