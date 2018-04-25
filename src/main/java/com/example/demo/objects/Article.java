package com.example.demo.objects;

import java.util.Objects;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
@Component
@Entity(name=Article.TABLE_NAME)
@DynamicInsert
@DynamicUpdate
@SelectBeforeUpdate
@Table(name=Article.TABLE_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName="Article")
public class Article {
	public static final String TABLE_NAME="articles";
	public static final String TABLE_ABREVIATION="a";
	public static final String ID_FIELD="id";
	public static final String TITLE_FIELD="title";
	public static final String CONTENT_FIELD="content";
	public static final String URL_FIELD="image_url";
	public static final String CREATED_FIELD="date";
	public static final String UPDATED_FIELD="updated";
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@JacksonXmlProperty(localName = "id")
	private long id;
	@Column(name = TITLE_FIELD)
	@JacksonXmlProperty(localName = "title")
	private String title;
	@Column(name = CONTENT_FIELD)
	@JacksonXmlProperty(localName = "content")
	private String content;
	@Column(name = URL_FIELD)
	@JacksonXmlProperty(localName = "url")
	private String url;
	@Column(name = CREATED_FIELD)
	@JacksonXmlProperty(localName = "createdDate")
	private long createdDate;
	@Column(name = UPDATED_FIELD)
	@JacksonXmlProperty(localName = "updatedDate")
	private long updatedDate;
	
	public Article(){
		
	}
	
	public Article(String title,String content,String url){
		setTitle(title);
		setContent(content);
		setUrl(url);
		long now=System.currentTimeMillis();
		setCreatedDate(now);
		setUpdatedDate(now);
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title=title;
//		try {
//		this.title =  new String(title.getBytes("UTF-8"));
//		System.out.println("tiitele"+this.title);
//		}catch(Exception e) {
//			
//		}
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content)  {
		this.content=content;
//		try {
//		this.content = new String(content.getBytes("UTF-8"));
//		System.out.println("content"+this.content);
//		}catch(Exception e) {
//			
//		}

	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public long getCreatedDate(){
		return createdDate;
	}
	
	public void setCreatedDate(long time){
		this.createdDate=time;
	}
	
	public long getUpdatedDate(){
		return updatedDate;
	}
	
	public void setUpdatedDate(long time){
		this.updatedDate=time;
	}
	
	public long getId(){
		return this.id;
	}
	
	@JsonIgnore
	public boolean isValid(){
		if(this.id>-1&& 
				getTitle()!=null && !getTitle().isEmpty() &&
				getContent()!=null && !getContent().isEmpty() &&
				getUrl()!=null && !getUrl().isEmpty()){
			return true;
		}
		return false;
	}
	
	public void setId(long id ) {
		this.id=id;
	}
	@Override
	public boolean equals(Object obj) {
		if(this.hashCode()==obj.hashCode()) {
			if(obj instanceof Article) {
				Article article=(Article)obj;
				if(article.content.equals(content) ) {
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Objects.hash(this.content);
	}

	@Override
	public String toString() {
		return "Article{id=" + id + ", title=" + title + ", content=" + content +",updatedDate "+updatedDate+ ", url=" + url + ", createdDate="
				+ createdDate + "}";
	}
	
}
