package com.smiligenceUAT1.metrozdeliveryappnew.Common;

public class Constant
{
    public static String USER_DETAILS_TABLE = "UserDetails";

    public static String MY_PROFILE="My Profile";
    public static String MY_ASSIGNED_ORDERS="My Assigned Orders";
    public static String Daily_CheckIn_CheckOut="Daily CheckIn-CheckOut";
    public static String WEEKLY_PAYMENT_SETTLEMENTS="Weekly Payment Settlements";
    public static String ATTENDANCE_HISTORY="Attendance History";
    public static String PAYMENT_HISTORY="Payment History";


    public static String DELIVERY_BOY_ATTENDANCE_TABLE = "DeliveryBoyAttendanceTable";

    public static String[] PERMISSION_LIST = {"Select One", "Tea Break 1", "Tea Break 2", "Lunch/Dinner", "Permission"};

    public static String TEA_BREAK_1 = "Tea Break 1";
    public static String TEA_BREAK_2 = "Tea Break 2";
    public static String LUNCH_DINNER = "Lunch/Dinner";
    public static String PERMISSION = "Permission";

    public static String DATE_FORMAT = "MMMM dd, yyyy";
    public static String TIME_FORMAT = "hh:mm a";
    public static String DATE_TIME_FORMAT = "MMMM dd, yyyy";


    public static String REQUIRED_MSG = "Required";
    public static final int PASSWORD_LENGTH = 8;
    public static final int FIXED_LENGTH = 6;
    public static boolean BOOLEAN_FALSE = false;
    public static boolean BOOLEAN_TRUE = true;
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@%^&+=!;'])(?=\\S+$)[^\\s$.#{}$`,.\\\\\\\"|]{4,}$";
    public  static  final String VEHICLE_NUMBER_PATTERN="^[A-Z]{2}[\\s][0-9]{2}[\\s][A-Z]{2}[\\s][0-9]{4}$";
    public  static  final  String LICENSE_NUMBER_TAMILNADU= "^(([T]{1}[N]{1}[0-9]{2})( )|([A-Z]{2}-[0-9]{2}))((19|20)[0-9][0-9])[0-9]{7}$";
    public  static  final String LICENSE_NUMBER_INDIA="^(([A-Z]{2}[0-9]{2})( )|([A-Z]{2}-[0-9]{2}))((19|20)[0-9][0-9])[0-9]{7}$";

    public static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+(\\.+[a-z]+)?";
    public static String USER_NAME_PATTERN = "^[a-zA-Z0-9_-]{3,15}$";
    public static String FIRST_NAME_PATTERN = "[a-zA-Z ]+\\.?";
    public static String NAME_PATTERN = "[a-zA-Z .@/]+";
    public static String BANK_NAME_PATTERN = "[a-zA-Z][a-zA-Z ]+[a-zA-Z]$";
    public static String LAST_NAME_PATTERN = "[a-zA-Z ]+\\.?";
    public static String ALPHA_NUMERIC_PATTERN = "^(?=.*?[a-zA-Z])(?=.*?[0-9])[0-9a-zA-Z]+$";
    public static String ADDRESS_PATTERN = "^[a-zA-Z0-9\\s,'-]*$/";
    public static String NUMERIC_PATTERN = "[0-9]*";
    public static final String ITEM_PRICE_PATTERN = "[0-9]*$";
    public  static  String IFSC_PATTERN="^[A-Z]{4}0[A-Z0-9]{6}$";
    public static String AADHAR_PATTERN="^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$";
    public static String ALPHA_NUMERIC_STRING_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[/])[^\\s$`,.\\\\\\;:'\"|]{3,40}$";
    public static String DISCOUNT_NAME_PATTERN = "^[a-zA-Z0-9]+([\\s][a-zA-Z0-9]+)*$";
    public static String ASSIGNED_TO_STATUS = "assignedTo";
}
