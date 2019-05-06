package com.corti.jsonutils;
import java.io.IOException;
import java.nio.file.attribute.FileTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Jackson returned an error when trying to deserialize a FileTime object.  I created this simple
 *   class to get around that.  You just add annotation 
 *      @JsonDeserialize(using = com.corti.jsonutils.FileTimeDeSerializer.class)
 *   right before the variable declaration to tell Jackson to use this to deserialize
 *   the variable... note the value should be a string representing milliseconds since
 *   ephoc.  If serializing in jackson use the following annotation
 *      @JsonSerialize(using = com.corti.jsonutils.FileTimeSerializer.class)
 */
public class FileTimeDeSerializer extends StdDeserializer<FileTime> {
  private static final long serialVersionUID = 1L;

  FileTimeDeSerializer() {
    this(null);
  }
  
  FileTimeDeSerializer(Class<FileTime> t) {
    super(t);
  }

  @Override
  public FileTime deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
    // The FileTime in the json object is expected to be a string representing milliseconds since epoch
    String fileTimeAsString = jsonparser.getText();
    return FileTime.fromMillis(Long.parseLong(fileTimeAsString));        
  }  
}