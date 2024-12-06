package com.choic11;

import com.choic11.GlobalConstant.GlobalConstant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class Util {

    public static void printLog(String tag, String message) {
        if (!GlobalConstant.isProjectTypeProd()) {
            System.out.println(tag + " : " + message);
        }
    }


    public static void printLog2(String tag, String message) {
        System.out.println(tag + " : " + message);

    }

    public static boolean isEmpty(String value) {
        return value == null || value.equals("");
    }

    public static String generateImageUrl(String image, String prefix, String placeholder) {
        if (!isEmpty(image) && !image.equals("0")) {
            return prefix + image;
        }
        return placeholder;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static Long getCurrentTime() {
        Date date = new Date();
        return date.getTime() / 1000;
    }

    public static String getFormatedDate(String date, String inFormate, String outFormate) {
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat(inFormate);
            SimpleDateFormat sdf2 = new SimpleDateFormat(outFormate);
            return sdf2.format(sdf1.parse(date));
        } catch (Exception ignore) {

        }
        return "";
    }

    public static String getFormatedDate(long date, String formate) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            SimpleDateFormat sdf2 = new SimpleDateFormat(formate);
            return sdf2.format(calendar.getTime());
        } catch (Exception ignore) {

        }
        return "";
    }

    public static long getFormatedDateMilli(String date, String formate) {
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat(formate);
            return sdf1.parse(date).getTime();
        } catch (Exception ignore) {

        }
        return 0;
    }


    public static Date getFormattedDateObject(String date, String formate) {
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat(formate);
            return sdf1.parse(date);
        } catch (Exception ignore) {

        }
        return null;
    }

    public static long getFormatedDateMilliTimeZone(String date, String formate, String timeZone) {
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat(formate);
            sdf1.setTimeZone(TimeZone.getTimeZone(timeZone));
            return sdf1.parse(date).getTime() / 1000;
        } catch (Exception ignore) {

        }
        return 0;
    }

    public static String convertToMD5(String input) {
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getAlphaNumericString(int n) {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            int index = (int) (AlphaNumericString.length() * Math.random());

            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }

    public static String encodeData(String data) {
        int position = 2;
        int sposition = 3;

        String string = getAlphaNumericString(4);
        String string1 = getAlphaNumericString(4);
        String newstring = new StringBuffer(data).insert(position, string).toString();
        String received_text = new StringBuffer(newstring).insert(newstring.length() - sposition, string1).toString();
        received_text = Base64.getEncoder().encodeToString(received_text.getBytes());

        BigInteger currentTime = BigInteger.valueOf(Util.getCurrentTime());
        String returnString = received_text + "----" + currentTime;
        returnString = Base64.getEncoder().encodeToString(returnString.toString().getBytes());

        String string3 = getAlphaNumericString(4);
        String string4 = getAlphaNumericString(4);
        newstring = new StringBuffer(returnString).insert(position, string3).toString();
        received_text = new StringBuffer(newstring).insert(newstring.length() - sposition, string4).toString();

        return received_text;

    }

    public static String decodeData(String data) {
        if (data != null && data.length() > 7) {
            StringBuilder sb = new StringBuilder(data);
            int i = data.length() - 7;
            sb.delete(i, i + 4);
            i = 2;
            sb.delete(i, i + 4);

            data = new String(Base64.getDecoder().decode(sb.toString().getBytes()));
            data = data.split("----")[0];
            data = new String(Base64.getDecoder().decode(data.getBytes()));
            sb = new StringBuilder(data);
            i = data.length() - 7;
            sb.delete(i, i + 4);
            i = 2;
            sb.delete(i, i + 4);
            return sb.toString();
        }
        return data;
    }

    public static String getSHA512(String input) {
        String toReturn = null;
        try {
            byte[] hashseq = input.getBytes();
            StringBuffer hexString = new StringBuffer();
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException nsae) {
        }
        return toReturn;
    }

    public static String getHmac256(String key, String input) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key_spec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key_spec);

            byte[] bytes = sha256_HMAC.doFinal(input.getBytes());

            return Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(input.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static float numberFormate(float value, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        return bd.floatValue();
    }


    public static String getPlayerFormattedName(String name) {
        if (name != null) {
            name = name.trim();
        }
        if (Util.isEmpty(name)) {
            return "";
        }
        String[] playerRealName = name.split(" ", 2);
        if (playerRealName.length > 1) {
            name = playerRealName[0].substring(0, 1) + " " + playerRealName[1];
        }
        return name;
    }


    public static List<HashMap<String, Object>> generateWeeks(long from, long to) {

        from = from * 1000;
        to = to * 1000;

        String fromDate = Util.getFormatedDate(from, "yyyy-MM-dd");
        String toDate = Util.getFormatedDate(to, "yyyy-MM-dd");

        long fromMilli = Util.getFormatedDateMilli(fromDate, "yyyy-MM-dd");
        long toMilli = Util.getFormatedDateMilli(toDate, "yyyy-MM-dd");

        List<HashMap<String, Object>> weeks = new ArrayList<HashMap<String, Object>>();
        int i = 1;
        while (fromMilli <= toMilli) {

            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(fromMilli);
            int dayOfWeek = instance.get(Calendar.DAY_OF_WEEK);

            String endDate = Util.getFormatedDate(instance.getTimeInMillis(), "yyyy-MM-dd");
            if (dayOfWeek == 1) {


            } else {
                int needDays = (7 - dayOfWeek) + 1;
                instance.add(Calendar.DAY_OF_MONTH, needDays);
                endDate = Util.getFormatedDate(instance.getTimeInMillis(), "yyyy-MM-dd");
            }

            String afromDate = "";
            if (dayOfWeek == 1) {
                Calendar instance1 = Calendar.getInstance();
                instance1.setTimeInMillis(fromMilli);
                instance1.add(Calendar.DAY_OF_MONTH, -6);
                afromDate = Util.getFormatedDate(instance1.getTimeInMillis(), "yyyy-MM-dd");
            } else {
                Calendar instance1 = Calendar.getInstance();
                instance1.setTimeInMillis(fromMilli);
                instance1.add(Calendar.DAY_OF_MONTH, -(dayOfWeek - 2));
                afromDate = Util.getFormatedDate(instance1.getTimeInMillis(), "yyyy-MM-dd");
            }

            String searchdate = afromDate + "/" + endDate;
            String fromDate_s = fromDate + " 00:00:00";
            String endDate_s = endDate + " 23:59:59";

            HashMap<String, Object> weekNo = new HashMap<String, Object>();
            weekNo.put("fromDate", fromDate);
            weekNo.put("endDate", endDate);
            weekNo.put("searchdate", searchdate);
            weekNo.put("week_no", i);
            weekNo.put("fromDate_timestamp", Util.getFormatedDateMilliTimeZone(fromDate_s, "yyyy-MM-dd HH:mm:ss", "IST"));
            weekNo.put("endDate_timestamp", Util.getFormatedDateMilliTimeZone(endDate_s, "yyyy-MM-dd HH:mm:ss", "IST"));

            weeks.add(weekNo);

            long endDateMilliNext = Util.getFormatedDateMilli(endDate, "yyyy-MM-dd");

            Calendar instance2 = Calendar.getInstance();
            instance2.setTimeInMillis(endDateMilliNext);
            instance2.add(Calendar.DAY_OF_MONTH, 1);

            endDateMilliNext = instance2.getTimeInMillis();
            fromMilli = endDateMilliNext;

            fromDate = Util.getFormatedDate(fromMilli, "yyyy-MM-dd");
            i++;
        }

        return weeks;
    }

}
