package org.yuanfang.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {


  private static final ThreadLocal<ObjectMapper> objMapperLocal = ThreadLocal
      .withInitial(() -> new ObjectMapper());

  public static String toJSON(Object value) {
    String result = null;
    try {
      result = objMapperLocal.get().writeValueAsString(value);
    } catch (Exception e) {
      log.error("toJson error:{}", value, e);
    }
    // Fix null string
    if ("null".equals(result)) {
      result = null;
    }
    return result;
  }

  public static <T> T toT(String jsonString, Class<T> clazz) {
    try {
      return objMapperLocal.get().readValue(jsonString, clazz);
    } catch (Exception e) {
      log.error("toT error: {}", jsonString, e);
    }
    return null;
  }

  public static <T> List<T> toTList(String jsonString, Class<T> clazz) {
    try {
      return objMapperLocal.get()
          .readValue(jsonString,
              TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
    } catch (Exception e) {
      log.error("toTList error: {}", jsonString, e);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public static Map<String, Object> toMap(String jsonString) {
    return toT(jsonString, Map.class);
  }


}