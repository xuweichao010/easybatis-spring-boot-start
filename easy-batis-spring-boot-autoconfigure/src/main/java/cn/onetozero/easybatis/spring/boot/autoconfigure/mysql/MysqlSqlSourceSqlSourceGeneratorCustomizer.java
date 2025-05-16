package cn.onetozero.easybatis.spring.boot.autoconfigure.mysql;


import cn.onetozero.easybatis.mysql.MysqlSqlSourceGenerator;

/**
 * 类描述：
 * @author  徐卫超 (cc)
 * @since 2023/2/10 14:53
 */
@FunctionalInterface
public interface MysqlSqlSourceSqlSourceGeneratorCustomizer {

    void customize(MysqlSqlSourceGenerator mysqlSqlSourceGenerator);
}
