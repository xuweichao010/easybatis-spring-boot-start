package cn.onetozero.easybatis.spring.boot.autoconfigure;

import cn.onetozero.easybatis.EasyBatisConfiguration;
import cn.onetozero.easybatis.fill.FillAttributeHandler;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisLanguageDriverAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.mybatis.spring.boot.autoconfigure.SqlSessionFactoryBeanCustomizer;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties({MybatisProperties.class, EasyProperties.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class, MybatisLanguageDriverAutoConfiguration.class})
public class EasyMybatisAutoConfiguration implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(EasyMybatisAutoConfiguration.class);
    private final MybatisProperties properties;
    private final EasyProperties easyProperties;
    private final Interceptor[] interceptors;
    private final TypeHandler[] typeHandlers;
    private final LanguageDriver[] languageDrivers;
    private final ResourceLoader resourceLoader;
    private final DatabaseIdProvider databaseIdProvider;
    private final List<ConfigurationCustomizer> configurationCustomizers;

    private final List<FillAttributeHandler> fillAttributeHandlers;
    private final List<SqlSessionFactoryBeanCustomizer> sqlSessionFactoryBeanCustomizers;
    private final List<EasyConfigurationCustomizer> easyConfigurationCustomizers;


    public EasyMybatisAutoConfiguration(MybatisProperties properties,
                                        EasyProperties easyProperties,
                                        ObjectProvider<Interceptor[]> interceptorsProvider,
                                        ObjectProvider<TypeHandler[]> typeHandlersProvider,
                                        ObjectProvider<LanguageDriver[]> languageDriversProvider,
                                        ResourceLoader resourceLoader,
                                        ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                        ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                        ObjectProvider<List<FillAttributeHandler>> fillAttributeHandlerObjectProvider,
                                        ObjectProvider<List<SqlSessionFactoryBeanCustomizer>> sqlSessionFactoryBeanCustomizers,
                                        ObjectProvider<List<EasyConfigurationCustomizer>> easyConfigurationCustomizersProvider) {
        this.properties = properties;
        this.easyProperties = easyProperties;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.typeHandlers = typeHandlersProvider.getIfAvailable();
        this.languageDrivers = languageDriversProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
        this.sqlSessionFactoryBeanCustomizers = sqlSessionFactoryBeanCustomizers.getIfAvailable();
        this.easyConfigurationCustomizers = easyConfigurationCustomizersProvider.getIfAvailable();
        this.fillAttributeHandlers = fillAttributeHandlerObjectProvider.getIfAvailable();

    }

    public void afterPropertiesSet() {
        this.checkConfigFileExists();
    }

    private void checkConfigFileExists() {
        if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
            Assert.state(resource.exists(), "Cannot find config location: " + resource + " (please add config file or check your Mybatis configuration)");
        }

    }


    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        applyConfiguration(factory);
        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (this.properties.getTypeAliasesSuperType() != null) {
            factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.typeHandlers)) {
            factory.setTypeHandlers(this.typeHandlers);
        }
        Resource[] mapperLocations = this.properties.resolveMapperLocations();
        if (!ObjectUtils.isEmpty(mapperLocations)) {
            factory.setMapperLocations(mapperLocations);
        }
        Set<String> factoryPropertyNames = Stream
                .of(new BeanWrapperImpl(SqlSessionFactoryBean.class).getPropertyDescriptors()).map(PropertyDescriptor::getName)
                .collect(Collectors.toSet());
        Class<? extends LanguageDriver> defaultLanguageDriver = this.properties.getDefaultScriptingLanguageDriver();
        if (factoryPropertyNames.contains("scriptingLanguageDrivers") && !ObjectUtils.isEmpty(this.languageDrivers)) {
            // Need to mybatis-spring 2.0.2+
            factory.setScriptingLanguageDrivers(this.languageDrivers);
            if (defaultLanguageDriver == null && this.languageDrivers.length == 1) {
                defaultLanguageDriver = this.languageDrivers[0].getClass();
            }
        }
        if (factoryPropertyNames.contains("defaultScriptingLanguageDriver")) {
            // Need to mybatis-spring 2.0.2+
            factory.setDefaultScriptingLanguageDriver(defaultLanguageDriver);
        }
        applySqlSessionFactoryBeanCustomizers(factory);
        return factory.getObject();
    }

