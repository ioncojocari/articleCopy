package com.example.demo.objects;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Checks if account with that name was created and if 
 * length of username ,password is decent
 * @author ionco
 *
 */
public class AccountValidator {
	@PersistenceContext
	private EntityManager manager;
	public Map<String,String> valid(Account account) {
		Map<String,String> result=new HashMap<String,String>();
		validateUsername(result,account.getUsername());
		validatePassword(result,account.getPassword());
		return result;
	}
	
	private void addError(Map<String,String> map,Map.Entry<String,String> entry) {
		if(entry!=null) {
			map.put(entry.getKey(),entry.getValue());
		}
	}
	
	private Map.Entry<String, String> newEntry(String key,String value) {
		return new AbstractMap.SimpleEntry<String,String>(key,value);
	}
	
	private void validateUsername(Map<String,String> results,String username) {
		Map.Entry<String,String> error=usernameRightLength(username);
		
		addError(results,error);
		if(error!=null) {
			return ;
		}
		error=usernameRegisteredError(username);
		addError(results,error);
	
	}
		
	private Map.Entry<String, String> usernameRegisteredError(String username){
		long nrs=(long)manager.createQuery(COUNT_USERS_WITH_USERNAME)
		.setParameter(Account.USERNAME_FIELD, username).getSingleResult();
		if(nrs>0) {
			return newEntry(Account.USERNAME_FIELD, USERNAME_REGISTERED_ERROR);
		}else {
			return null;
		}
	}
	
	private Map.Entry<String, String> usernameRightLength(String username){
		if(username!=null &&
				username.length()>=min_username_length&&
				username.length()<=max_username_length) {
			return null;
		}
		return newEntry(Account.USERNAME_FIELD,USERNAME_LENGTH_ERROR);
	}
		
	private void validatePassword(Map<String,String> results,String password) {
		Map.Entry<String,String> error=passwordRightLength(password);
		
		addError(results,error);
	}
	
	private Map.Entry<String,String> passwordRightLength(String password){
		if(password!=null &&
				password.length()>=min_password_length&&
				password.length()<=max_password_length) {
			return null;
		}
		return newEntry(Account.USERNAME_FIELD,PASSWORD_LENGTH_ERROR);
	}
		
	private static String USERNAME_REGISTERED_ERROR="username is already registered.";
	private static int min_username_length=5;
	private static int max_username_length=60;
	private static int min_password_length=9;
	private static int max_password_length=255;
	private static String USERNAME_LENGTH_ERROR="username empty or size is wrong,min length="+
	min_username_length+",max length="+max_username_length;
	private static String PASSWORD_LENGTH_ERROR="password min size:  "+min_password_length+
			",max size: "+max_password_length;

	
	private static final String COUNT_USERS_WITH_USERNAME="select count(*) from "+
			Account.TABLE_NAME+" "+Account.ABREVIATION
			+" where "+Account.ABREVIATION+
			"."+Account.USERNAME_FIELD+"=:"
			+Account.USERNAME_FIELD;
}
