package com.choic11.GlobalConstant;

import java.util.HashMap;

public class FileUploadConstant {

	static {
		HashMap<String, String> awsConstant = AWSConstant.getAwsConstant();
		FILES_UPLOAD_URL=awsConstant.get("AWS_URL");
	}

	public static final String FILES_UPLOAD_DIR = "";
	public static final String FILES_UPLOAD_URL;
	public static final String BTB_PRIFIX = "choic11/";
	public static final String APP_ICON = FILES_UPLOAD_DIR + BTB_PRIFIX + "app/logo.png";

	public static final String GAME_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "game/";
	public static final String GAME_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "game/thumbnail/";
	public static final String GAME_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "game/large/";
	public static final String GAME_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "game/thumbnail/";
	public static final String GAME_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "game/large/";

	public static final String CUSTOMER_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "customer/";
	public static final String CUSTOMER_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "customer/thumbnail/";
	public static final String CUSTOMER_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "customer/large/";
	public static final String CUSTOMER_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "customer/thumbnail/";
	public static final String CUSTOMER_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "customer/large/";
	
	
	public static final String CUSTOMERGALLERY_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "customer_gallery/";
	public static final String CUSTOMERGALLERY_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "customer_gallery/thumbnail/";
	public static final String CUSTOMERGALLERY_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "customer_gallery/large/";
	public static final String CUSTOMERGALLERY_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "customer_gallery/thumbnail/";
	public static final String CUSTOMERGALLERY_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "customer_gallery/large/";

	public static final String PLAYER_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "player/";
	public static final String PLAYER_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "player/thumbnail/";
	public static final String PLAYER_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "player/large/";
	public static final String PLAYER_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "player/thumbnail/";
	public static final String PLAYER_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "player/large/";

	public static final String TEAMCRICKET_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "cricket_team/";
	public static final String TEAMCRICKET_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "cricket_team/thumbnail/";
	public static final String TEAMCRICKET_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "cricket_team/large/";
	public static final String TEAMCRICKET_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "cricket_team/thumbnail/";
	public static final String TEAMCRICKET_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "cricket_team/large/";

	public static final String TEAMSOCCER_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "cricket_team/";
	public static final String TEAMSOCCER_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "cricket_team/thumbnail/";
	public static final String TEAMSOCCER_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "cricket_team/large/";
	public static final String TEAMSOCCER_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "cricket_team/thumbnail/";
	public static final String TEAMSOCCER_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "cricket_team/large/";

	public static final String TEAMBASKETBALL_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "cricket_team/";
	public static final String TEAMBASKETBALL_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "cricket_team/thumbnail/";
	public static final String TEAMBASKETBALL_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "cricket_team/large/";
	public static final String TEAMBASKETBALL_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "cricket_team/thumbnail/";
	public static final String TEAMBASKETBALL_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "cricket_team/large/";


	public static final String SLIDER_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "sliders/";
	public static final String SLIDER_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "sliders/thumbnail/";
	public static final String SLIDER_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "sliders/large/";
	public static final String SLIDER_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "sliders/thumbnail/";
	public static final String SLIDER_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "sliders/large/";

	public static final String CONTEXTCATEGORY_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "context_categories/";
	public static final String CONTEXTCATEGORY_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX
			+ "context_categories/thumbnail/";
	public static final String CONTEXTCATEGORY_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX
			+ "context_categories/large/";
	public static final String CONTEXTCATEGORY_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX
			+ "context_categories/thumbnail/";
	public static final String CONTEXTCATEGORY_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX
			+ "context_categories/large/";

	public static final String PANCARD_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "pancards/";
	public static final String PANCARD_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "pancards/thumbnail/";
	public static final String PANCARD_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "pancards/large/";
	public static final String PANCARD_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "pancards/thumbnail/";
	public static final String PANCARD_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "pancards/large/";

