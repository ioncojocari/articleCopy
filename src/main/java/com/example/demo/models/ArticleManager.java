package com.example.demo.models;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.objects.Article;
import com.example.demo.upload.StorageService;
@Repository
@ComponentScan
public class ArticleManager {
	
	private static final int DEFAULT_LIMIT_OF_ARTICLES;
	private static final String SELECT_NEWEST_ARTICLES;
	private static final String SELECT_ARTICLES_AFTER_WITH_LIMIT;
	private static final String SELECT_ARTICLES_BEFORE_WITH_LIMIT;
	
	private static final String COUNT_AFTER_ID;
	private static final String SELECT_NEWEST_ARTICLES_AFTER_ID;
	private static final String DELETE_ALL_FROM_ARTICLES;
	public static final int LAST_LIMIT;
	private static final String DELETE_ARTICLE;
	private static final String COUNT_ARTICLES_WTIH_CONTENT;
	private static final String SELECT_AFTER_NR_ARTICLES;
	private static final String GET_UPDATED_ARTICLES;
	private static final String GET_IDS;
	@PersistenceContext
	private EntityManager manager;
	@Autowired
	StorageService storageService;
	/**
	 * method for non production usage;pure testing
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Article> getAll(){
		List<Article> articles=(List<Article>) manager.createQuery(SELECT_NEWEST_ARTICLES).getResultList();
		return articles;
	}
	/**
	 * Return newest articles 
	 * @param limit
	 * @return
	 */
	public List<Article> get(Integer limit){
		if(limit==null || limit<=0){
			limit=DEFAULT_LIMIT_OF_ARTICLES;
		}
		@SuppressWarnings("unchecked")
		List<Article> articles=(List<Article>) manager.createQuery(SELECT_NEWEST_ARTICLES)
				.setMaxResults(limit)
				.getResultList();
		return articles;
	}
	
	@SuppressWarnings("unchecked")
	public List<Article> getLastChange(Long min,Long max,Long lastTimeChanged) {
			return manager.createQuery(GET_UPDATED_ARTICLES)
					.setParameter("min", min)
					.setParameter("max", max)
					.setParameter(Article.UPDATED_FIELD, lastTimeChanged).getResultList();
				
	}
	
	public List<Long> needToBeDeleted(Long min ,Long max){

		@SuppressWarnings("unchecked")
		List<Long> list=manager.createQuery(GET_IDS)
							   .setParameter("min", min)
							   .setParameter("max", max)
							   .getResultList();
		List<Long> result=new ArrayList<Long>();
		System.out.println("----");
		for(Long l:list) {
			System.out.println(l);
		}
		
		System.out.println("----");
		for(long i=min;i<max;i++) {
			if(!list.contains(i)) {
				result.add(i);
			}
		}
		return result;
	}
	
	public long[] needToDelete(long min ,long max) {
		List<Long>longs=needToBeDeleted(min,max);
		long[] needToDelete=new long[longs.size()];
		int i=0;
		for(Long l:longs) {
			needToDelete[i++]=l;
		}
		return needToDelete;
	}
	
	public long getMin(Long[] ids) {
		if(ids[0]>ids[1]) {
			return ids[1];
		}else {
			return ids[0];
		}
		
	}
	
	public long getMax(Long[] ids) {
		if(ids[0]<ids[1]) {
			return ids[1];
		}else {
			return ids[0];
		}
	}
	public List<Article> getAfterNr(Integer afterNr,Integer limit) {
		if(limit==null || limit<=0){
			limit=DEFAULT_LIMIT_OF_ARTICLES;
		}
		if(afterNr<0) {
			afterNr=0;
		}
		@SuppressWarnings("unchecked")
		List<Article> articles=(List<Article>) manager.createQuery(SELECT_AFTER_NR_ARTICLES)
				.setFirstResult(afterNr)
				.setMaxResults(limit)
				.getResultList();
		return articles;
	}
	
	public List<Article> getAfterId(long id,Integer limit){
		if(limit==null || limit<=0 ){
			limit=DEFAULT_LIMIT_OF_ARTICLES;
		}
		@SuppressWarnings("unchecked")
		List<Article> articles=(List<Article>) manager.createQuery(SELECT_ARTICLES_AFTER_WITH_LIMIT)
				.setParameter(Article.ID_FIELD, id)
				.setMaxResults(limit)
				.getResultList();
		return articles;
	}
	/**
	 * Returns articles that are created before {id}
	 * @param id
	 * @param limit
	 * @return
	 */
	public List<Article> getBeforeId(long id,Integer limit){
		if(limit==null || limit<=0){
			limit=DEFAULT_LIMIT_OF_ARTICLES;
		}
		@SuppressWarnings("unchecked")
		List<Article> articles=(List<Article>) manager
				.createQuery(SELECT_ARTICLES_BEFORE_WITH_LIMIT)
				.setParameter(Article.ID_FIELD, id)
				.setMaxResults(limit)
				.getResultList();
		return articles;
	}
	
