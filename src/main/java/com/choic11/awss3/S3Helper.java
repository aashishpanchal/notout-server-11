package com.choic11.awss3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.choic11.GlobalConstant.AWSConstant;

import java.io.File;
import java.util.HashMap;

public class S3Helper {

    private static AmazonS3 getAmazonS3Cient() {
        try {
            HashMap<String, String> awsConstant = AWSConstant.getAwsConstant();

            final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(awsConstant.get("AWS_KEY"),
                    awsConstant.get("AWS_SECRET"));
            return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(awsConstant.get("AWS_REGION")))
                    .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials)).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void uploadFileToS3Bucket(final String bucketPath, final String uniqueFileName, final File file,
                                            final S3HelperListener s3HelperListener) {
        AmazonS3 amazonS3 = getAmazonS3Cient();
        if (amazonS3 != null) {
            try {
                final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketPath, uniqueFileName, file);
                putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
                amazonS3.putObject(putObjectRequest);
                if (s3HelperListener != null)
                    s3HelperListener.onFileUploadComplete(true,
                            bucketPath + "/" + uniqueFileName + " upload successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                if (s3HelperListener != null)
                    s3HelperListener.onFileUploadComplete(false, e.getMessage());
            }
        } else {
            if (s3HelperListener != null)
                s3HelperListener.onFileUploadComplete(false, "amazonS3 client is not found.");
        }
    }

    public interface S3HelperListener {
        void onFileUploadComplete(boolean success, String message);
    }
}
