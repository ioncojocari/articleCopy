var HtmlManager={

	writeToElement:function(element,value){
		element.innerHTML=value;
	},
	
	deleteElement:function(id){
		var el=document.getElementById(id);
		if(el!=null){
			el.remove();
		}
	},

	addToEnd:function(element,data){
		if(element!=null){
			var value=element.innerHTML;
			var final;
			if(value!=null){
				final=value+data;
			}else{
				final=data;
			}
			this.writeToElement(element,final);
		}else{
			alert("elementNull");
		}
	},

	addToTop:function(element,data){
		if(element!=null){
			var value=element.innerHTML;
			var final;
			if(value!=null){
				final=data+value;
			}else{
				final=data;
			}
			this.writeToElement(element,final);
		}else{
			alert("elementNull");
		}
	}
	
}

var ArticleManager={
	
	getArticles:function(needToInsertIn,editFunction){
		var request =new XMLHttpRequest();
		console.log('starting to get articles');
		request.open("GET","/api/articles",true);
		//request.setRequestHeader('Accept', 'application/json');
		request.onload=function(){
				var resp=request.responseText;
				var result = JSON.parse( resp );
				var rs=result["result"];
				var str="";
				var ids=new Array();
				for( nr in rs){
					 str+=this.articleTemplate(rs[nr]["id"],rs[nr]["title"],rs[nr]["content"],rs[nr]["url"]);
//					"<tr >" +
//							"<th scope='row'>"+rs[nr]["id"]+"</th >"+
//							"<td id='title" +rs[nr]["id"]+"'>"+rs[nr]["title"]+"</td >"+
//							"<td id='content" +rs[nr]["id"]+"'>"+rs[nr]["content"]+"</td >"+
//							"<td><img id='image"+rs[nr]["id"] +"' src='/files/"+rs[nr]["url"]+"' /></td >"+
//							"<td><button value='"+rs[nr]["id"]+"' id='edit"+rs[nr]["id"]+"'>"+"edit"+"</button></td >"+
//							"<td><button  value='"+rs[nr]["id"]+"' id='delete"+rs[nr]["id"]+"'>"+"delete"+"</button></td >"+
//					"</tr>";
					ids.push(rs[nr]["id"]);
					
					
				}
				console.log(str);
				needToInsertIn.append(str);
				//HtmlManager.addToEnd(needToInsertIn,str);	
				
				DataManager.addFunctionsToElements(ids,editFunction);
				
				
		}
		
		request.send();
	},
    articleTemplate:function(id,title,content,url){
    	var result= `<div class="col-lg-4 col-md-6 mb-4">
        <div class="card h-100">
          
          <div  id="article`+id+`"  class="card-body">
          	<img class="card-img-top" id="image`+id+`" src="/api/files/`+url+`" alt=""/>
            <h4 id="title`+id+`" class="card-title">`+title+`</h4>
            <p id="content`+id+`" style=" 
    white-space: nowrap;

    overflow:hidden !important;
    text-overflow: ellipsis;"
     class="card-text">`+content+`</p>
          </div>
          <div class="card-footer">
          	<div class="row">
              	<div class="col-lg-8 col-md-8  col-sm-8  col-12 mx-auto">
	                  <button value="`+id+`"  id="edit`+id+`"class="btn btn-outline-primary btn-sm">edit</button>
	                  <button value="`+id+`" id="delete`+id+`" style="float:right"  class="btn btn-outline-primary btn-sm">delete</button>
              	</div>
          	</div>
          	
          </div>
        </div>
      </div>`;
      return result;
    },
	displayArticles:function(articles,editFunction,page){
		var ids=new Array();
		var rs=articles["result"];
		var str="";
		if(rs!=null && rs.length !=0){
			for( nr in rs){
				 str+=this.articleTemplate(rs[nr]["id"],rs[nr]["title"],rs[nr]["content"],rs[nr]["url"]);
//				"<tr >" +
//						"<th scope='row'>"+rs[nr]["id"]+"</th >"+
//						"<td id='title" +rs[nr]["id"]+"'>"+rs[nr]["title"]+"</td >"+
//						"<td id='content" +rs[nr]["id"]+"'>"+rs[nr]["content"]+"</td >"+
//						"<td><img id='image"+rs[nr]["id"] +"' src='/files/"+rs[nr]["url"]+"' /></td >"+
//						"<td><button value='"+rs[nr]["id"]+"' id='edit"+rs[nr]["id"]+"'>"+"edit"+"</button></td >"+
//						"<td><button  value='"+rs[nr]["id"]+"' id='delete"+rs[nr]["id"]+"'>"+"delete"+"</button></td >"+
//				"</tr>";
				ids.push(rs[nr]["id"]);
				
				
			}
	
			$("#tbody").html(str);
			DataManager.addFunctionsToElements(ids,editFunction);
			$("#page").text(page);
		}
	},
	
	displayArticleContent:function(id){
		
		content=$("#content"+id).text();
		$("#contentText").text(content);
		$( "#contentDialog" ).dialog( "open" );	
	},
	
	getAfter:function(id){
		
	},
	
	getBefore:function(id){
		
	},
	
	insertArticle:function(article){
		
	},
	
	updateArticle:function(article){
		
	},
	
	deleteArticle:function(id){
		
	}
}



