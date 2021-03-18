package com.ap.app.fw.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author apanjiyar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface MethodLogger
{
    boolean logInParam() default true;

    boolean logOutParam() default true;

    boolean trackTime() default true;

}
