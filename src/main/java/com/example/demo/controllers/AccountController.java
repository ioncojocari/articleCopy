package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.AccountManager;
import com.example.demo.objects.Account;
import com.example.demo.objects.Comment;
import com.example.demo.objects.Response;

@RestController
@ComponentScan
public class AccountController {
	//public static final String API_PREFIX="/api/account";
	@Autowired
	AccountManager manager;
	@GetMapping("/api/account/{username}/{password}")
	public boolean isCorrect(@PathVariable("username") String username,
			@PathVariable("password") String password) {
		return manager.checkLogin(username, password);
	}
	
	@PostMapping("/api/account/{username}/{password}")
	public Response registerNewUser(@PathVariable("username") String username,
			@PathVariable("password") String password) {
		Response rs=new Response();
		Account account=new Account();
		System.out.println("password"+password);
		account.setUsername(username);
		account.setPassword(password);
		Map<String,String> errors=manager.createAccount(account);
		if(errors!=null&&errors.size()>0) {
			rs.setSuccessful(false);
			rs.setAdditionalProperty("errors", errors);
		}else {
			rs.setSuccessful(true);
		}
		
		return rs;
	}
	
	@PostMapping("/api/comment")
	public Response addComment(@RequestHeader("authorization") List<String> headers,@Param("message") Comment message) {
		Response rs=new Response();
		String username=extractUsername(headers.get(0));
		
		return rs;
	}
	
	private String extractUsername(String header) {
		String baseStr=header.substring(6);
		String result=new String(Base64Utils.decodeFromString(baseStr));
		String[] strings=result.split(":");
		return strings[0];
	}
}
