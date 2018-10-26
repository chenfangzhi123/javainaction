package introspector;

import org.junit.Before;
import org.junit.Test;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @Name: IntrospectorTest
 * @Description: JavaBean-API：内省机制测试类
 * @Author: XXX
 * @CreateDate: XXX
 * @Version: V1.0
 */
public class IntrospectorTest {

    private User user;

    @Before
    public void init() {
        user = new User();
        user.setName("张三");
        user.setAge(21);
        user.setGender(true);
        user.setBirthday(new Date());
        user.setAddress("北京丰台");
    }

    /**
     * @Name: getBeanPropertyInfo
     * @Description: 获取User-Bean的所有属性信息
     * @Author: XXX
     * @Version: V1.0
     * @CreateDate: XXX
     * @Parameters: @throws Exception
     * @Return: void
     */
    @Test
    public void getBeanPropertyInfo() throws Exception {
        //获取User-BeanInfo对象：beanInfo是对一个Bean的描述，可以通过它取得Bean内部的信息
        /**
         * 获取User-BeanInfo对象
         *      1、Introspector类
         *              是一个工具类，提供了一系列取得BeanInfo的方法；
         *      2、BeanInfo接口
         *              对一个JavaBean的描述，可以通过它取得Bean内部的信息；
         *      3、PropertyDescriptor属性描述器类
         *              对一个Bean属性的描述，它提供了一系列对Bean属性进行操作的方法
         */
        BeanInfo userBeanInfo = Introspector.getBeanInfo(User.class);
        PropertyDescriptor[] pds = userBeanInfo.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            Method method = pd.getReadMethod();
            String methodName = method.getName();
            Object result = method.invoke(user);
            System.out.println(methodName + "-->" + result);
        }
    }

    /**
     * @Name: getBeanPropertyByName
     * @Description: 获取指定属性名称的属性描述器，并对属性进行操作
     * @Author: XXX
     * @Version: V1.0
     * @CreateDate: XXX
     * @Parameters:
     * @Return: void
     */
    @Test
    public void getBeanPropertyByName() throws Exception {
        //获取name属性的属性描述器
        PropertyDescriptor pd = new PropertyDescriptor("name", user.getClass());
        //得到name属性的getter方法
        Method readMethod = pd.getReadMethod();
        //执行getter方法，获取返回值，即name属性的值
        String result = (String) readMethod.invoke(user);
        System.out.println("user.name" + "-->" + result);
        //得到name属性的setter方法
        Method writeMethod = pd.getWriteMethod();
        //执行setter方法，修改name属性的值
        writeMethod.invoke(user, "李四");
        System.out.println("user.name" + "-->" + user.getName());
    }

}