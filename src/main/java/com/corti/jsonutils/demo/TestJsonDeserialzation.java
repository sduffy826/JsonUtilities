package com.corti.jsonutils.demo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import com.corti.javalogger.LoggerUtils;
import com.corti.jsonutils.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import java.nio.file.attribute.FileTime;

/**
 * This is just a little stub of code to show you how to work with the
 * JsonUtilities and how to serialize/deserialize objects.
 * 
 * @author S. Duffy
 *
 */
public class TestJsonDeserialzation {
  private static final boolean DEBUGIT = true;
  
  public static void main(String[] args) throws Exception {  
    Logger logger = (new LoggerUtils()).getLogger("loggerName", "testJsonDeserialize");
    JsonUtils jsonUtils = new JsonUtils();
  
    List<SampleClass> listObjBefore = new ArrayList<SampleClass>(2);
    List<String> jsonObjectList     = new ArrayList<String>(2);
    List<SampleClass> listObjAfter  = new ArrayList<SampleClass>(2);
    
    // Create object using default constructor
    Calendar aCal = Calendar.getInstance();
    aCal.add(Calendar.DATE, -15); 
    aCal.add(Calendar.MONTH, -11); 
    aCal.add(Calendar.YEAR, -2); 
    
    // Create FileTime object
    FileTime aFileTime = FileTime.fromMillis(Calendar.getInstance().getTimeInMillis());
    
    // Create objects and add to list; did it two diff ways... one with default constructor
    //   other with constructor passing args
    SampleClass sampleClass = new SampleClass();      
    sampleClass.setaCalendar(aCal);
    sampleClass.setaString("From default String");
    sampleClass.setFileTime(aFileTime);
    
    listObjBefore.add(sampleClass);
    listObjBefore.add(new SampleClass(4, "Silver", Calendar.getInstance(), aFileTime));
    
    if (DEBUGIT) logger.info("Size of fileList is: " + listObjBefore.size());
        
    // Serialize objects and add to jsonObjectList
    for (SampleClass theObj : listObjBefore) {
      if (DEBUGIT) logger.info("Before: " + theObj.toString());
      try {
        jsonObjectList.add(jsonUtils.getJsonStringFromPojo(theObj));
      } catch (Exception e) {
        System.out.println("Exception raised with " + theObj.toString());
        e.printStackTrace();
      }
    }
       
    //  Down here the jsonObjectList has elements to process, we'll deserialize here (other
    //    stuff is for demo)
    for (int i = 0; i < jsonObjectList.size(); i++) {
      String tempString = jsonObjectList.get(i);      
      JsonNode jsonNode = jsonUtils.getJsonNodeForJsonString(tempString);
      
      if (DEBUGIT) logger.info("non formatted: " + tempString);;
      if (DEBUGIT) logger.info("prettified: " + jsonUtils.prettifyIt(tempString));   
      
      // We have the class name of the object in json node; use that to deserialize
      String fullClassName = jsonNode.get("className").asText();
      
      Class<?> someClass = Class.forName(fullClassName);
      if (DEBUGIT) logger.info("someClass: " + someClass.getName());
      
      SampleClass aNewObj = (SampleClass) jsonUtils.getPojoFromJsonNode(jsonNode, someClass);
      listObjAfter.add(aNewObj);
      if (DEBUGIT) logger.info("After: " + aNewObj.toString());
    }
    
    // Down here list the before and after lists... they should be the same after
    //   objects have been serialized/deserialized.
    System.out.println("Objects before serialized/deserialized");
    dumpList(listObjBefore);
    System.out.println("\n\nObjects after serialized/deserialized");
    dumpList(listObjAfter);    
  }
  
  // Simple method to write contents of list
  public static void dumpList(List<SampleClass> theList) {
    for (SampleClass aObj : theList) {
      System.out.println(aObj.toString());
    }
  } 
}
