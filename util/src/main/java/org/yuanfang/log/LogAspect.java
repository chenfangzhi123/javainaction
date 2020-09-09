package org.yuanfang.log;


import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.yuanfang.json.JsonUtils;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
public class LogAspect {

  @Pointcut("@annotation(Log)")
  public void pointcut() {

  }


  @Around("pointcut()")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object res = null;
    Signature signature = joinPoint.getSignature();
    MethodSignature methodSignature = (MethodSignature) signature;
    Method method = methodSignature.getMethod();
    String methodName = method.getName();

    Object[] args = joinPoint.getArgs();

    log.info("pre req method {}, args {}", methodName, JsonUtils.toJSON(args));

    res = joinPoint.proceed(args);
    log.info("after req method {}, args {},resp {},cost {}", methodName, JsonUtils.toJSON(args),
        JsonUtils.toJSON(res), System.currentTimeMillis() - start);

    return res;

  }

}
