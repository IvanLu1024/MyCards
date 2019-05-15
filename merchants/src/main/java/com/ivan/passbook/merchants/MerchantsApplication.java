package com.ivan.passbook.merchants;

import com.ivan.passbook.merchants.security.AuthCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class MerchantsApplication extends WebMvcConfigurerAdapter {

	@Autowired
	private AuthCheckInterceptor authCheckInterceptor;

	public static void main(String[] args) {
		SpringApplication.run(MerchantsApplication.class, args);
	}

	/**
	 * 添加自定义的拦截器
	 * @param registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authCheckInterceptor)
				.addPathPatterns("/merchants/**");
		super.addInterceptors(registry);
	}
}
