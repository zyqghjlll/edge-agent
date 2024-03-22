package com.edge.agent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author Administrator
 */
@SpringBootApplication
@MapperScan("com.edge.agent.repository.mysql.mapper")
@EnableCaching
public class AgentApplication {

    public static void main(String[] args) {
        try {
            new SpringApplicationBuilder(AgentApplication.class)
                    .beanNameGenerator(new UniqueNameGenerator())
                    .run(args);
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    // 在这里执行你想要在程序退出时执行的逻辑
                }
            });
        }

    }

    /**
     * 由于需要混淆代码,混淆后类都是A B C,spring 默认是把A B C当成BeanName,BeanName又不能重复导致报错
     * 所以需要重新定义BeanName生成策略
     */
    @Component("UniqueNameGenerator")
    public static class UniqueNameGenerator extends AnnotationBeanNameGenerator {
        /**
         * 重写buildDefaultBeanName
         * 其他情况(如自定义BeanName)还是按原来的生成策略,只修改默认(非其他情况)生成的BeanName带上包名
         */
        @Override
        public @NotNull String buildDefaultBeanName(BeanDefinition definition) {
            //全限定类名
            return Objects.requireNonNull(definition.getBeanClassName());
        }
    }

}
