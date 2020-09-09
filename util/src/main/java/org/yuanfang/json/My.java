package org.yuanfang.json;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = true)
public class My {

  @JsonAlias("no")
  int my;
  int hello;
  @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
  Date date;
  @JsonIgnore
  String test;


  public int getNum() {
    return 4;
  }

  public static void main(String[] args) {
    My my = new My();
    my.setDate(new Date());
    my.setHello(89);

    my.date = new Date();
    System.out.println(JsonUtils.toJSON(my));

    System.out.println(JsonUtils.toT("{\n"
        + "  \"hello\": 12,\n"
        + "  \"no\": 123\n"
        +
        "}", My.class));
  }

}
