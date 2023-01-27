/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: Utils.java
 *  Last modified: 29/12/2022, 01:38
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.util;

import org.slf4j.Logger;
import org.hibernate.Session;

import jakarta.servlet.http.*;
import at.favre.lib.crypto.bcrypt.BCrypt;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.dto.attribute.*;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Utils {

    public static <T> T getFromSessionAndDestroy(HttpServletRequest req, String propertyName, Class<T> mappedClazz) {
        final HttpSession session = req.getSession();
        final T property = mappedClazz.cast(session.getAttribute(propertyName));
        if (isNull(property)) return null;
        session.removeAttribute(propertyName);
        return property;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static AlertTupleDto getAndDestroySessionAlert(HttpServletRequest req, SessionAlert sessionAlert) {
        return getFromSessionAndDestroy(req, sessionAlert.getName(), AlertTupleDto.class);
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
        final LoggedUserDataDto loggedUser = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());
        return loggedUser.getLogin();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static AttributeValidatorPayloadDto validateEquipmentAttribute(HttpServletRequest req, ValidatorBean validator) {
        final AlertTupleDto alert = new AlertTupleDto();
        final HttpSession httpSession = req.getSession();

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
}
