package com.smiligenceUAT1.metrozdeliveryappnew.Common;

import android.widget.EditText;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.smiligenceUAT1.metrozdeliveryappnew.Common.Constant.*;

public class TextUtils {
    public static boolean isValidUserName(final String userName) {
        Pattern pattern = Pattern.compile ( USER_NAME_PATTERN );
        Matcher matcher = pattern.matcher ( userName );
        return matcher.matches ();
    }

    public static boolean isValidFirstName(final String firstName) {
        Pattern pattern = Pattern.compile ( FIRST_NAME_PATTERN );
        Matcher matcher = pattern.matcher ( firstName );
        return matcher.matches ();
    }

    public static boolean isValidlastName(final String lastName) {
        Pattern pattern = Pattern.compile ( LAST_NAME_PATTERN );
        Matcher matcher = pattern.matcher ( lastName );
        return matcher.matches ();
    }

    public  static  boolean validateVehicleNumber(String gstnumber){
        Pattern pattern = Pattern.compile(VEHICLE_NUMBER_PATTERN);
        Matcher matcher = pattern.matcher(gstnumber);
        return matcher.matches();
    }
    public static boolean isValidAddress(final String Address) {
        Pattern pattern = Pattern.compile ( ADDRESS_PATTERN );
        Matcher matcher = pattern.matcher ( Address );
        return matcher.matches ();
    }

    public static boolean isValidnumeric(final String number) {
        Pattern pattern = Pattern.compile ( NUMERIC_PATTERN );
        Matcher matcher = pattern.matcher ( number );
        return matcher.matches ();
    }

    public static boolean isValidPrice(String ValidatePrice) {
        Pattern pattern_validPrice = Pattern.compile ( ITEM_PRICE_PATTERN );
        Matcher matcher = pattern_validPrice.matcher ( ValidatePrice );
        return matcher.matches ();
    }
    public static boolean validAdharNumber(final String aadharNumber) {
        Pattern pattern = Pattern.compile(AADHAR_PATTERN);
        Matcher matcher = pattern.matcher(aadharNumber);

        return matcher.matches();

    }
    public static boolean validIFSCCode(final String IFSC) {
        Pattern pattern = Pattern.compile(IFSC_PATTERN);
        Matcher matcher = pattern.matcher(IFSC);

        return matcher.matches();

    }
    public static boolean isValidAlphaCharacters(final String bankName) {
        Pattern pattern = Pattern.compile ( BANK_NAME_PATTERN );
        Matcher matcher = pattern.matcher ( bankName );
        return matcher.matches ();
    }

    public static boolean isValidAlphaNumeric(final String alphaPattern) {
        Pattern pattern = Pattern.compile ( ALPHA_NUMERIC_PATTERN );
        Matcher matcher = pattern.matcher ( alphaPattern );
        return matcher.matches ();
    }

    public static boolean isValidPassword(final String password) {
        if (password.length () > PASSWORD_LENGTH) {
            Pattern pattern = Pattern.compile ( PASSWORD_PATTERN );
            Matcher matcher = pattern.matcher ( password );
            return matcher.matches ();
        } else {
            return BOOLEAN_FALSE;
        }
    }

    public static boolean isValidEmail(final String email) {

        Pattern pattern = Pattern.compile ( EMAIL_PATTERN );
        Matcher matcher = pattern.matcher ( email );
        return matcher.matches ();
    }

    public static boolean validateLoginForm(final String userNameStr, final String passwordStr,
                                            EditText userName, EditText password) {

        if ("".equals ( userNameStr )) {
            userName.setError ( REQUIRED_MSG );
            return false;
        }
        if ("".equals ( passwordStr )) {
            password.setError ( REQUIRED_MSG );
            return false;
        }
        return true;
    }

    public boolean validateRegistratonForm(final String userName, final String password,
                                           final String repassword, final String email) {
        return true;
    }

    public static boolean validateItemForm(final EditText e_fname, final EditText e_price,
                                           final String itemName, final String itemPrice) {

        if (android.text.TextUtils.isEmpty ( itemName )) {
            e_fname.setError ( REQUIRED_MSG );
            return false;
        }
        if ("".equals ( e_price.getText ().toString ().trim () )) {
            e_price.setError ( REQUIRED_MSG );
            return false;
        }
        return true;
    }

    public static boolean validatePhoneNumber(String phoneNo) {

        if (phoneNo.matches ( "\\d{10}" )) return true;
        else return false;
    }

    public static boolean validateName(String name) {
        return name.matches ( "[aA-zZ]*" );
    }


    public static boolean validateNumber(String number) {
        return number.matches ( "[0-9]*" );
    }

    public static boolean isValidAlphaNumericSpecialCharacters(final String data) {
        Pattern pattern = Pattern.compile ( ALPHA_NUMERIC_STRING_PATTERN );
        Matcher matcher = pattern.matcher ( data );
        return matcher.matches ();
    }

    public static boolean isValidNamePattern(final String data) {
        Pattern pattern = Pattern.compile ( NAME_PATTERN );
        Matcher matcher = pattern.matcher ( data );
        return matcher.matches ();
    }

    public static boolean validDiscountName(String name) {

        Pattern pattern = Pattern.compile ( DISCOUNT_NAME_PATTERN );
        Matcher matcher = pattern.matcher ( name );
        return matcher.matches ();
    }

    public static <T> List<T> removeDuplicatesList(List<T> list) {

        Set<T> set = new LinkedHashSet<> ();

        set.addAll ( list );

        list.clear ();


        list.addAll ( set );

        return list;
    }
}
