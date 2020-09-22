package tshabalala.bongani.courierservice.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Pattern;

import tshabalala.bongani.courierservice.model.Request;
import tshabalala.bongani.courierservice.model.User;

public class Common {

    public static String TOKEN = "token";
    public static String ROLE = "role";
    public static User currentUser;
    public static Request currentRequest;
    public final static String UPDATE = "Update";
    public final static String DELETE = "Delete";
    public final static String USER_PHONE = "UserPhone";
    public final static String USER_PASSWORD = "UserPassword";
    public final static String USER_NAME = "UserName";
    public final static String USER_SURNAME = "UserSurname";
    public final static String USER_EMAIL = "UserEmail";
    public final static String USER_IDNUMBER = "UserIDNumber";
    public final static String CLIENT = "client";
    public final static String SERVER = "server";
    public final static String CATEGORY_ID = "categoryid";
    public static Double GPS_LONG    = 0.0;
    public static Double GPS_LAT     = 0.0;
    public static Integer TRACKING_INTERVAL     = 60;
    public static Integer TRACKING_INTERVAL_OLD = 60;
    public static Boolean NETWORK_ON = false;
    public static Boolean STARTUP = true;
    public static Activity TAG = null;

    public static String getMimeType(String url)
    {
        String parts[]=url.split("\\.");
        String extension=parts[parts.length-1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }


    public static String getStatus(String status) {
        switch (status) {
            case "0":
                return "Placed";
            case "1":
                return "Shipping";
            case "2":
                return "Shipped";
            default:
                return "Status Not Available";
        }
    }


    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean validatePhone(String phoneNo) {
        //validate phone numbers of format "1234567890"
        boolean valid ;
        String preg = "((?:\\+27|27)|0)(=99|72|82|73|83|74|84|86|87|79|81|71|76|60|61|62|63|64|65|66|67|78|)(\\d{7})";

        valid = Pattern.matches(preg, phoneNo);
        return valid;

    }

    public static String getGender(String id) {
        String M = "Male", F = "Female";

        int d = Integer.parseInt(id.substring(6, 7));
        if (d <= 4 || d == 0) {
            return F;
        } else {
            return M;
        }
    }

    public static String getDateOfBirth(String idnumber) {

            final String year = idnumber.substring(0, 2);
            String month = idnumber.substring(2, 4);
            String day = idnumber.substring(4, 6);

            int yearDate = Integer.parseInt(year);
            int monthDate = Integer.parseInt(month);
            int dayDate = Integer.parseInt(day);

            GregorianCalendar cal = new GregorianCalendar();

            int y, m, d, a;

            y = cal.get(Calendar.YEAR);
            m = cal.get(Calendar.MONTH) +1;
            d = cal.get(Calendar.DAY_OF_MONTH);
            cal.set(yearDate, monthDate, dayDate);

            a = y - cal.get(Calendar.YEAR);
            String strAge = String.valueOf(a).substring(2,String.valueOf(a).length());

            int age = Integer.parseInt(strAge);

            DateFormat dateFormat = new SimpleDateFormat("yyyy");
            Date date = new Date();

            String dateString = date != null ? dateFormat.format(date) : "N/A";

            Calendar calendar = Calendar.getInstance();
            String y1,y2,y3,y4,m1,m2,d1,d2,y3c,y4c,y3n4,y3n4c;
            y1 = "0";
            y2 = "0";


            y3c = String.valueOf(y).valueOf(String.valueOf(y).charAt(0));
            y4c = String.valueOf(y).valueOf(String.valueOf(y).charAt(1));

            y3n4c = y3c +y4c;


            int calcy3c = Integer.parseInt(y3n4c);

            int caly3n = Integer.parseInt(year);

            if(caly3n > calcy3c)
            {
                y1 = "1";

                y2 = "9";
            }

            if(caly3n >=0 && caly3n <= calcy3c)
            {
                y1 = "2";

                y2 = "0";
            }

            String yob = y1 +y2 + year;

            Log.w("HEllo","LEvels "+age+" grtg "+ yearDate + " ddf "+y+" ff "+year+ " fddf "+ yob);

            return day+"/"+month+"/"+yob;
        }

    public static int getAge(String idnumber) {

            final String year = idnumber.substring(0, 2);
            String month = idnumber.substring(2, 4);
            String day = idnumber.substring(4, 6);

            int yearDate = Integer.parseInt(year);
            int monthDate = Integer.parseInt(month);
            int dayDate = Integer.parseInt(day);

            GregorianCalendar cal = new GregorianCalendar();

            int y, m, d, a;

            y = cal.get(Calendar.YEAR);
            m = cal.get(Calendar.MONTH) +1;
            d = cal.get(Calendar.DAY_OF_MONTH);
            cal.set(yearDate, monthDate, dayDate);

            a = y - cal.get(Calendar.YEAR);
            String strAge = String.valueOf(a).substring(2,String.valueOf(a).length());

            int age = Integer.parseInt(strAge);

            DateFormat dateFormat = new SimpleDateFormat("yyyy");
            Date date = new Date();

            String dateString = date != null ? dateFormat.format(date) : "N/A";

            Calendar calendar = Calendar.getInstance();
            String y1,y2,y3,y4,m1,m2,d1,d2,y3c,y4c,y3n4,y3n4c;
            y1 = "0";
            y2 = "0";


            y3c = String.valueOf(y).valueOf(String.valueOf(y).charAt(0));
            y4c = String.valueOf(y).valueOf(String.valueOf(y).charAt(1));

            y3n4c = y3c +y4c;


            int calcy3c = Integer.parseInt(y3n4c);

            int caly3n = Integer.parseInt(year);

            if(caly3n > calcy3c)
            {
                y1 = "1";

                y2 = "9";
            }

            if(caly3n >=0 && caly3n <= calcy3c)
            {
                y1 = "2";

                y2 = "0";
            }

            String yob = y1 +y2 + year;

            Log.w("HEllo","LEvels "+age+" grtg "+ yearDate + " ddf "+y+" ff "+year+ " fddf "+ yob);

       return age;
    }

    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        boolean valid;
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        valid = Pattern.matches(emailPattern, email);

        return valid;
    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic

        return password.length() > 5;
    }

