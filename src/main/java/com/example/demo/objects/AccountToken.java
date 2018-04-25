package com.example.demo.objects;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AccountToken implements Authentication {
	public String username;
	public String password;
	public boolean auth=false;
	@Override
	public String getName() {
		AccountToken.class.getName();
		return null;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return null;
	}
	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return password;
	}
	@Override
	public Object getDetails() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isAuthenticated() {
		// TODO Auto-generated method stub
		return auth;
	}
	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}
}
