/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: Utils.java
 *  Last modified: 31/01/2023, 08:36
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.util;

import org.slf4j.Logger;

import org.hibernate.Session;
import at.favre.lib.crypto.bcrypt.BCrypt;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalReqDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeValidatorPayloadDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Utils {

    public static <T> T getFromSessionAndDestroy(HttpServletRequest req, String propertyName, Class<T> mappedClazz) {
        final HttpSession session = req.getSession();
        final T property = mappedClazz.cast(session.getAttribute(propertyName));
        if (Objects.isNull(property)) return null;
        session.removeAttribute(propertyName);
        return property;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static AlertTupleDto getAndDestroySessionAlert(HttpServletRequest req, SessionAlert sessionAlert) {
        final AlertTupleDto alert = getFromSessionAndDestroy(req, sessionAlert.getName(), AlertTupleDto.class);
        if (Objects.isNull(alert)) return new AlertTupleDto();
        return alert;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static AttributeModalResDto getAndDestroySessionModalData(HttpServletRequest req, SessionAttribute attribute) {
        return getFromSessionAndDestroy(req, attribute.getName(), AttributeModalResDto.class);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String generateHash(String preCoded) {
        return BCrypt.withDefaults().hashToString(10, preCoded.toCharArray());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getLoggedUserLogin(HttpServletRequest req) {
        final HttpSession httpSession = req.getSession();
        final LoggedUserDataDto loggedUser = (LoggedUserDataDto) httpSession
            .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());
        return loggedUser.getLogin();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static AttributeValidatorPayloadDto validateEquipmentAttribute(HttpServletRequest req, ValidatorSingleton validator) {
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void onHibernateException(Session session, Logger logger, RuntimeException ex) {
        if (session.getTransaction().isActive()) {
            logger.info("Some issues appears. Transaction rollback and revert previous state...");
            session.getTransaction().rollback();
        }
        throw ex;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static LocalDateTime truncateToTotalHour(LocalDateTime localDateTime) {
        int minutes = localDateTime.getMinute();
        if (minutes >= 30) {
            localDateTime = localDateTime.plusHours(1);
        }
        return localDateTime.truncatedTo(ChronoUnit.HOURS);
    }
}
