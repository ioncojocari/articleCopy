package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.models.ArticleManager;
import com.example.demo.objects.Article;
import com.example.demo.objects.Response;

@Controller
public class PublicArticleController {
	private static final String PREFIX="/api";
	@Autowired
	ArticleManager manager;
	/**
	 * 
	 * @param ids is array with 2 numbers min and max id 
	 * @return
	 */
	@GetMapping(PREFIX+"/"+Article.TABLE_NAME)
	@ResponseBody
	public Response getArticlesWithParams(
			@RequestParam(value="limit",required=false) Integer limit,
			@RequestParam(value="beforeId",required=false) Long before,
			@RequestParam(value="afterId",required=false) Long after,
			@RequestParam(value="lastId",required=false) Long lastId,
			@RequestParam(value="lastNr",required=false)Integer lastNr,
			@RequestParam(value="min",required=false)Long minId,
			@RequestParam(value="max",required=false) Long maxId,
			@RequestParam(value="lastChange",required=false)Long lastChange){
		System.out.println("lastChange "+lastChange);
		System.out.println("min "+minId);
		System.out.println("max "+maxId);
		Response response=new Response();
		List<Article> result;
		response.setSuccessful(true);
		try {
			if(before!=null){
				System.out.println("before");
				result=getBeforeId(before,limit);
			}else if(lastId!=null) {
				System.out.println("lastId");
				result=getLast(response, lastId, limit);
				
			}else if(lastChange!=null&& minId!=null&&maxId!=null) {
				System.out.println("lastChange");
				result=getLastChange(response,lastChange,minId,maxId);
			} else if(after!=null){
				System.out.println("afterId");
				result=getAfterId(response, after, limit);
			}else if(lastNr!=null) {
				System.out.println("lastNr");
				result=manager.getAfterNr(lastNr,limit);
			
			}else {
				System.out.println("get default");
				result= manager.get(limit);
			}
			response.setResult(result);
			
		}catch(Exception e) {
			System.out.println("error "+e.getLocalizedMessage());
			System.out.println("message "+e.getMessage());
			System.out.println("class "+e.getClass());
			response.setMessage(e.getMessage());
			response.setSuccessful(false);
		}
		return response;
	}
	
	private List<Article> getBeforeId(Long before,Integer limit){
		return manager.getBeforeId(before,limit);
	}
	
	private List<Article> getLast(Response response,Long lastId,Integer limit){
		List<Article> result;
		int afterId=manager.afterId(lastId);
		response.setLimit(afterId);
		if(limit==null) {
			result=manager.getLastWithLimit(lastId);
		}else {
			result=manager.getLastWithLimit(lastId,limit);
		}
		return result;
	}
	
	private List<Article> getAfterId(Response response,Long after,Integer limit){
		List<Article>result= manager.getAfterId(after,limit);
		response.setLimit(manager.afterId(after));
		return result;
	}
	
	private List<Article> getLastChange(Response response,Long lastChange,Long minId,Long maxId){
		int afterId=manager.afterId(maxId);
		if(afterId!=0&&afterId>ArticleManager.LAST_LIMIT){
			return getLast(response,maxId,null);
		}else {

			List<Article> result=manager.getLastChange(minId,maxId,lastChange);
			List<Long> needToBeDeleted=manager.needToBeDeleted(minId,maxId);
			System.out.println("needToBeDelted length:"+needToBeDeleted.size());
			response.setNeedToDeleteIds(needToBeDeleted);
			
			if(afterId>0) {
				List<Article> newArticles=getLast(response,maxId,null);
				result.addAll(newArticles);
			}
			return result;
		}
	}
		
}