	@Transactional
	/***
	 * If articles with same content exits return  -1;
	 * @param article
	 * @return
	 */
	public long add(Article article){
		long articlesWithSameContent=(long)manager.createQuery(COUNT_ARTICLES_WTIH_CONTENT)
				.setParameter(Article.CONTENT_FIELD, article.getContent()).getSingleResult();
		if(articlesWithSameContent<=0) {
			article.setCreatedDate(System.currentTimeMillis());
			article.setId(0);
			Article addedArticle=manager.merge(article);
			return addedArticle.getId();
		}else {
			return -1;
		}
	}

	
	@Transactional
	public long update(Article article){
		return update(article,article.getId());
	}
	
	@Transactional
	public long update(Article article,long id){
		Query query= manager
		.createQuery(getUpdatedArticlesHQL(article))
		.setParameter(Article.ID_FIELD, id);
		if(article.getContent()!=null) {
			query.setParameter(Article.CONTENT_FIELD, article.getContent());
		}
		if(article.getTitle()!=null) {
			query.setParameter(Article.TITLE_FIELD, article.getTitle());
		}
		if(article.getUrl()!=null) {
			query.setParameter(Article.URL_FIELD, article.getUrl());
		}
		query.setParameter(Article.UPDATED_FIELD, System.currentTimeMillis());
		
		return query.executeUpdate();
	}
	/**
	 * Dynamic hql for updating article
	 * @param article
	 * @return
	 */
	private String getUpdatedArticlesHQL(Article article) {
		StringBuilder builder =new StringBuilder()
				.append(" update ")
				.append(Article.TABLE_NAME)
				.append(" set ");
		boolean prevWas=false;
		if(article.getTitle()!=null) {
			builder.append(Article.TITLE_FIELD)
				.append("=:")
				.append(Article.TITLE_FIELD);
				prevWas=true;
		}
		if(article.getContent()!=null) {
			if(prevWas) {
				builder.append(",");
			}
		
			builder.append(Article.CONTENT_FIELD)
				.append("=:")
				.append(Article.CONTENT_FIELD);
		}
		if(article.getUrl()!=null && !article.getUrl().isEmpty()) {
			if(prevWas) {
				builder.append(",");
			}
		
			builder.append(Article.URL_FIELD)
				.append("=:")
				.append(Article.URL_FIELD);
		}
		
		
			if(prevWas) {
				builder.append(",");
			}
		
			builder.append(Article.UPDATED_FIELD)
				.append("=:")
				.append(Article.UPDATED_FIELD);
		
				builder.append(" where ")
				.append(Article.ID_FIELD)
				.append("=:")
				.append(Article.ID_FIELD);
		
		return builder.toString();
	}
	
	@Transactional
	public void generate(int nr) throws UnsupportedEncodingException{
		List<Article> prevArticle=get(1);
		long prev=0;
		if(prevArticle!=null &&!prevArticle.isEmpty()) {
			prev=prevArticle.get(0).getId();
		}

		for(long i=prev+1;i<nr+prev+1;i++) {
			Article article=new Article();
			article.setContent("content azaza "+i);
			article.setTitle("title nr "+i);
			article.setUrl("url_"+i);
			add(article);
		}
	}
	
	public int afterId(long id) {
		int result=((Long) manager.createQuery(COUNT_AFTER_ID)
				.setParameter(Article.ID_FIELD, id)
				.getSingleResult()).intValue();
		
		return result;
	}

	public List<Article> getLastWithLimit(long id){	
		return getLastWithLimit(id,LAST_LIMIT);
	}
	
	/**
	 * Returns last articles if there are just 2 new articles and limit is 50 ,
	 * This method will return just 2 new articles by knowing that articles should have id more than {id}
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Article> getLastWithLimit(long id,int limit){
		
		return manager.createQuery(SELECT_NEWEST_ARTICLES_AFTER_ID)
		.setParameter(Article.ID_FIELD,id)
		.setMaxResults(limit)
		.getResultList();

	}
	/**
	 * Delete all articles and images from db/storage , it's mainly for testing;
	 */
	@Transactional
	public void deleteAllArticles() {
		manager.createQuery(DELETE_ALL_FROM_ARTICLES).executeUpdate();
		storageService.deleteAllFiles();
	}
	
	@Transactional
	public void deleteArticle(long id){
	
		String hql="SELECT " +Article.TABLE_ABREVIATION+".url from "+Article.TABLE_NAME+" "+Article.TABLE_ABREVIATION +" "+
				
				" where "+Article.TABLE_ABREVIATION+"."+Article.ID_FIELD+" =:"+Article.ID_FIELD;

		String url=(String)manager.createQuery(hql).setParameter(Article.ID_FIELD, id).getSingleResult();

		manager.createQuery(DELETE_ARTICLE).setParameter(Article.ID_FIELD,id).executeUpdate();
		
		storageService.deleteFile(url);
		
	}
	
