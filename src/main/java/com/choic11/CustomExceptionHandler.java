package com.choic11;

import com.choic11.model.response.BaseResponse;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
	    return echoRespose(new BaseResponse(0, true, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
	}
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
	    return echoRespose(new BaseResponse(0, true, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
	    
	}
	
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
	    return echoRespose(new BaseResponse(0, true, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
	    return echoRespose(new BaseResponse(0, true, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
	}
	
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		return super.handleNoHandlerFoundException(ex, headers, status, request);
	}
	

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}
	
	
	public ResponseEntity<Object> echoRespose(Object data, HttpStatus ok) {
		return ResponseEntity.status(ok).body(data);
	}

}
