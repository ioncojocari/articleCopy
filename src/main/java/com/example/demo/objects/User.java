package com.example.demo.objects;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Size;

@Entity
@Table(name="users")
public class User {
	private static Validator validator;
	
	public static void setUp() {
	    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	    validator = factory.getValidator();
	}
	
	private void init() {
		if(validator==null) {
			setUp();
		}
	}
	
	public User(){
		init();
	}
	
	public User(String email,String password) {
		init();
		setEmail(email);
		setPassword(password);
	}
	@Id
	public int id;
	@Size(min=6,max=255)
	public String email;
	@Size(min=8,max=255)
	public String password;
	public boolean enabled;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [email=" + email + ", password=" + password + "]";
	}
	
	public Set<ConstraintViolation<User>> errors() {
		 return validator.validate( this );
	}
	
	public boolean userIsValid() {
		if(this!=null&&validator.validate( this ).size()==0) {
			return true;
		}
		return false;
	}
	
	
}
