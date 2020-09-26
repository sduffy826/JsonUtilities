package com.corti.jsonutils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.corti.dateutils.Convert;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Common json utilities, this is a helper class.  It uses
 * jackson to convert from java objects to json strings and vica
 * versa.
 * 
 * @author sduffy
 */
public class JsonUtils {
  private ObjectMapper mapper;
  private static final boolean DEBUG = true;

  public JsonUtils() {
    // Main thing our code needs is a jackson objectmapper
    mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
  }
  
  public void setFailOnUnknowProperties(boolean boolValue) {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, boolValue);
  }

  /**
   * Takes in a jsonNode and returns it as a string that has been 'prettified' :)
   * @param _jsonNode
   * @return A 'prettified' string version of the json node
   */
  public String prettifyIt(JsonNode _jsonNode) {
 
    try {
      return mapper.writerWithDefaultPrettyPrinter()
          .writeValueAsString(_jsonNode);
    } catch (JsonGenerationException e1) {
      e1.printStackTrace();
    } catch (IOException e2) {
      e2.printStackTrace();
    }
    return "Error with JsonNode:\n" + _jsonNode.toString(); // Should never get here :(
  }
 
  
  /**
   * Takes in a pojo and returns it as a json string repsentation of it
   * @param _object
   * @return A string version of the pojo
   */
  public String getJsonStringFromPojo(Object _pojo) {
 
    try {
      return mapper.writeValueAsString(_pojo);
      //return mapper.writerWithDefaultPrettyPrinter()
       //   .writeValueAsString(_pojo);
    } catch (JsonGenerationException e1) {
      e1.printStackTrace();
    } catch (IOException e2) {
      e2.printStackTrace();
    }
    return "Error with getJsonStringFromPojo\n" + _pojo.toString(); // Should never get here :(
  }
  
  /**
   * Return a java pojo from a json string
   * 
   * @param <T>
   * @param _jsonString  a json string representing the pojo
   * @param genericClass  the class of the object returned 
   * @return  the pojo
   */
  public <T> T getPojoFromJsonString(String _jsonString, Class<T> genericClass) {
    try {
      return (T)mapper.readValue(_jsonString,genericClass);
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return null;
  }
 
  /**
   * Little helper when want to dump out a JsonNode and don't need a prefix, it
   * just calls the other method and passes in a prefix of ""
   * @param _jsonNode  the node to inspect
   */
  public void dumpNode(JsonNode _jsonNode) {
    dumpNode(_jsonNode,"");
  }
  
  /**
   * Helper to dump out information about the JsonNode passed in
   * @param _jsonNode  the JsonNode to inspect
   * @param _prefix  a string to use a prefix; mainly for clarifying the record written out
   */
  public void dumpNode(JsonNode _jsonNode, String _prefix) {
    Iterator<String> fieldNames = _jsonNode.fieldNames();
    
    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      Object v = _jsonNode.get(fieldName);
      JsonNode theVar = (JsonNode)v;
      
      System.out.print(_prefix + "Field: " + fieldName + " " + theVar.getNodeType().toString() + " ");
      
      String outStr = "";   
      if (theVar.isArray()) { outStr += ", isArray"; }
      if (theVar.isNumber()) { outStr += "isNumber"; }
      if (theVar.isBoolean()) { outStr += "isBoolean"; }
      if (theVar.isBinary()) { outStr += "isBinary (base 64 encoded)"; }
      if (theVar.isDouble()) { outStr += "isDouble"; }
      if (theVar.isInt()) { outStr += "isInt"; }
      if (theVar.isShort()) { outStr += "isShort"; }
      if (theVar.isFloat()) { outStr += "isFloat"; }
      if (theVar.isLong()) { outStr += "isLong"; }
      if (theVar.isNull()) { outStr += "isNull"; }
      if (theVar.isObject()) { outStr += "isObject"; }
      if (theVar.isPojo()) { outStr += "isPojo"; }
      if (theVar.isTextual()) { outStr += "isTextual"; }
      outStr = (outStr.length() > 3 ? outStr.substring(2) : "");
      System.out.println(outStr);
      
      if ((v instanceof Integer) || (v instanceof Long)) {
        System.out.println("Integer/Long");
      }
      else if (v instanceof Boolean) {
        System.out.println("Boolean");
      }
      else if ((v instanceof Float) || ( v instanceof Double)) {
        System.out.println("Float/Double");
      }
      else if (v instanceof List) {
        System.out.println("List");
      }
      else if (v instanceof Map) {
        System.out.println("Map");
      }
      else if (v instanceof JsonNode) {
        dumpNode((JsonNode)v, _prefix + "  ");
      }
    }
    
    if (_jsonNode.isArray()) {
      for (JsonNode arrayElement : _jsonNode) {
        dumpNode(arrayElement, _prefix + "  ");
      }
    }    
  }  
  
  /**
   * Return the json string in a more readable format
   * @param _jsonString  The json string 
   * @return The jsonString that has been 'prettified'
   */
  public String prettifyIt(String _jsonString) {
    JsonNode jsonNode = getJsonNodeForJsonString(_jsonString);
    return prettifyIt(jsonNode);
  }

  /**
   * Return a JsonNode for a given string representing a json object, note if the
   * string is 'really' the 'value' part then call getJsonNodeForJsonStringKeyAndValue
   * instead (and give it the json key part).
   * 
   * @param _jsonString
   * @return JsonNode
   */
  public JsonNode getJsonNodeForJsonString(String _jsonString) {
    JsonNode rtnNode = null;

    try {    
      rtnNode = mapper.readValue(_jsonString, JsonNode.class);
    }
    /*
     * String jsonOutString = objectMapper.writeValueAsString(jsonNode);
     * StringEntity entity= new StringEntity(jsonOutString);
     * System.out.println("jsonOutString:"+jsonOutString);
     * httpPost.setEntity(entity); response= client.execute(httpPost); }
     */
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return rtnNode;
  }
  
  /** 
   * Return a JsonNode for a key/value pair; basically takes a key value and
   * returns a JsonNode of it i.e. JsonNode({ "key" : "value" })
   * @param _key
   * @param _value
   * @return JsonNode version of key/value pair
   */
  public JsonNode getJsonNodeForKeyAndValue(String _key, String _value) {    
    return getJsonNodeForJsonString(getJsonStringForKeyAndValue(_key, _value));
  }  
  
  /**
   *  Return a String representing a key/value pair (i.e. { "key" : "value" }), the main thing
   *  this does is replace '\"' with '\\\\\"', the proper escape sequence (due to way it's interpreted
   *  you have a lot of \ :))
   *  @param _key The 'key' part of key/value pair
   *  @param _value The 'value' part of key/value pair
   *  @return A string thats properly formatted for json '{"key":"value"}'  
   */
  public String getJsonStringForKeyAndValue(String _key, String _value) {
    String value = _value.trim().replaceAll("\"", "\\\\\"");
    return "{ \"" + _key.trim() + "\" : \"" + value + "\"}";
  }
  
  /**
   * Return a java pojo from a JsonNode; you also pass in the class of the pojo
   * created.  Look at TestJsonDeserialization for example of usage :)
   * @param <T> 
   * @param jsonNode  A JsonNode (json version of the pojo)
   * @param genericClass The class of the object returned
   * @return A pojo of that's of type 'genericClass' that you also pass in 
   */
  public <T> T getPojoFromJsonNode(JsonNode jsonNode, Class<T> genericClass) {
    try {
      return (T)mapper.treeToValue(jsonNode,genericClass);
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * Little helper to dump out all the properties of the class passed in
   * 
   * @param genericClass
   */
  public void dumpInfo(Class<?> genericClass) {
    BeanInfo beanInfo;
    try {
      beanInfo = Introspector.getBeanInfo(genericClass);
      for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
        System.out.println(propertyDescriptor.getName());
      }
    } catch (IntrospectionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  
  /**
   * Returns the field (theField) in the JsonNode as a string
   * @param jNode  the JsonNode
   * @param theField  the field you want extracted
   * @return  a String representation of the field
   */
  public String getText(JsonNode jNode, String theField) {
    String rtnString = null;
    
    if (jNode != null) {
      JsonNode aNode = jNode.get(theField);
      if (aNode != null) {
        rtnString = aNode.asText();
        if (rtnString != null && rtnString.equalsIgnoreCase("null")) {
          rtnString = null;
          if (DEBUG) System.out.println("getText, text for: " + theField +
                                        "is literal 'null' so changed to null object");
        }       
      }
    }  
    else {
      if (DEBUG) System.out.println("getText, argument jNode is null");
    }
    return rtnString;
  }

  /**
   * Returns a boolean representation of the field (theField) in the JsonNode passed in
   * @param jNode  the JsonNode
   * @param theField  the field we want returned (as a boolean)
   * @return Boolean representation of the field
   */
  public Boolean getBoolean(JsonNode jNode, String theField) {
    Boolean rtnValue = false;  // Default it to false (has to be something :))
    
    if (jNode != null) {
      JsonNode aNode = jNode.get(theField);
      if (aNode != null) {
        rtnValue = aNode.asBoolean();
      }
    }    
    else {
      if (DEBUG) System.out.println("getBoolean for field: " + theField + "jNode is null");
    }
    return rtnValue;
  }

  /**
   * Return 'int' value of 'theField' in JsonNode   * 
   * @param jNode  the JsonNode to extract the value from
   * @param theField  the field we want extracted
   * @return  an 'int' version of 'theField'
   */
  public int getInt(JsonNode jNode, String theField) {
    int rtnValue = 0;  // Default to 0 (has to be something :))
   
    if (jNode != null) {
      JsonNode aNode = jNode.get(theField);
      if (aNode != null) {
        rtnValue = aNode.asInt();
      }
    }    
    else {
      if (DEBUG) System.out.println("getInt for field: " + theField + "jNode is null");
    }
    return rtnValue;
  }
  
  
  /**
   * Return the JsonNode for field (theField) within the JsonNode passed in
   * @param jNode  the JsonNode
   * @param theField  the field desired
   * @return  A JsonNode representation of the field (theField)
   */
  public JsonNode getJsonNode(JsonNode jNode, String theField) {
    JsonNode rtnNode = null;
    
    if (jNode != null) {
      rtnNode = jNode.get(theField);
    }    
    else {
      if (DEBUG) System.out.println("getJsonNode for field: " + theField + "jNode is null");
    }
    return rtnNode;
  }  
 
  
  /**
   * Return a java.sql.Timestamp for the field (theField) within the JsonNode 
   * @param jNode  the JsonNode
   * @param theField  the field we want
   * @return  a Timestamp representation of the field
   */
  public java.sql.Timestamp getTimestamp(JsonNode jNode, String theField) {
    java.sql.Timestamp rtnValue = null;
       
    String iso8601String = getText(jNode,theField); // Get the string field first
    if (DEBUG) { System.out.println("getTimestamp for: " + theField + " string value: " + iso8601String); }
    if (iso8601String != null) {
      rtnValue = Convert.getTimestampFromISO8601String(iso8601String);
      System.out.println("Return value: " + rtnValue.toString());
    }    
    return rtnValue;
  }
}