var DataManager={
		

		addFunctionsToElements:function(ids,editFunction){
			
			for(key in ids){
				console.log("id"+ids[key]);
				
				var edit=document.getElementById("edit"+ids[key]);
				edit.onclick=editFunction;
				//add function to delete buttons;
				var deletes=document.getElementById("delete"+ids[key]);
				deletes.onclick=function(){
					if (confirm('Do you want to delete article with id : '+this.value+'?')){
						DataManager.deleteFromDb(this.value);
					}
				}
				var article=$("#article"+ids[key]).on("click",showArticle)
				var image=$("#image"+ids[key]).on("click",showArticle)
				
				function showArticle(){
					ArticleManager.displayArticleContent(this.id.match(/\d+/))
				}
				
			}
		},
		
		deleteFromDb:function(id){
			var xhr = new XMLHttpRequest();
			
			xhr.open('DELETE', '/api/admin/articles/'+id, true);
			xhr.onload=function(){
				if (xhr.status != 200) {
					  // обработать ошибку
					    alert( xhr.status + ': ' + xhr.statusText ); // пример вывода: 404: Not Found
					} else {
					  // вывести результат
						DataManager.deleteUi(id);
					}
			}
			xhr.send();
		},
		
		deleteUi:function(id){
			//this function removes whole row 
			var el=document.getElementById("delete"+id);
			var td=el.parentElement.parentElement.parentElement.parentElement;
			var tr=td.parentElement;
			tr.remove(td);
		},
		
		editUi:function(id){
			
		}
}



