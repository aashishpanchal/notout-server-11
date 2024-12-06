package com.choic11.config;

import com.choic11.MyRequestinterceptor;
import com.choic11.jwt.JwtTokenUtil;
import com.choic11.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Config implements WebMvcConfigurer {

	@Autowired
	JwtTokenUtil jwtTokenUtil;
	@Autowired
	CustomerRepository customerRepository;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		WebMvcConfigurer.super.addInterceptors(registry);
		InterceptorRegistration addInterceptor = registry
				.addInterceptor(new MyRequestinterceptor(jwtTokenUtil, customerRepository));
		addInterceptor.excludePathPatterns(
				"/paymentcallback/*",
				"/payoutwebhook/**",
				"/adminapis/**",
				"/cricketcron/**",
				"/entitysp_cricket/**",
				"/testapis/**",
				"/soccercron/**",
				"/entitysp_soccer/**",
				"/basketballcron/**",
				"/entitysp_basketball/**"
				);
	}
}
