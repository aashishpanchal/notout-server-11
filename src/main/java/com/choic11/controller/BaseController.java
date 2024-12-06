package com.choic11.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class BaseController {

	public static final String HEADER_lang="lang";
	public static final String HEADER_deviceid="deviceid";
	public static final String HEADER_devicetype="devicetype";
	public static final String HEADER_deviceinfo="deviceinfo";
	public static final String HEADER_appinfo="appinfo";
	
	public ResponseEntity<Object> echoRespose(Object data, HttpStatus ok) {
		return ResponseEntity.status(ok).body(data);
	}
	public ResponseEntity<Object> echoResposeString(Object data, HttpStatus ok) {
		return ResponseEntity.status(ok).contentType(MediaType.TEXT_HTML).body(data);
	}
	
	
	
}
