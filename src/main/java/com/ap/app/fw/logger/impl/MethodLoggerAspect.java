package com.ap.app.fw.logger.impl;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import com.ap.app.fw.logger.MethodLogger;

/**
 * @author apanjiyar
 */
@Aspect
@Component
public class MethodLoggerAspect
{
    private static final Logger logger = LoggerFactory.getLogger(MethodLogger.class);

    @Before("@annotation(MethodLogger)")
    public void logBeforeMethod(JoinPoint point) throws Throwable
    {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        MethodLogger methodLoggerAnnotation = method.getAnnotation(MethodLogger.class);
        boolean logParam = methodLoggerAnnotation.logInParam();
        if (logParam)
        {
            logger.info("Enter into Method= [{}] of class= [{}] with value: [{}]", method.getName(),
                    methodSignature.getDeclaringTypeName(), argsAsString(point.getArgs()));
        }
        else
        {
            logger.info("Enter into Method= [{}] of class= [{}]", method.getName(),
                    methodSignature.getDeclaringTypeName());
        }
    }

    @AfterReturning(value = "@annotation(MethodLogger)", returning = "retVal")
    public void logAfterMethod(JoinPoint point, Object retVal) throws Throwable
    {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        MethodLogger methodLoggerAnnotation = method.getAnnotation(MethodLogger.class);
        boolean logParam = methodLoggerAnnotation.logOutParam();
        if (logParam)
        {
            logger.info("Exit from Method= [{}] of class= [{}] with value: [{}]", method.getName(),
                    methodSignature.getDeclaringTypeName(), retVal);
        }
        else
        {
            logger.info("Exit from Method= [{}] of class= [{}]", method.getName(),
                    methodSignature.getDeclaringTypeName());
        }
    }

    @Around("@annotation(MethodLogger)")
    public Object logAroundMethod(ProceedingJoinPoint point) throws Throwable
    {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        MethodLogger methodLoggerAnnotation = method.getAnnotation(MethodLogger.class);
        boolean trackTime = methodLoggerAnnotation.trackTime();
        long start = System.currentTimeMillis();
        boolean isExceptionThrown = false;
        try
        {
            Object result = point.proceed();
            return result;
        }
        catch (Throwable ex)
        {
            isExceptionThrown = true;
            throw ex;
        }
        finally
        {
            long timeTaken = System.currentTimeMillis() - start;
            if (trackTime)
            {
                logger.info(
                        "Total time taken (in millisecond) for method= [{}] of class= [{}] is: [{}] with exception thrown: [{}]",
                        method.getName(), methodSignature.getDeclaringTypeName(), timeTaken, isExceptionThrown);
            }
        }
    }

    private static String argsAsString(Object[] args)
    {
        StringBuilder sb = new StringBuilder();
        if (args != null && args.length > 0)
        {
            for (int xx = 0; xx < args.length; xx++)
            {
                sb.append("\nargs[");
                sb.append(xx);
                sb.append("]=");
                sb.append((args[xx] != null) ? args[xx].toString() : "null");
            }
        }
        return sb.toString();
    }

}
