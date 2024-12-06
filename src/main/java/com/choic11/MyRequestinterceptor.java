package com.choic11;

import com.choic11.controller.BaseController;
import com.choic11.jwt.JwtTokenUtil;
import com.choic11.model.customer.TblCustomer;
import com.choic11.model.response.BaseResponse;
import com.choic11.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MyRequestinterceptor extends HandlerInterceptorAdapter {

	CustomerRepository customerRepository;

	JwtTokenUtil jwtTokenUtil;

	public MyRequestinterceptor() {

	}

	public MyRequestinterceptor(JwtTokenUtil jwtTokenUtil, CustomerRepository customerRepository) {
		this.jwtTokenUtil = jwtTokenUtil;
		this.customerRepository = customerRepository;

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String path = request.getRequestURI();
		String methodType = request.getMethod();
		if (methodType.equals("POST")) {
			String contentType = request.getHeader("content-type");
			if (contentType == null || !contentType.contains("multipart")) {
				sendErrorResponse(response, 400, "content-type: multipart missing.", null);
				return false;
			}
		}
		List<String> requiredheaders = new ArrayList<String>();
		requiredheaders.add(BaseController.HEADER_lang);
		requiredheaders.add(BaseController.HEADER_deviceid);
		requiredheaders.add(BaseController.HEADER_devicetype);
		requiredheaders.add(BaseController.HEADER_deviceinfo);
		requiredheaders.add(BaseController.HEADER_appinfo);

		Enumeration<String> headers = request.getHeaderNames();
		if (headers != null) {
			while (headers.hasMoreElements()) {
				String a = headers.nextElement();
				if (!isEmpty(request.getHeader(a))) {
					requiredheaders.remove(a);
				}
			}
		}

		if (requiredheaders.size() > 0) {
			sendErrorResponse(response, 412, "Header missing " + requiredheaders.toString(), null);
			return false;

		}
		String deviceId=request.getHeader(BaseController.HEADER_deviceid);

		if (JwtTokenUtil.checkAuthenticatedUrl(path)) {
			String authToken = request.getHeader("Authorization");
			if (isEmpty(authToken)) {
				sendErrorResponse(response, 401, "Authorization header missing", null);

				return false;
			} else if (!authToken.startsWith("Bearer ")) {
				sendErrorResponse(response, 401, "Authorization token invalid", null);

				return false;
			} else {
				try {
					authToken = authToken.substring(7);
					if (!jwtTokenUtil.validateToken(authToken)) {
						sendErrorResponse(response, 401, "Authorization token expired", null);

						return false;
					} else {
						String userId = jwtTokenUtil.getUserIdFromToken(authToken);


						TblCustomer customerById = customerRepository.getCustomerByIdForAuth(Integer.parseInt(userId), deviceId);
						if (customerById == null) {
							sendErrorResponse(response, 401, "User not found.", null);
							return false;
						}
						if (!customerById.getStatus().equals("A")) {
							sendErrorResponse(response, 412, "Account is deactivated.", null);
							return false;
						}
						if (customerById.getIsDeleted().equals("Y")) {
							sendErrorResponse(response, 412, "Account is deleted by admin.", null);
							return false;
						}
						if (!customerById.isValidCustomerLoginId()) {
							sendErrorResponse(response, 412, "Authorization token expired2.", null);
							return false;
						}

						request.setAttribute("authUserId", userId);

					}
				} catch (MalformedJwtException exception) {
					sendErrorResponse(response, 401, "Authorization token  invalid", null);

					return false;

				}

			}
		}

		return super.preHandle(request, response, handler);

	}

	public void sendErrorResponse(HttpServletResponse response, int status, String message, Object data)
			throws Exception {
		response.setContentType("application/json");
		if (status == 401) {
			response.setStatus(status, "Invalid Request");
		} else if (status == 412) {
			response.setStatus(status, "UnAuthorzied");
		} else {
			response.setStatus(status);
		}

		response.getWriter()
				.write(new ObjectMapper().writeValueAsString(new BaseResponse(status, true, message, null)));
	}

	public ResponseEntity<Object> echoRespose(Object data, HttpStatus ok) {
		return ResponseEntity.status(ok).body(data);
	}

	public boolean isEmpty(String data) {
		return data == null || data.length() == 0;
	}
}
