package cn.onetozero.easybatis.spring.boot.autoconfigure;

import cn.onetozero.easy.parse.EasyConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 类描述：
 * 作者：徐卫超 (cc)
 * 时间 2023/3/24 11:19
 */
@ConfigurationProperties(prefix = EasyProperties.EASY_PREFIX)
public class EasyProperties extends EasyConfiguration {
    public static final String EASY_PREFIX = "mybatis.easybatis";

}
