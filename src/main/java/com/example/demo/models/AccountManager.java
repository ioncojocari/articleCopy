package com.example.demo.models;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.objects.Account;
import com.example.demo.objects.AccountValidator;
import com.example.demo.upload.StorageService;

/**
 * this should change password create new accounts;
 *
 */
@Repository
@ComponentScan
public class AccountManager {
	@PersistenceContext
	private EntityManager manager;
	@Autowired
	StorageService storageService;
	private static final String CHANGE_PASSWORD="update "+
			Account.TABLE_NAME+" SET "+
			Account.PASSWORD_FIELD+"=:"+Account.PASSWORD_FIELD+
			" where "+Account.USERNAME_FIELD+"=:"+Account.USERNAME_FIELD;
	private static final String SELECT_USERNAME_WITH_PASSWORD="from "+Account.TABLE_NAME+" WHERE "+
					Account.USERNAME_FIELD+ "=:" +Account.USERNAME_FIELD;
	private static final String SAVE_ACCOUNT=
			"INSERT INTO accounts (username,password) VALUES (:username:password)";
	@Autowired
	AccountValidator validator;
	@Autowired
	PasswordEncoder passwordEncoder;
	/**
	 * if is correct returns null else returns errors
	 * @param account
	 * @param password
	 * @return
	 */
	@Transactional
	public Map<String,String> changePassword(Account account,String password) {
		account.setPassword(password);
		Map<String,String> errors=validator.valid(account);
		if(errors.size()>0) {
			return errors;
		}else {
			try {
				String hashedPassword=passwordEncoder.encode(password);
				manager.createQuery(CHANGE_PASSWORD)
				.setParameter(Account.USERNAME_FIELD, account.getUsername())
				.setParameter(Account.PASSWORD_FIELD, hashedPassword)
				.executeUpdate();
				
			}catch(Exception e) {
				errors.put("Server error", e.getMessage());
				return errors ;
			}
			return null;
		}
		
	}
	@Transactional
	public Map<String,String> createAccount(Account account) {
		Map<String,String> errors=validator.valid(account);
		System.out.println("password length:"+account.getPassword().length());
		if(errors.size()>0) {
			return errors;
		}else {
			try {
				String hashedPassword=passwordEncoder.encode(account.getPassword());
				account.setPassword(hashedPassword);
				System.out.println("password length:"+hashedPassword.length());
				manager.merge(account);
				
			}catch(Exception e) {
				errors.put("Server error", e.getMessage());
				return errors ;
			}
			return null;
		}
	}
	
	public boolean checkLogin(String username,String password) {
		boolean result=false;
		if(passwordEncoder!=null) {
			System.out.println("password encoder not null");
		}else {
			System.out.println("password encoder null");
		}
	
		try {
			Account account=(Account)manager.createQuery(SELECT_USERNAME_WITH_PASSWORD)
			.setParameter(Account.USERNAME_FIELD, username)
			.getSingleResult();
			if(account!=null&&passwordEncoder.matches(password, account.getPassword())) {
				result=true;
			}
		}catch(Exception e) {
			System.out.println("e"+e.getMessage());
			result=false;
		}
		
		return result;
	}
			
}
