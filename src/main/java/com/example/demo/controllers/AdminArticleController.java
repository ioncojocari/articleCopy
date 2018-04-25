package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.models.ArticleManager;
import com.example.demo.objects.Article;
import com.example.demo.objects.Response;
import com.example.demo.upload.FileNotImageExeption;
import com.example.demo.upload.UploadController;
import com.fasterxml.jackson.databind.ObjectMapper;




@Controller
@ComponentScan
public class AdminArticleController {
	private static final String API_PREFIX="/api/admin";
	private static final String ADMIN_PREFIX="/admin";
	@Autowired
	ArticleManager manager;
	@Autowired
	UploadController uploadController;
	@Autowired
	ObjectMapper mapper;
	@PostMapping(API_PREFIX+"/"+Article.TABLE_NAME)
	@ResponseBody
	public Response addArticle(
			@RequestParam("article") String articleStr,@RequestParam(name="file",required=true)MultipartFile theFile) {
		String rs=null;
		Response response=new Response();
		response.setSuccessful(false);
		
		try {
			rs = uploadController.handleFileUploadApi(theFile);
		} catch (FileNotImageExeption e1) {
			// TODO Auto-generated catch block
			response.setMessage("file isn't image");
			return response;
		}
		Article article;
		try {
			article = mapper.readValue(articleStr,Article.class);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			System.out.println("e"+e.getMessage());
			return response;
		}
		article.setUrl(rs);
		if(article!=null && article.isValid()) {
			long id=manager.add(article);
			if(id>=0) {
			response.setSuccessful(true);
			response.setAdditionalProperty("id",id);
			response.setAdditionalProperty("url",rs);
			}else {
				response.setSuccessful(false);
				response.setMessage("article already registered");
			}
		}else{
			response.setMessage("invalid article");
		}
		return response;
	}
	
	@PutMapping(API_PREFIX+"/"+Article.TABLE_NAME+"/{"+Article.ID_FIELD+"}")
	@ResponseBody
	public Response updateArticle(
			@PathVariable( Article.ID_FIELD ) Long id,@RequestParam(name="file",required=false) MultipartFile theFile,@RequestParam(value="article",required=true) String articleStr){
		String rs=null;
		System.out.println("update called");

		Response response=new Response();
		response.setSuccessful(false);
		if(theFile!=null) {
			try {
				rs = uploadController.handleFileUploadApi(theFile);
			} catch (FileNotImageExeption e1) {
				// TODO Auto-generated catch block
				response.setMessage("file isn't image");
				return response;
			}
		}
		Article article;
		try {
			article = mapper.readValue(articleStr,Article.class);
		} catch (Exception e) {
			System.out.println("exception article is not mapped");
			response.setMessage(e.getMessage());
			return response;
		}
		System.out.println("article mapped");
		if(rs!=null) {
			article.setUrl(rs);
			response.setAdditionalProperty("url", rs);
		}
		if(id!=null && article!=null){
			response.setSuccessful(true);
			
			manager.update(article,id);
		}else{
			response.setMessage("invalid article or id");
		}
		return response;
	}
	
	@PostMapping(API_PREFIX+"/"+Article.TABLE_NAME+"/generate/{nr}")
	@ResponseBody
	public Response generateAricles(@PathVariable("nr") Integer nr) {
		System.out.println("generated called");
		System.out.println("nr : "+nr);
		Response response=new Response();
		try {
			if(nr!=null && nr>0) {
				manager.generate(nr);
				response.setSuccessful(true);
				return response;
			}
		}catch(Exception e) {
			response.setMessage("generation Error");
		}
		response.setSuccessful(false);
		return response;
	}
	
	@DeleteMapping(API_PREFIX+"/"+Article.TABLE_NAME)
	@ResponseBody
	public Response deleteAllArticles() {
		Response response=new Response();
		try {
			manager.deleteAllArticles();
			response.setSuccessful(true);
		}catch(Exception e) {
			response.setMessage(e.getMessage());
			response.setSuccessful(false);
		}
		return response;
	}
	
	@DeleteMapping(API_PREFIX+"/"+Article.TABLE_NAME+"/{id}")
	@ResponseBody
	public Response deleteArticle(@PathVariable("id") int id) {
		Response response =new Response();
		try {
			manager.deleteArticle(id);
			response.setSuccessful(true);
		}catch(Exception e) {
			response.setMessage(e.getMessage());
			response.setSuccessful(false);
		}
		return response;
	}
	
	@GetMapping(ADMIN_PREFIX)
	public String admin() {
		return "index";
	}
	
	@GetMapping(ADMIN_PREFIX+"/index")
	public String index() {
		return "index";
	}
		
}
