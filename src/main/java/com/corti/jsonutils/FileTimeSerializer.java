package com.corti.jsonutils;
import java.io.IOException;
import java.nio.file.attribute.FileTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Simple class to serialize a FileTime object; I created this cause Jackson had problem
 *   trying to deserialize a FileTime; this simple class and 'FiletimeDeSerializer' get around
 *   that.  Just add annotations 
 *     @JsonSerialize(using = com.corti.jsonutils.FileTimeSerializer.class)
 *     @JsonDeserialize(using = com.corti.jsonutils.FileTimeDeSerializer.class)
 *   to tell jackson to use these classes to serialize/deserialize.
 * The serialized value is a string representing milliseconds since epoch.  
 */
public class FileTimeSerializer extends StdSerializer<FileTime> {
  private static final long serialVersionUID = 1L;
  
  FileTimeSerializer() {
    this(null);
  }
  
  FileTimeSerializer(Class<FileTime> t) {
    super(t);
  }
  
  @Override
  public void serialize(FileTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    // Write the FileTime in milliseconds format (as a string)
    gen.writeString(Long.toString(value.toMillis()));
  }  
}  