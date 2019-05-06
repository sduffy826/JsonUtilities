package com.corti.jsonutils.demo;

import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

//@JsonIgnoreProperties({ "ignoreMe1", "ignoreMe2" })
public class SampleClass {
  protected String className;
  private int whereInitialized;
  private String aString;
  private Calendar aCalendar;
  
  @JsonIgnore
  private String ignoreMe1;
  private String ignoreMe2;
  
  @JsonSerialize(using = com.corti.jsonutils.FileTimeSerializer.class)
  @JsonDeserialize(using = com.corti.jsonutils.FileTimeDeSerializer.class)
  private FileTime fileTime;
  
  SampleClass() {
    whereInitialized = 101;
    this.className = this.getClass().getName();
    ignoreMe1 = "ignoreMe1 values in default";
    ignoreMe2 = "ignoreMe2's value in default";
  }
  
  SampleClass(int a, String b, Calendar c, FileTime f) {
    this.className = this.getClass().getName();
    whereInitialized = 1;
    aString = b;
    aCalendar = c;
    fileTime = f;
    ignoreMe1 = "ignoreMe1 values";
    ignoreMe2 = "ignoreMe2's value";
  }

  public int getWhereInitialized() {
    return whereInitialized;
  }

  public String getaString() {
    return aString;
  }

  public Calendar getaCalendar() {
    return aCalendar;
  }
  
  public FileTime getFileTime() {
    return fileTime;
  }

  public void setWhereInitialized(int whereInitialized) {
    this.whereInitialized = whereInitialized;
  }

  public void setaString(String aString) {
    this.aString = aString;
  }

  public void setaCalendar(Calendar aCalendar) {
    this.aCalendar = aCalendar;
  }
  
  public void setFileTime(FileTime fileTime) {
    this.fileTime = fileTime;
  }

  @Override
  public String toString() {
    return "SampleClass [whereInitialized=" + whereInitialized + ", aString="
        + aString + ", aCalendar=" + calToString(aCalendar)
        + ", fileTime=" + fileTime.toString() 
        + ", ignoreMe1=" + ignoreMe1 + ", ignoreMe2=" + ignoreMe2 + "]";
  } 
  
  public String calToString(Calendar theCal) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    return dateFormat.format(theCal.getTime());
  }
  
}
