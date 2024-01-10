/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.polsl.skirentalservice.core.servlet.session.Attribute;

@Getter
@AllArgsConstructor
public enum SessionAlert implements Attribute {
    LOGIN_PAGE_ALERT("login-page-alert"),
    FORGOT_PASSWORD_PAGE_ALERT("forgot-password-page-alert"),
    CHANGE_FORGOTTEN_PASSWORD_PAGE_ALERT("change-forgotten-password-page-alert"),
    FIRST_ACCESS_PAGE_ALERT("first-access-page-alert"),

    OWNER_SETTINGS_PAGE_ALERT("owner-settings-page-alert"),
    OWNER_EMPLOYERS_PAGE_ALERT("owner-employers-page-alert"),
    OWNER_ADD_EMPLOYER_PAGE_ALERT("owner-add-employer-page-alert"),
    OWNER_EDIT_EMPLOYER_PAGE_ALERT("owner-edit-employer-page-alert"),
    OWNER_ADD_EQUIPMENT_PAGE_ALERT("owner-add-equipment-page-alert"),
    OWNER_EDIT_EQUIPMENT_PAGE_ALERT("owner-edit-equipment-page-alert"),

    SELLER_DASHBOARD_PAGE_ALERT("seller-dashboard-page-alert"),
    SELLER_ADD_CUSTOMER_PAGE_ALERT("seller-add-customer-page-alert"),
    SELLER_EDIT_CUSTOMER_PAGE_ALERT("seller-edit-customer-page-alert"),
    SELLER_CREATE_NEW_RENT_PAGE_ALERT("seller-create-new-rent-page-alert"),
    SELLER_COMPLETE_RENT_PAGE_ALERT("seller-complete-rent-page-alert"),

    COMMON_PROFILE_PAGE_ALERT("common-profile-page-alert"),
    COMMON_RENTS_PAGE_ALERT("common-rents-page-alert"),
    COMMON_CUSTOMERS_PAGE_ALERT("common-customers-page-alert"),
    COMMON_EQUIPMENTS_PAGE_ALERT("common-equipments-page-alert"),
    COMMON_RETURNS_PAGE_ALERT("common-returns-page-alert"),
    ;

    private final String attributeName;
}
