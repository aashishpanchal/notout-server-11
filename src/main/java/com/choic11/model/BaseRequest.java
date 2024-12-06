package com.choic11.model;

import com.choic11.controller.BaseController;
import com.choic11.model.response.BaseResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class BaseRequest {
	public String REQUEST_DOMAIN;
	public Integer authUserId;
	public String HEADER_lang;
	public String HEADER_deviceid;
	public String HEADER_devicetype;
	public String HEADER_deviceinfo;
	public String HEADER_appinfo;
	public String HEADER_ipAddress;
	public Object data;
	public HashMap<String, String> params;

	public BaseRequest(HttpServletRequest request) {
		if (request == null) {
			return;
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(request.getScheme() + "://");
		stringBuilder.append(request.getServerName());
		if(request.getServerPort()!=80 && request.getServerPort()!=443) {
			stringBuilder.append(":" + request.getServerPort());
		}
		

		REQUEST_DOMAIN = stringBuilder.toString();


		if (request.getAttribute("authUserId") != null) {
			this.authUserId = Integer.parseInt((String) request.getAttribute("authUserId"));
		}

		this.HEADER_lang = request.getHeader(BaseController.HEADER_lang);
		this.HEADER_deviceid = request.getHeader(BaseController.HEADER_deviceid);
		this.HEADER_devicetype = request.getHeader(BaseController.HEADER_devicetype);
		this.HEADER_deviceinfo = request.getHeader(BaseController.HEADER_deviceinfo);
		this.HEADER_appinfo = request.getHeader(BaseController.HEADER_appinfo);
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		this.HEADER_ipAddress = ipAddress;

		params = new HashMap<String, String>();

		Map<String, String[]> parameters = request.getParameterMap();
		Set<String> keySet = parameters.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			params.put(key, parameters.get(key)[0]);
		}
	}

	public boolean hasParam(String key) {
		return params.containsKey(key);
	}

	public String getParam(String key) {
		return params.get(key);
	}

	public void setParam(String key, String value) {
		params.put(key, value);
	}

	public ArrayList<String> verifyRequiredParams(String... requiredParams) {
		ArrayList<String> errorFields = new ArrayList<String>();

		for (String key : requiredParams) {
			if (!params.containsKey(key) || params.get(key) == null || params.get(key).trim().length() == 0) {
				errorFields.add(key);
			}
		}

		return errorFields;
	}

	public BaseResponse generateRequiredParamsResponse(ArrayList<String> errorFields) {
		String message = "Required field(s) " + errorFields.toString() + "  is missing or empty.";
		return new BaseResponse(1, true, message, null);
	}

}
