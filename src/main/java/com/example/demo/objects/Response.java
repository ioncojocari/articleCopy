

package com.example.demo.objects;


import java.util.List;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"successful",
"message",
"limit",
"articles",
"id"
})
@JacksonXmlRootElement(localName="Response")
public class Response 
{
public static final String SUCCESSFUL_FIELD="successful";
public static final String MESSAGE_FIELD="message";
public static final String LIMIT_FIELD="limit";
public static final String RESULT_FIELD="result";
@JsonProperty("successful")
@JacksonXmlProperty(localName = "successful")
private Boolean successful;
@JsonProperty("message")
@JacksonXmlProperty(localName = "message")
private String message;
@JsonProperty("limit")
@JacksonXmlProperty(localName = "limit")
private Integer limit;

@JsonProperty("result")
@JacksonXmlProperty(localName="Article")
@JacksonXmlElementWrapper(useWrapping=true,localName="Articles")
private List<Article> result;
@JsonProperty("needToDelete")
@JacksonXmlProperty(localName="id")
@JacksonXmlElementWrapper(useWrapping=true,localName="needToDelete")
private List<Long> needToDeleteIds;
@JsonProperty("additionalProperties")
private Map<String, Object> additionalProperties = new HashMap<String, Object>();


/**
* No args constructor for use in serialization
* 
*/
public Response() {
}

/**
* 
* @param id
* @param limit
* @param message
* @param result
* @param successful
*/
public Response(Boolean successful, String message, Integer limit, List<Article> result) {
super();
this.successful = successful;
this.message = message;
this.limit = limit;
this.result = result;

}

@JsonGetter("successful")
public Boolean getSuccessful() {
return successful;
}

@JsonSetter("successful")
public void setSuccessful(Boolean successful) {
this.successful = successful;
}

@JsonGetter("message")
public String getMessage() {
return message;
}

@JsonSetter("message")
public void setMessage(String message) {
this.message = message;
}

@JsonGetter("limit")
public Integer getLimit() {
return limit;
}

@JsonSetter("limit")
public void setLimit(Integer limit) {
this.limit = limit;
}

public List<Article> getResult() {
return result;
}


public void setResult(List<Article> result) {
this.result = result;
}


public List<Long> getNeedToDeleteIds() {
return needToDeleteIds;
}


public void setNeedToDeleteIds(List<Long> needToDeleteIds) {
this.needToDeleteIds = needToDeleteIds;
}


@Override
public String toString() {
	return "Response [successful=" + successful + ", message=" + message + ", limit=" + limit + ", result=" + result
			+  ", additionalProperties=" + additionalProperties + "]";
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}


}