    public static boolean isIDValid(String number) {
        //TODO: Replace this with your own logic
        String idChecker = "([0-9]){2}([0-1][0-9])([0-3][0-9])([0-9]){4}([0-1])([0-9]){2}?";
        boolean valid;
        if (Pattern.matches(idChecker, number))
        {
            valid = number.length() == 13;

        }else {
            valid = false;
        }

        return valid;
    }

    public static boolean checkFutureIDValid(String number) {

        boolean valid = true;
        int y, m, d, a;
        String y1,y2,y3c,y4c,y3n4c;
        y1 = "0";
        y2 = "0";

        String year = number.substring(0, 2);
        String month = number.substring(2, 4);
        String day = number.substring(4, 6);

        int yearDate = Integer.parseInt(year);
        int monthDate = Integer.parseInt(month);
        int dayDate = Integer.parseInt(day);

        GregorianCalendar cal = new GregorianCalendar();

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH) +1;
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(yearDate, monthDate, dayDate);

        a = y - cal.get(Calendar.YEAR);
        y3c = String.valueOf(y).valueOf(String.valueOf(y).charAt(0));
        y4c = String.valueOf(y).valueOf(String.valueOf(y).charAt(1));

        y3n4c = y3c +y4c;


        int calcy3c = Integer.parseInt(y3n4c);

        int caly3n = Integer.parseInt(year);

        if(caly3n > calcy3c)
        {
            y1 = "1";

            y2 = "9";
        }

        if(caly3n >=0 && caly3n <= calcy3c)
        {
            y1 = "2";

            y2 = "0";
        }

        String yob = y1 +y2 + year;

        Log.w("HEllo","LEvels grtg "+ yearDate + " ddf "+y+" ff "+year+ " fddf "+ yob);

        if (Integer.parseInt(yob) > y)
        {

            valid = false;

        }

        if (monthDate == 0)
        {
            valid = false;
        }
        if(monthDate == 2)
        {
            if(dayDate == 30 || dayDate ==31)
            {
                valid = false;
            }
        }
        System.out.println("year is "+ d+" " + dayDate);
        if (dayDate == 0)
        {
            valid = false;
        }


        return valid;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    public static String getDateTime(Date timestamp){
        DateFormat dateformat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dateformat.setTimeZone(TimeZone.getDefault());
        String timeString = timestamp != null ? dateformat.format(timestamp) : "N/A";
        return timeString;
    }
}