	public static final String BANK_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "bankproof/";
	public static final String BANK_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "bankproof/thumbnail/";
	public static final String BANK_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "bankproof/large/";
	public static final String BANK_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "bankproof/thumbnail/";
	public static final String BANK_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "bankproof/large/";

	public static final String NOTIFICATION_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "notifications/";
	public static final String NOTIFICATION_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX
			+ "notifications/thumbnail/";
	public static final String NOTIFICATION_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "notifications/large/";
	public static final String NOTIFICATION_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX
			+ "notifications/thumbnail/";
	public static final String NOTIFICATION_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "notifications/large/";

	public static final String EMAILS_NOTIFICATION_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "emails_notifications/";
	public static final String EMAILS_NOTIFICATION_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX
			+ "emails_notifications/thumbnail/";
	public static final String EMAILS_NOTIFICATION_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX
			+ "emails_notifications/large/";
	public static final String EMAILS_NOTIFICATION_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX
			+ "emails_notifications/thumbnail/";
	public static final String EMAILS_NOTIFICATION_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX
			+ "emails_notifications/large/";

	public static final String REFER_EARN_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "refer_earn/";
	public static final String REFER_EARN_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "refer_earn/thumbnail/";
	public static final String REFER_EARN_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "refer_earn/large/";
	public static final String REFER_EARN_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "refer_earn/thumbnail/";
	public static final String REFER_EARN_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "refer_earn/large/";

	public static final String MATCH_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "match/";
	public static final String MATCH_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "match/thumbnail/";
	public static final String MATCH_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "match/large/";
	public static final String MATCH_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "match/thumbnail/";
	public static final String MATCH_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "match/large/";

	public static final String REACTION_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "reaction/";
	public static final String REACTION_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "reaction/thumbnail/";
	public static final String REACTION_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "reaction/large/";
	public static final String REACTION_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "reaction/thumbnail/";
	public static final String REACTION_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "reaction/large/";
	
	
	public static final String FEED_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "context_categories/";
	public static final String FEED_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "context_categories/thumbnail/";
	public static final String FEED_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "context_categories/large/";
	public static final String FEED_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "context_categories/thumbnail/";
	public static final String FEED_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "context_categories/large/";

	public static final String APP_ICON_CUSTOMIZE_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "app_icon_customize/";
	public static final String APP_ICON_CUSTOMIZE_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX
			+ "app_icon_customize/thumbnail/";
	public static final String APP_ICON_CUSTOMIZE_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX
			+ "app_icon_customize/large/";
	public static final String APP_ICON_CUSTOMIZE_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX
			+ "app_icon_customize/thumbnail/";
	public static final String APP_ICON_CUSTOMIZE_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX
			+ "app_icon_customize/large/";

	public static final String QUOTATIONS_IMAGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "quotations/";
	public static final String QUOTATIONS_IMAGE_THUMB_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "quotations/thumbnail/";
	public static final String QUOTATIONS_IMAGE_LARGE_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "quotations/large/";
	public static final String QUOTATIONS_IMAGE_THUMB_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "quotations/thumbnail/";
	public static final String QUOTATIONS_IMAGE_LARGE_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "quotations/large/";

	public static final String PDF_PATH = FILES_UPLOAD_DIR + BTB_PRIFIX + "contest_pdf/";
	public static final String PDF_URL = FILES_UPLOAD_URL + BTB_PRIFIX + "contest_pdf/";

	public static final String LOCAL_THUMB_PATH = GlobalConstant.ROOT_DIRECTORY + "uploads/";

	public static final String CONTEST_PDF_LOGO_URL = "https://choic11.com/adminpanel/img/logo.png";
	public static final String NO_IMG_URL = "https://choic11.com/adminpanel/img/noimage.png";
	public static final String NO_IMG_URL_TEAM = "https://choic11.com/adminpanel/img/no_image_team.png";
	public static final String NO_IMG_URL_PLAYER = "https://choic11.com/adminpanel/img/no_image_player.png";

}
