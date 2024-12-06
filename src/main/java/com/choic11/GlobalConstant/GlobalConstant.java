package com.choic11.GlobalConstant;

import java.util.HashMap;

public class GlobalConstant {
    static {
        HashMap<String, String> WALLET_TYPE1 = new HashMap<String, String>();
        WALLET_TYPE1.put("deposit_wallet", "Deposit Wallet");
        WALLET_TYPE1.put("winning_wallet", "Winning Wallet");
        WALLET_TYPE1.put("bonus_wallet", "Bonus Wallet");
        WALLET_TYPE = WALLET_TYPE1;
    }

    public static final String APP_NAME = "CHOIC11";
    public static final String PROJECTTYPE = "TEST"; //PROD. TEST
    public static final String BUILDTYPE = "PROD"; //PROD. TEST
    public static final boolean CRONENABLED = false;
    public static final boolean CRONENABLED_SOCCER = false;
    public static final boolean CRONENABLED_BASKETBALL = false;
    public static final boolean CRONENABLEDFORPROJECT = false;
    public static final String SUBDIRECTORY_DEV = "";
    public static final String SUBDIRECTORY_PROD = "choic11project";

    public static final int SPORT_ID_CRICKET = 0;
    public static final int SPORT_ID_KABADDI=1;
    public static final int SPORT_ID_SOCCER=2;
    public static final int SPORT_ID_BASKETBALL=3;

    public static final String SITE_TITLE = APP_NAME;
    public static final String ADMIN_URL = "https://choic11.com/adminpanel/";
    public static final String ADMIN_URL_STAG = "http://139.162.28.58/choic11/adminpanel/";
    public static final String WEBSITE_URL_SHOW = "https://choic11.com";
    public static final String WIN_MAIL_LOGO = "https://choic11.com/adminpanel/img/win_mail_logo.png";
    public static final String PAYMENT_PAGE_LOADER = "https://choic11.com/adminpanel/img/ajax-loader.gif";

    public static final String ROOT_DIRECTORY = "/var/www/html/uploads/";
    public static final String CONTEST_PDF_LOCAL_DIR = "/opt/tomcat/temp/contestpdf";
    public static final String IMAGE_UPLOAD_TYPE = "BUCKET";

    public static final String WINNING_BREAKUP_MESSAGE = "Note: The actual prize money may be different than the prize money mentionaed above if there is a tie for any of the winning positions. Check FAQs for further details. As per government regulations, a tax of {TOTAL_TAX}% will be deducted if an individual wins more than Rs. 10,000";
    public static final String JOIN_CONTEST_MESSAGE = "By joining this contest, you accept Choic11's T&C and confirm that you are not a resident of Tamil Nadu, Andhra Pradesh, Assam, Odisha, Telangana, Nagaland or Sikkim.";
    public static final String PROFILE_UPDATE_MESSAGE = "To play in Choic11's pay-to-play contests, you need to be 18 years or above, and not a resident of Tamil Nadu, Andhra Pradesh, Assam, Odisha, Telangana, Nagaland or Sikkim.";

    public static final String CONTEST_SHARE_MESSAGE = "You’ve been challenged!\n\nThink you can beat me? Join the contest on "
            + APP_NAME
            + " for the {MATCH_NAME} match and prove it!\n\nUse Contest Code {CONTES_SLUG} & join the action NOW! Or download app from {APP_DYNAMIC_LINK}";
    public static final HashMap<String, String> WALLET_TYPE;
    public static final String CURRENCY_SYMBOL = "₹";
    public static final String APK_DOWNLOAD_URL = "https://choic11.com/choic11apk";
    public static final String APKURL = "https://choic11.com/choic11.apk";

    public static final Float[] MULTIPLIER_ARRAY = new Float[]{2f, 1.5f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};
    public static final Float[] MULTIPLIER_ARRAY_SOCCER = new Float[]{2f, 1.5f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};
    public static final Float[] MULTIPLIER_ARRAY_BASKETBALL = new Float[]{2f, 1.5f, 1f, 1f, 1f, 1f, 1f, 1f};

    public static String getProjectSubDirectory() {
        return isBuildTypeProd() ? SUBDIRECTORY_PROD : SUBDIRECTORY_DEV;
    }

    public static boolean isProjectTypeProd() {
        return PROJECTTYPE.equals("PROD");
    }

    public static boolean isBuildTypeProd() {
        return BUILDTYPE.equals("PROD");
    }

    public static boolean isCronEnabled() {
        return CRONENABLED;
    }

    public static boolean isCronEnabledSoccer() {
        return CRONENABLED_SOCCER;
    }
    public static boolean isCronEnabledBasketball() {
        return CRONENABLED_BASKETBALL;
    }

    public static boolean isCronenabledforproject() {
        return CRONENABLEDFORPROJECT;
    }


    public static String getContestPdfLocalDirectory() {
        return isBuildTypeProd() ? CONTEST_PDF_LOCAL_DIR + "/" : "";
    }

}
