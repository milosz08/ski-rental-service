/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.mail;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MailTemplate {
    ADD_NEW_EMPLOYER_CREATOR("add-new-employer-creator"),
    ADD_NEW_EMPLOYER_REQUESTER("add-new-employer-requester"),
    ADD_NEW_RENT_CUSTOMER("add-new-rent-customer"),
    ADD_NEW_RENT_EMPLOYER("add-new-rent-employer"),
    ADD_NEW_RENT_OWNER("add-new-rent-owner"),
    CHANGE_PASSWORD("change-password"),
    CREATE_NEW_RETURN_CUSTOMER("create-new-return-customer"),
    CREATE_NEW_RETURN_EMPLOYER("create-new-return-employer"),
    CREATE_NEW_RETURN_OWNER("create-new-return-owner"),
    ;

    private final String templateName;

    public String getFullName() {
        return templateName + ".template.ftl";
    }
}
