package com.choic11.curl;

import okhttp3.*;
import okhttp3.Request.Builder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Curl {
	static OkHttpClient okHttpClient;
	public static OkHttpClient getOkHttpClient() {
		if(okHttpClient==null){
			OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();
			okClientBuilder.connectTimeout(10*1000, TimeUnit.MILLISECONDS);
			okClientBuilder.readTimeout(30 * 1000, TimeUnit.MILLISECONDS);
			okClientBuilder.writeTimeout(15 * 1000, TimeUnit.MILLISECONDS);
			okHttpClient= okClientBuilder.build();
		}

		return okHttpClient;
	}

	public static Response excuteCurlRequest(String url, String method, JSONObject jsonBody,
			LinkedHashMap<String, String> headers) {

		OkHttpClient client = getOkHttpClient();
		RequestBody body = null;
		if (method.equals("POST")) {
			MediaType JSON = MediaType.parse("application/json; charset=utf-8");
			body = RequestBody.create(JSON, jsonBody.toString());
		}
		Builder requestBuilder = new Builder().url(url).method(method, body);
		if (headers != null && !headers.isEmpty()) {
			Set<String> keySet = headers.keySet();
			for (String key : keySet) {
				requestBuilder.addHeader(key, headers.get(key));
			}
		}
		try {
			return client.newCall(requestBuilder.build()).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public static Response excuteCurlRequest(String url, String method, JSONArray jsonBody,
			LinkedHashMap<String, String> headers) {

		OkHttpClient client = getOkHttpClient();
		RequestBody body = null;
		if (method.equals("POST")) {
			MediaType JSON = MediaType.parse("application/json; charset=utf-8");
			body = RequestBody.create(JSON, jsonBody.toString());
		}
		Builder requestBuilder = new Builder().url(url).method(method, body);
		if (headers != null && !headers.isEmpty()) {
			Set<String> keySet = headers.keySet();
			for (String key : keySet) {
				requestBuilder.addHeader(key, headers.get(key));
			}
		}
		try {
			return client.newCall(requestBuilder.build()).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public static Response excuteCurlRequestUrlEncode(String url, String method, JSONObject jsonBody,
			LinkedHashMap<String, String> headers) {

		OkHttpClient client = getOkHttpClient();
		RequestBody body = null;
		if (method.equals("POST") && jsonBody!=null && !jsonBody.isEmpty()) {
			FormBody.Builder builder = new FormBody.Builder();
			Set<String> keySet = jsonBody.keySet();
			for (String key : keySet) {
				builder.addEncoded(key, jsonBody.getString(key));
			}
			body = builder.build();
		}
		Builder requestBuilder = new Builder().url(url).method(method, body);
		requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded");
		if (headers != null && !headers.isEmpty()) {
			Set<String> keySet = headers.keySet();
			for (String key : keySet) {
				requestBuilder.addHeader(key, headers.get(key));
			}
		}
		try {
			return client.newCall(requestBuilder.build()).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}

