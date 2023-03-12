/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: SessionAttribute.java
 *  Last modified: 30/01/2023, 23:27
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.util;

import lombok.Getter;
import lombok.AllArgsConstructor;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@AllArgsConstructor
public enum SessionAttribute {
    EMPLOYERS_LIST_SORTER       ("employers-list-sorter"),
    EMPLOYERS_LIST_FILTER       ("employers-list-filter"),
    CUSTOMERS_LIST_SORTER       ("customers-list-sorter"),
    CUSTOMERS_LIST_FILTER       ("customers-list-filter"),
    EQUIPMENTS_LIST_SORTER      ("equipments-list-sorter"),
    EQUIPMENTS_LIST_FILTER      ("equipments-list-filter"),
    RENT_EQUIPMENTS_LIST_SORTER ("equipments-list-sorter"),
    RENT_EQUIPMENTS_LIST_FILTER ("equipments-list-filter"),
    RENTS_LIST_SORTER           ("rents-list-sorter"),
    RENTS_LIST_FILTER           ("rents-list-filter"),
    RETURNS_LIST_SORTER         ("returns-list-sorter"),
    RETURNS_LIST_FILTER         ("returns-list-filter"),

    EQ_TYPES_MODAL_DATA         ("equipmentTypeModalData"),
    EQ_BRANDS_MODAL_DATA        ("equipmentBrandsModalData"),
    EQ_COLORS_MODAL_DATA        ("equipmentColorsModalData"),
    EQ_ADD_CART_MODAL_DATA      ("equipmentNewCartModalData"),
    EQ_EDIT_CART_MODAL_DATA     ("equipmentEditCartModalData"),

    LOGGED_USER_DETAILS         ("loggedUserDetails"),
    INMEMORY_NEW_RENT_DATA      ("inmemoryNewRentData"),
    LOGOUT_MODAL                ("logout-modal");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
}
