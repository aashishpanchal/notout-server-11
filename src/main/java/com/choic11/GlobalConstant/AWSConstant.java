package com.choic11.GlobalConstant;

import java.util.HashMap;

public class AWSConstant {

    public static final String AWS_KEY = "AKIAWR7EE3QN4CSLW53F";
    public static final String AWS_SECRET = "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";
    public static final String AWS_REGION = "ap-south-1";
    public static final String AWS_BUCKET = "choic11stag";
    public static final String AWS_URL = "https://" + AWS_BUCKET + ".s3.amazonaws.com/";


    public static final String AWS_KEY_LIVE = "AKIAWR7EE3QN4CSLW53F";
    public static final String AWS_SECRET_LIVE = "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";
    public static final String AWS_REGION_LIVE = "ap-south-1";
    public static final String AWS_BUCKET_LIVE = "choic11";
    public static final String AWS_URL_LIVE = "https://" + AWS_BUCKET_LIVE + ".s3.amazonaws.com/";


    public static final HashMap<String, String> getAwsConstant() {

        HashMap<String, String> output = new HashMap<String, String>();
        if (!GlobalConstant.isProjectTypeProd()) {
            output.put("AWS_KEY", AWS_KEY);
            output.put("AWS_SECRET", AWS_SECRET);
            output.put("AWS_REGION", AWS_REGION);
            output.put("AWS_BUCKET", AWS_BUCKET);
            output.put("AWS_URL", AWS_URL);
        } else {
            output.put("AWS_KEY", AWS_KEY_LIVE);
            output.put("AWS_SECRET", AWS_SECRET_LIVE);
            output.put("AWS_REGION", AWS_REGION_LIVE);
            output.put("AWS_BUCKET", AWS_BUCKET_LIVE);
            output.put("AWS_URL", AWS_URL_LIVE);
        }

        return output;

    }

}
