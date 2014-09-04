package ioc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
/**
 * 修饰需要注入的java bean,其中value是修饰类的id
 * @author pb
 *
 */
public @interface BeanId {
	String[] value() default "";
}
