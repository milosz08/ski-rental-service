/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: Regex.java
 *  Last modified: 01/01/2023, 23:16
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.util;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Regex {
    public static final String LOGIN_CHANGE_EMAIL       = "^[a-z0-9]{5,80}$";
    public static final String LOGIN_EMAIL              = "^[a-z0-9@.]{5,80}$";
    public static final String PASSWORD_REQ             = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*]).{8,25}$";
    public static final String PASSWORD_AVLB            = "^[a-zA-Z0-9#?!@$%^&*]{8,25}$";
    public static final String NAME_SURNAME             = "^[a-zA-ZżźćńółęąśŻŹĆĄŚĘŁÓŃ]{3,30}$";
    public static final String PHONE_NUMBER             = "^[0-9]{3}( |)[0-9]{3}( |)[0-9]{3}$";
    public static final String DATE                     = "^[0-9]{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";
    public static final String STREET                   = "^[a-zA-Z0-9ąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\- ]{2,50}$";
    public static final String BUILDING_NR              = "^([0-9]+(?:[a-z]{0,1})){1,5}$";
    public static final String APARTMENT_NR             = "^([0-9]+(?:[a-z]{0,1})){0,5}$";
    public static final String CITY                     = "^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\- ]{2,70}$";
    public static final String POSTAL_CODE              = "^[0-9]{2}-[0-9]{3}$";
    public static final String DEF_SELECT_VALUE         = "\\b(?!none\\b)\\w+";
    public static final String POS_NUMBER_INT           = "^(?:[1-9][0-9]{3}|[1-9][0-9]{2}|[1-9][0-9]|[1-9])$";
    public static final String DECIMAL_2_ROUND          = "^$|[1-9]{0,4}(?:[.,][0-9]{1,3})?";
}
