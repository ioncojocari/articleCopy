package com.example.demo.objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@Component
@Entity(name=Account.TABLE_NAME)
@DynamicInsert
@DynamicUpdate
@SelectBeforeUpdate
@Table(name=Account.TABLE_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName=Account.TABLE_NAME)
public class Account {
	public static final String TABLE_NAME="accounts";
	public static final String USERNAME_FIELD="username";
	public static final String PASSWORD_FIELD="password";
	public static final String ID_FIELD="id";
	public static final String ABREVIATION="ac";
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = ID_FIELD)
	private long id;
	@Column(name = USERNAME_FIELD)
	private String username;
	@Column(name = PASSWORD_FIELD)
	private String password;
	
	public Account(long id,String username,String password) {
		setId(id);
		setUsername(username);
		setPassword(password);
	}
	
	public Account() {
		
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
		
}
