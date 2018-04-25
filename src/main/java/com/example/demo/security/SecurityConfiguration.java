package com.example.demo.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
	 
	@Configuration
	@Order(1)
	@ComponentScan("com.example.demo")
	public class WebsiteSecurityConfiguration extends WebSecurityConfigurerAdapter {
	 
	    @Autowired
	    private DataSource data;
	    @Override
	    protected void configure(AuthenticationManagerBuilder auth)
	      throws Exception {
	    	auth.jdbcAuthentication().dataSource(data).usersByUsernameQuery(
	    			"select username,password, enabled from users where username=?")
	    		.authoritiesByUsernameQuery(
	    			"select username, role from user_roles where username=?");
	    }
	    
	    @Override
	    public void configure(WebSecurity web) throws Exception {
	        web
	            .ignoring()
	                .antMatchers("/api/comment/**","/api/account/**");
	    }
	 
	    @Override
	    protected void configure(HttpSecurity http) throws Exception { 
	        http
	        .csrf().disable()
	        .authorizeRequests()
	        .antMatchers("/api/admin/**" ,"/admin/**").hasRole("ADMIN")
	        .antMatchers("/api/articles/**","/api/files/**").permitAll()
	        .anyRequest().authenticated();
	        http.httpBasic();
	    } 
	}
	
	@Configuration
	@Order(2)
	@ComponentScan("com.example.demo")
	public static class AccountSecurityJavaConfig extends WebSecurityConfigurerAdapter {
		 
	    @Autowired
	    private DataSource data;
	
	    @Autowired
	    private AuthenticationProvider customAuthenticationProvider;
	 
	    @Override
	    protected void configure(AuthenticationManagerBuilder auth)
	      throws Exception {
	    	auth.authenticationProvider(customAuthenticationProvider);    		
	    }
	     
	    @Override
	    protected void configure(HttpSecurity http) throws Exception { 
	        http
	        .csrf().disable()
	        .authorizeRequests()
	        .antMatchers("/api/comment/**" ).authenticated()
	        .antMatchers("/api/account/**").permitAll()
	        .anyRequest().authenticated()
	        .and()
	        .httpBasic();
	    }	    
	}

}