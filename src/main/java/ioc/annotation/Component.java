package ioc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 * 修饰需要被加载的java bean,其中value是修饰类的id
 * @author pb
 *
 */
public @interface Component {
	String[] value() default "";
}
