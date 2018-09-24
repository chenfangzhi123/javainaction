package org.yuanfang.rabbit.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/19-22:42
 * @ModifiedBy:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExampleEvent {
    int id;
    String message;
}