$( function() {
    var dialog, form,
 
      // From http://www.whatwg.org/specs/web-apps/current-work/multipage/states-of-the-type-attribute.html#e-mail-state-%28type=email%29
    	
      title = $( "#title" ),
      id=$("#id"),
      labelId=$("#labelId"),
      content = $( "#content" ),
      image = $( "#image" ),
      allFields = $( [] ).add( title ).add( content ).add( image ).add(id),
      tips = $( ".validateTips" );
    function setEmptyFields(){
    	content.text("");
    	labelId.val("");
    	image.val("");
    	title.val("");
    }
	var tbody=$("#tbody");

	var getArticles=$("#getArticles");
	var i=0;
	var allEditButtons=new Array();
	
	;
	var editFunction=function(){
			$("#createButton").text("Edit article");
			$("#id").show();
			$("#id").val(this.value);
			$("#labelId").show();
	    	contentVar=$("#content"+this.value);
	    	if(contentVar!=null){
	    		$("#content").text(contentVar.text());
	    	}
	    	titleVar=$("#title"+this.value);
	    	if(titleVar!=null){
	    		$("#title").val(titleVar.text());
	    	}
	    	//var edit=document.getElementById("edit"+ids[key]);
	    	dialog.dialog( "open" );
    	};
    function getArticlesForPage(page){

        var xhr = new XMLHttpRequest();

       param="?lastNr="+(page-1)*20;

        // These extra params aren't necessary but show that you can include other data.

        xhr.open("GET", '/api/articles'+param, true);

        xhr.onreadystatechange = function(){
	        if (this.readyState == XMLHttpRequest.DONE) {      	 
	        	if(xhr.status === 200){
	        		response=JSON.parse(xhr.response);
	        		ArticleManager.displayArticles(response,editFunction,page);
	        		
	        	}
	        }
        }

        xhr.send();
    };
    getArticlesForPage(1);
    $("#prev").button().on("click",function(){
    	
    	page=$("#page").text();
    	if(page>1){
	    	page--;
	    	getArticlesForPage(page);

	    }    	
    });
	$("#next").button().on("click",function(){
    	page=$("#page").text();
    	if(page>=1){
	    	page++;
	    	getArticlesForPage(page);

	    }  
	});

	
    function updateTips( t ) {
      tips
        .text( t )
        .addClass( "ui-state-highlight" );
      setTimeout(function() {
        tips.removeClass( "ui-state-highlight", 1500 );
      }, 500 );
    }
 
    function checkLength( o, n, min, max ) {
      if ( o.val().length > max || o.val().length < min ) {
        o.addClass( "ui-state-error" );
        updateTips( "Length of " + n + " must be between " +
          min + " and " + max + "." );
        return false;
      } else {
        return true;
      }
    }
 
    function checkRegexp( o, regexp, n ) {
      if ( !( regexp.test( o.val() ) ) ) {
        o.addClass( "ui-state-error" );
        updateTips( n );
        return false;
      } else {
        return true;
      }
    }
    
    function chooseAction(){
    	
    	if(!$("#id").is(":visible")){
    		addArticle();
    	}else{
    		editArticle();
    	}
    }
    
    function saveArticle(succesfulFunc,failureFunc){

        var file = $('#image')[0].files[0];
        if(file==null){
        	alert("file is null");
        }
        var xhr = new XMLHttpRequest();
       // var data={article:'{ "title":"'+title.val()+'","content":"'+content.val()+'"}'};
        var fd = new FormData();
        fd.append("file", file);
        // These extra params aren't necessary but show that you can include other data.
        var articleResp='{ "title":'+escape(title.val())+
		',"content":'+escape(content.val())+'}';
        fd.append("article", articleResp);
        console.log(articleResp);
        xhr.open("POST", '/api/admin/articles', true);

        xhr.onreadystatechange = function(){
	        if (this.readyState == XMLHttpRequest.DONE) {      	 
	        	if(xhr.status === 200){
	        		response=JSON.parse(xhr.response);
	        		console.log("response"+response);
	        		successful=response["successful"];
	        		if(successful){
		        		newUrl=response["url"];
		        		id=response["id"];
		        		console.log("response id:"+id);
		        	 	succesfulFunc(id,newUrl);
	        		}else{
	        			failureFunc();
	        		}
	        	}else{
	        	  	failureFunc();
	        	}
	        }
        }

        xhr.send(fd);
    }
    
    function escape(str) {
    	  //return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&").;
    	return JSON.stringify(str);
    	}
 
    function addArticle() {
      var valid=validate();
      if ( valid ) {
	      saveArticle(saveArticleUiSuccess,saveArticleUiFailure);        
      }
      return valid;
    }
    
    function saveArticleUiSuccess(id,newUrl){
//		$( "#tbody" ).prepend("<tr >" +
//				"<th scope='row'>"+id+"</th >"+
//				"<td id='title" +id+"'>"+title.val()+"</td >"+
//				"<td id='content" +id+"'>"+content.val()+"</td >"+
//				"<td><img id='image"+id +"' src='/files/"+newUrl+"' /></td >"+
//				"<td><button value='"+id+"' id='edit"+id+"'>"+"edit"+"</button></td >"+
//				"<td><button  value='"+id+"' id='delete"+id+"'>"+"delete"+"</button></td >"+
//		"</tr>");
		var rs=ArticleManager.articleTemplate(id,title.val(),content.val(),newUrl);
		console.log(rs);
		$( "#tbody" ).prepend(rs);
		articleId=new Array();
		console.log("saveArticleUiSuccess id:"+id);
		articleId.push(id);
		DataManager.addFunctionsToElements(articleId,editFunction);
		setEmptyFields();
		dialog.dialog( "close" );
    }
    

    
    function saveArticleUiFailure(){alert("failed to save article");}
    
    function validate(){
        var valid = true;
        allFields.removeClass( "ui-state-error" );
   
        valid = valid && checkLength( title, "title", 3, 25 );
        valid = valid && checkLength( content, "content", 6, 1024 );
   
        valid = valid && checkRegexp( title, /^[a-z]([0-9a-z_\s])+$/i, "title may consist of a-z, 0-9, underscores, spaces and must begin with a letter." );
        return valid;
    }
    
    function editArticle(){
    	var valid=validate();
        if ( valid ) {
        	editArticleDb(editArticleUiSuccess,editArticleUiFailure);
        }
        return valid;
    }
    
    function editArticleDb(success,failure){
    	id=$("#id");
    	var xhr = new XMLHttpRequest();
        var fd = new FormData();
        var file = $('#image')[0].files[0];

        console.log("longId : "+id.val());
        if(file!=null){
        	fd.append("file", file);
        }
        // These extra params aren't necessary but show that you can include other data.
        var articleResp='{ "title":'+escape(title.val())+
		',"content":'+escape(content.val())+'}';
        fd.append("article", articleResp);
        var url='/api/admin/articles/'+Number(id.val());
        console.log("url : "+url);
        xhr.open("PUT", url, true);

        xhr.onreadystatechange = function(){
	        if (this.readyState == XMLHttpRequest.DONE) {      	 
	        	if(xhr.status === 200){
	        		response=JSON.parse(xhr.response);
	        		success(response);
	        	}else{
	        		failure();
	        	}
	        }
        }

        xhr.send(fd);
    }
    
    function editArticleUiSuccess(response){
    	id=$("#id").val();
    	$("#title"+id).text(title.val());
    	$("#content"+id).text(content.val());
    	if(response["url"]!=null){
    		$("#image"+id).attr("src","/api/files/"+response["url"]);
    	}
		setEmptyFields();
		dialog.dialog( "close" );
    }
    
    function editArticleUiFailure(){alert("failed to edit article");}
    
    $("#contentDialog" ).dialog({
        autoOpen: false,
        width:$(document).width()*0.66,
        height:$(document).height()*0.5
    });
    
    dialog = $( "#dialog-form" ).dialog({
      autoOpen: false,
      width:$(document).width()*0.66,
      modal: true,
      buttons:[{
    	        text:"Add article",
    	        id:"createButton",
    	        click:chooseAction,
    	    	  },{
    	    	text:"Cancel",
    	    	  click: function() {
    	              dialog.dialog( "close" );
    	            }
    	    	  }
    	      ],
      close: function() {
        form[ 0 ].reset();
        allFields.removeClass( "ui-state-error" );
      }
    });
   
 
    form = dialog.find( "form" ).on( "submit", function( event ) {
      event.preventDefault();
      chooseAction();
    });
 
    $( "#create-user" ).button().on( "click", function() {
    	setEmptyFields();
    	$("#createButton").text("Add article");
    	$("#id").hide();
    	$("#labelId").hide();
    	dialog.dialog( "open" );
    });
    
    
   
//      $( "#edit-user" ).button().on( "click", function() {
//    	id.show();
//      	labelId.show();
//    	dialog.dialog( "open" );
//    	 
//      });
  } );