//    private void applyConfiguration(SqlSessionFactoryBean factory) {
//
//        if (configuration == null && !StringUtils.hasText(this.easyMybatisProperties().getConfigLocation())) {
//            configuration = new EasyBatisConfiguration();
//        }
//        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
//            configuration.setEasyConfiguration(easyProperties());
//            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
//                customizer.customize(configuration);
//            }
//        }
//        factory.setConfiguration(configuration);
//    }

    private void applyConfiguration(SqlSessionFactoryBean factory) {
        MybatisProperties.CoreConfiguration coreConfiguration = this.properties.getConfiguration();
        EasyBatisConfiguration configuration = null;
        if (coreConfiguration != null || !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new EasyBatisConfiguration();
        }
        if (configuration != null && coreConfiguration != null) {
            coreConfiguration.applyTo(configuration);
        }
        if (configuration != null) {
            configuration.setEasyConfiguration(easyProperties);
        }
        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        // 添加拦截器
        if (configuration != null && !CollectionUtils.isEmpty(fillAttributeHandlers)) {
            for (FillAttributeHandler fillAttributeHandler : fillAttributeHandlers) {
                configuration.addFillAttributeHandler(fillAttributeHandler);
            }
        }
        factory.setConfiguration(configuration);
    }


    private void applySqlSessionFactoryBeanCustomizers(SqlSessionFactoryBean factory) {
        if (!CollectionUtils.isEmpty(this.sqlSessionFactoryBeanCustomizers)) {
            Iterator var2 = this.sqlSessionFactoryBeanCustomizers.iterator();

            while (var2.hasNext()) {
                SqlSessionFactoryBeanCustomizer customizer = (SqlSessionFactoryBeanCustomizer) var2.next();
                customizer.customize(factory);
            }
        }

    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        return executorType != null ? new SqlSessionTemplate(sqlSessionFactory, executorType) : new SqlSessionTemplate(sqlSessionFactory);
    }

    @Configuration
    @Import({AutoConfiguredMapperScannerRegistrar.class})
    @ConditionalOnMissingBean({MapperFactoryBean.class, MapperScannerConfigurer.class})
    public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {
        public MapperScannerRegistrarNotFoundConfiguration() {
        }

        public void afterPropertiesSet() {
            logger.debug("Not found configuration for registering mapper bean using @MapperScan, MapperFactoryBean and MapperScannerConfigurer.");
        }
    }

    public static class AutoConfiguredMapperScannerRegistrar implements BeanFactoryAware, EnvironmentAware, ImportBeanDefinitionRegistrar {
        private BeanFactory beanFactory;
        private Environment environment;

        public AutoConfiguredMapperScannerRegistrar() {
        }

        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            if (!AutoConfigurationPackages.has(this.beanFactory)) {
                logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
            } else {
                logger.debug("Searching for mappers annotated with @Mapper");
                List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
                if (logger.isDebugEnabled()) {
                    packages.forEach((pkg) -> {
                        logger.debug("Using auto-configuration base package '{}'", pkg);
                    });
                }

                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
                builder.addPropertyValue("processPropertyPlaceHolders", true);
                builder.addPropertyValue("annotationClass", Mapper.class);
                builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(packages));
                BeanWrapper beanWrapper = new BeanWrapperImpl(MapperScannerConfigurer.class);
                Set<String> propertyNames = (Set) Stream.of(beanWrapper.getPropertyDescriptors()).map(FeatureDescriptor::getName).collect(Collectors.toSet());
                if (propertyNames.contains("lazyInitialization")) {
                    builder.addPropertyValue("lazyInitialization", "${mybatis.lazy-initialization:false}");
                }

                if (propertyNames.contains("defaultScope")) {
                    builder.addPropertyValue("defaultScope", "${mybatis.mapper-default-scope:}");
                }

                boolean injectSqlSession = (Boolean) this.environment.getProperty("mybatis.inject-sql-session-on-mapper-scan", Boolean.class, Boolean.TRUE);
                if (injectSqlSession && this.beanFactory instanceof ListableBeanFactory) {
                    ListableBeanFactory listableBeanFactory = (ListableBeanFactory) this.beanFactory;
                    Optional<String> sqlSessionTemplateBeanName = Optional.ofNullable(this.getBeanNameForType(SqlSessionTemplate.class, listableBeanFactory));
                    Optional<String> sqlSessionFactoryBeanName = Optional.ofNullable(this.getBeanNameForType(SqlSessionFactory.class, listableBeanFactory));
                    if (!sqlSessionTemplateBeanName.isPresent() && sqlSessionFactoryBeanName.isPresent()) {
                        builder.addPropertyValue("sqlSessionFactoryBeanName", sqlSessionFactoryBeanName.get());
                    } else {
                        builder.addPropertyValue("sqlSessionTemplateBeanName", sqlSessionTemplateBeanName.orElse("sqlSessionTemplate"));
                    }
                }

                builder.setRole(2);
                registry.registerBeanDefinition(MapperScannerConfigurer.class.getName(), builder.getBeanDefinition());
            }
        }

        public void setBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        private String getBeanNameForType(Class<?> type, ListableBeanFactory factory) {
            String[] beanNames = factory.getBeanNamesForType(type);
            return beanNames.length > 0 ? beanNames[0] : null;
        }
    }
}
