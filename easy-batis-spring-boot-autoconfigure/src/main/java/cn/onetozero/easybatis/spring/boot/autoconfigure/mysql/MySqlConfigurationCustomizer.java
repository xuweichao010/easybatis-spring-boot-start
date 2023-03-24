package cn.onetozero.easybatis.spring.boot.autoconfigure.mysql;

import cn.onetozero.easy.parse.utils.StringUtils;
import cn.onetozero.easybatis.EasyBatisConfiguration;
import cn.onetozero.easybatis.mysql.MysqlSqlSourceGenerator;
import cn.onetozero.easybatis.supports.DefaultParamArgsResolver;
import cn.onetozero.easybatis.supports.DriverDatabaseIdProvider;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 类描述：配置config 并注册
 * 作者：徐卫超 (cc)
 * 时间 2023/2/9 19:06
 */

public class MySqlConfigurationCustomizer implements ConfigurationCustomizer {

    private final List<MysqlSqlSourceSqlSourceGeneratorCustomizer> sqlSourceSqlSourceGeneratorCustomizer;

    public MySqlConfigurationCustomizer(List<MysqlSqlSourceSqlSourceGeneratorCustomizer> sqlSourceSqlSourceGeneratorCustomizer) {
        this.sqlSourceSqlSourceGeneratorCustomizer = sqlSourceSqlSourceGeneratorCustomizer;
    }


    @Override
    public void customize(Configuration configuration) {
        if (configuration instanceof EasyBatisConfiguration) {
            doCustomize((EasyBatisConfiguration) configuration);
        }
    }

    private void doCustomize(EasyBatisConfiguration configuration) {
        MysqlSqlSourceGenerator mysqlSqlSourceGenerator = new MysqlSqlSourceGenerator(configuration);
        configuration.registrySqlSourceGenerator(DriverDatabaseIdProvider.MYSQL, mysqlSqlSourceGenerator, new DefaultParamArgsResolver(configuration));
        if (!StringUtils.hasText(configuration.getDatabaseId())) {
            configuration.setDatabaseId(DriverDatabaseIdProvider.MYSQL);
        }
        if (!CollectionUtils.isEmpty(sqlSourceSqlSourceGeneratorCustomizer)) {
            for (MysqlSqlSourceSqlSourceGeneratorCustomizer customizer : sqlSourceSqlSourceGeneratorCustomizer) {
                customizer.customize(mysqlSqlSourceGenerator);
            }
        }
    }
}
