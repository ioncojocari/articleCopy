package com.example.demo;

import java.security.SecureRandom;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.models.AccountManager;
import com.example.demo.objects.Account;
import com.example.demo.objects.AccountToken;
import com.example.demo.objects.AccountValidator;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class APPConfig {

	@Autowired
	public AccountManager manager;
	@Bean 
	public ObjectMapper getMapper() {
		ObjectMapper mapper=new ObjectMapper() ;
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true); 
		
		return mapper;
	}
	
	@Bean
	public PasswordEncoder  passwordEncoder() {
		byte[] bytes= { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 54, 55,
				56, 57, 58, 59, 60, 61, 62, 63, -1, -1, -1, -1, -1, -1, -1, 2, 3, 4, 5, 6, 7,
				8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
				-1, -1, -1, -1, -1, -1, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
				41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, -1, -1, -1, -1, -1 };
		SecureRandom random=new SecureRandom(bytes);
	    return new BCryptPasswordEncoder(16,random);
	}
	
	@Bean 
	@Primary
	public AccountManager getAccountManager() {
		return new AccountManager();
	}
	
	@Bean 
	
	public AccountValidator getAccountValidator() {
		return new AccountValidator();
	}
	
	@Bean
	
	public AuthenticationProvider getAuthProvider() {
		return new AuthenticationProvider() {
			
			@Override
			public boolean supports(Class<?> authentication) {
				
				if(authentication.equals(UsernamePasswordAuthenticationToken.class)) {
					return true;
				}
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				   String username = authentication.getName();
				   System.out.println("username:"+username);
			       //String password = authentication.getCredentials().toString();
			      String password=(String)authentication.getCredentials();
			       System.out.println("password:"+password);
			        if(manager.checkLogin(username, password)) {
			        	UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(username,password,new ArrayList<>());
			        	System.out.println("logged in");
			        	return token;
			        }
				return null;
			}
		};
	}
	
}
