package energy.adesso.adessoandroidapp.logic.model.identifiable;


import androidx.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.Serializable;

import energy.adesso.adessoandroidapp.logic.model.SerializableObject;

public abstract class IdentifiableObject extends SerializableObject implements Serializable {
  private String id;
  @Nullable
  protected String updatedAt;
  @Nullable
  protected String createdAt;
  @Nullable
  protected String deletedAt;

  protected final static transient DateTimeFormatter dateTimeStrategy = ISODateTimeFormat.dateTime();

  public IdentifiableObject(String id){
    this.id = id;
  }

  public IdentifiableObject(String id, DateTime createdAt, DateTime updatedAt, DateTime deletedAt){
    this.id = id;
    this.createdAt = createdAt.toString(dateTimeStrategy);
    this.updatedAt = updatedAt.toString(dateTimeStrategy);
    this.deletedAt = deletedAt.toString(dateTimeStrategy);
  }

  public DateTime getCreatedAt(){
    return DateTime.parse(createdAt,dateTimeStrategy);
  }

  public DateTime getUpdatedAt(){
    return DateTime.parse(updatedAt,dateTimeStrategy);
  }

  public DateTime getDeletedAt(){
    return DateTime.parse(deletedAt,dateTimeStrategy);
  }

  public String getId(){
    return id;
  }

  public static IdentifiableObject deserialize(String source){
    return gson.fromJson(source, IdentifiableObject.class);
  }
}
