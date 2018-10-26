package introspector;

import lombok.Data;

import java.util.Date;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/3/14-14:11
 * @ModifiedBy:
 */
@Data
public class User {
    private String name;
    private boolean gender;
    private String address;
    private int age;
    private Date birthday;
}
