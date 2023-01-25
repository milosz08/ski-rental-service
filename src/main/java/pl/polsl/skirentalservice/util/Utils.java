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

import pl.polsl.skirentalservice.dto.attribute.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Utils {

    public static AlertTupleDto getAndDestroySessionAlert(HttpServletRequest req, SessionAlert sessionAlert) {
        final HttpSession httpSession = req.getSession();
        final AlertTupleDto alert = (AlertTupleDto) httpSession.getAttribute(sessionAlert.getName());
        if (isNull(alert)) return new AlertTupleDto();
        httpSession.removeAttribute(sessionAlert.getName());
        return alert;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static AttributeModalResDto getAndDestroySessionModalData(HttpServletRequest req, SessionAttribute attribute) {
        final HttpSession httpSession = req.getSession();
        final AttributeModalResDto attr = (AttributeModalResDto) httpSession.getAttribute(attribute.getName());
        if (isNull(attr)) return new AttributeModalResDto();
        httpSession.removeAttribute(attribute.getName());
        return attr;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String generateHash(String preCoded) {
        return BCrypt.withDefaults().hashToString(10, preCoded.toCharArray());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean invalidPassword(String hash, String decoded) {
        return !BCrypt.verifyer().verify(hash.toCharArray(), decoded).verified;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getLoggedUserLogin(HttpServletRequest req) {
        final HttpSession httpSession = req.getSession();
        final LoggedUserDataDto loggedUser = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());
        return loggedUser.getLogin();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static AttributeValidatorPayloadDto validateEquipmentAttribute(HttpServletRequest req, ValidatorBean validator) {
        final AlertTupleDto failureAlert = new AlertTupleDto();
        final AlertTupleDto successAlert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();

        final AttributeModalReqDto reqDto = new AttributeModalReqDto(req);
        final AttributeModalResDto resDto = new AttributeModalResDto(validator, reqDto, failureAlert);

        resDto.setModalImmediatelyOpen(validator.someFieldsAreInvalid(reqDto));
        return AttributeValidatorPayloadDto.builder()
            .failureAlert(failureAlert)
            .successAlert(successAlert)
            .reqDto(reqDto)
            .resDto(resDto)
            .isInvalid(validator.someFieldsAreInvalid(reqDto))
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void onAttributeException(AttributeValidatorPayloadDto payload, RuntimeException ex) {
        payload.getFailureAlert().setActive(true);
        payload.getFailureAlert().setMessage(ex.getMessage());
        payload.getResDto().setAlert(payload.getFailureAlert());
        payload.getResDto().getName().setValue(EMPTY);
        payload.getResDto().setModalImmediatelyOpen(true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void onAttributeException(AlertTupleDto alert, AttributeModalResDto resDto, RuntimeException ex) {
        alert.setActive(true);
        alert.setMessage(ex.getMessage());
        resDto.setAlert(alert);
        resDto.setModalImmediatelyOpen(true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void onHibernateException(Session session, Logger logger, RuntimeException ex) {
        if (session.getTransaction().isActive()) {
            logger.error("Some issues appears. Transaction rollback and revert previous state...");
            session.getTransaction().rollback();
        }
        throw ex;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static <T> T getFromSessionAndDestroy(HttpServletRequest req, String propertyName, Class<T> mappedClazz) {
        final HttpSession session = req.getSession();
        final T postRedirGetProperty = mappedClazz.cast(session.getAttribute(propertyName));
        if (isNull(postRedirGetProperty)) {
            return null;
        }
        session.removeAttribute(propertyName);
        return postRedirGetProperty;
    }
}
