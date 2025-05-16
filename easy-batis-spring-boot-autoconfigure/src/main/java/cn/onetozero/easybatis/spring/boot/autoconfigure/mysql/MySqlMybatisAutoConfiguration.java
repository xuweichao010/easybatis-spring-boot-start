package cn.onetozero.easybatis.spring.boot.autoconfigure.mysql;

import cn.onetozero.easybatis.spring.boot.autoconfigure.EasyMybatisAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 类描述：创建关于mysql的自动配置类
 * @author  徐卫超 (cc)
 * @since 2023/2/9 15:06
 */
@org.springframework.context.annotation.Configuration
@AutoConfigureAfter({EasyMybatisAutoConfiguration.class})
@ConditionalOnClass(name = {"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"})
public class MySqlMybatisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MySqlConfigurationCustomizer mySqlConfigurationCustomizer(ObjectProvider<List<MysqlSqlSourceSqlSourceGeneratorCustomizer>> sqlSourceSqlSourceGeneratorObjectProvider) {
        return new MySqlConfigurationCustomizer(sqlSourceSqlSourceGeneratorObjectProvider.getIfAvailable());
    }
}
