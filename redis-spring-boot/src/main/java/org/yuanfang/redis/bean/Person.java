package org.yuanfang.redis.bean;

import lombok.Data;
import lombok.experimental.Accessors;

@Data()
@Accessors(chain = true)
public class Person {
    String firstname;
    String lastname;
    Address address;
}
