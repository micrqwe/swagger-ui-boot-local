package xyz.micrqwe.annotations;

import org.springframework.context.annotation.Import;
import xyz.micrqwe.context.ApplicationSwagger;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by shaowenxing on 2017/11/23.
 * 用于启动本地生成程序
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({ApplicationSwagger.class})
public @interface EnableSwagger2UiLocal {
}