	static {
		LAST_LIMIT=50;
		DEFAULT_LIMIT_OF_ARTICLES=20;
		SELECT_NEWEST_ARTICLES=new StringBuilder()
				.append("select ")
				.append(Article.TABLE_ABREVIATION)
				.append(" from ")
				.append(Article.TABLE_NAME)
				.append(" ")
				.append( Article.TABLE_ABREVIATION)
				.append(" order by ")
				.append(Article.CREATED_FIELD)
				.append(" desc,")
				.append(Article.ID_FIELD)
				.append(" desc ").toString();
		SELECT_NEWEST_ARTICLES_AFTER_ID=new StringBuilder()
				.append("select ")
				.append(Article.TABLE_ABREVIATION)
				.append(" from ")
				.append(Article.TABLE_NAME)
				.append(" ")
				.append( Article.TABLE_ABREVIATION)
				.append(" where ")
				.append(Article.TABLE_ABREVIATION)
				.append(".")
				.append(Article.ID_FIELD)
				.append(">:")
				.append(Article.ID_FIELD)
				.append(" order by ")
				.append(Article.CREATED_FIELD)
				.append(" desc,")
				.append(Article.ID_FIELD)
				.append(" desc ").toString();
		SELECT_ARTICLES_AFTER_WITH_LIMIT=new StringBuilder()
				.append("select ")
				.append(Article.TABLE_ABREVIATION)
				.append(" from ")
				.append(Article.TABLE_NAME)
				.append(" ")
				.append(Article.TABLE_ABREVIATION)
				.append(" where ")
				.append(Article.TABLE_ABREVIATION)
				.append(".")
				.append(Article.ID_FIELD)
				.append(" > :")
				.append(Article.ID_FIELD) 
				.append(" order by ")
				.append(Article.ID_FIELD)
				.append(" asc").toString();
		SELECT_ARTICLES_BEFORE_WITH_LIMIT=new StringBuilder()
				.append("select ")
				.append(Article.TABLE_ABREVIATION)
				.append(" from ")
				.append(Article.TABLE_NAME)
				.append(" ")
				.append(Article.TABLE_ABREVIATION)
				.append(" where ")
				.append(Article.TABLE_ABREVIATION)
				.append(".")
				.append(Article.ID_FIELD)
				.append(" < :")
				.append(Article.ID_FIELD)
				.append(" order by ")
				.append(Article.ID_FIELD)
				.append(" desc ").toString();
		SELECT_AFTER_NR_ARTICLES=new StringBuilder()
				.append("select ")
				.append(Article.TABLE_ABREVIATION)
				.append(" from ")
				.append(Article.TABLE_NAME)
				.append(" ")
				.append(Article.TABLE_ABREVIATION)
				.append(" order by ")
				.append(Article.ID_FIELD)
				.append(" desc ").toString();
		COUNT_AFTER_ID=new StringBuilder()
				.append("select count(*) from ")
				.append(Article.TABLE_NAME)
				.append(" ")
				.append(Article.TABLE_ABREVIATION)
				.append(" where ")
				.append(Article.TABLE_ABREVIATION)
				.append(".")
				.append(Article.ID_FIELD)
				.append(">:")
				.append(Article.ID_FIELD).toString();
		DELETE_ALL_FROM_ARTICLES=new StringBuilder()
				.append("delete from ")
				.append(Article.TABLE_NAME)
				.toString();
		DELETE_ARTICLE=new StringBuilder()
				.append("delete from ")
				.append(Article.TABLE_NAME)
				.append(" where ")
				.append(Article.ID_FIELD)
				.append("=:")
				.append(Article.ID_FIELD)
				.toString();
		COUNT_ARTICLES_WTIH_CONTENT=new StringBuilder()
				.append("select count(*) from ")
				.append(Article.TABLE_NAME)
				.append(" ")
				.append(Article.TABLE_ABREVIATION)
				.append(" where ")
				.append(Article.TABLE_ABREVIATION)
				.append(".")
				.append(Article.CONTENT_FIELD)
				.append("=:")
				.append(Article.CONTENT_FIELD)
				.toString();
		GET_UPDATED_ARTICLES=new StringBuilder()
				.append(" from ")
				.append(Article.TABLE_NAME)
				.append(" ")
				.append(" where ( ")
				.append(Article.ID_FIELD)
				.append(">:")
				.append("min")
				.append(" or ")
				.append(Article.ID_FIELD)
				.append("=:")
				.append("min ) and ( ")
				.append(Article.ID_FIELD)
				.append("<:")
				.append("max ")
				.append(" or ")
				.append(Article.ID_FIELD)
				.append("=:max) and ")
				.append(Article.UPDATED_FIELD)
				.append(">:")
				.append(Article.UPDATED_FIELD)
				.toString();
		GET_IDS=new StringBuilder()
				.append("select ")
				.append(Article.ID_FIELD)
						.append(" from ")
				.append(Article.TABLE_NAME)
				.append(" ")
				.append(" where ( ")
				.append(Article.ID_FIELD)
				.append(">:")
				.append("min")
				.append(" or ")
				.append(Article.ID_FIELD)
				.append("=:")
				.append("min ) and ( ")
				.append(Article.ID_FIELD)
				.append("<:")
				.append("max ")
				.append(" or ")
				.append(Article.ID_FIELD)
				.append("=:max)")
				.toString();
				
	}
	

}
