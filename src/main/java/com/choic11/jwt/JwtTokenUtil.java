package com.choic11.jwt;

import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.model.customer.TblCustomer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
	private static final long serialVersionUID = -2550185165626007488L;

	public static final long JWT_TOKEN_VALIDITY = 7 * 24 * 60 * 60;
	public static final String[] excludeUrls = new String[] { "/apis/", "/error" };

	private String secret = TextCodec.BASE64.encode("javainuse");

	public static boolean checkAuthenticatedUrl(String url) {
		for (String urls : excludeUrls) {
			if (url.startsWith(urls) || url.startsWith("/" + GlobalConstant.SUBDIRECTORY_PROD + urls)) {
				return false;
			}
		}

		return true;
	}

	public String getUserIdFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String generateToken(TblCustomer customerDetails) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, String.valueOf(customerDetails.getId()));
	}

	private String doGenerateToken(Map<String, Object> claims, String userId) {

		return Jwts.builder().setClaims(claims).setSubject(userId).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	public Boolean validateToken(String token) {
		try {
			return !isTokenExpired(token);
		} catch (ExpiredJwtException e) {

		}

		return false;

	}
}
