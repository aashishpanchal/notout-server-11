package com.choic11.config;

import com.choic11.GlobalConstant.GlobalConstant;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;


@PropertySource(value = {"classpath:application.properties"})
@Configuration
public class DBConfig {
    private final String url = "jdbc:mysql://148.66.152.172:3306/sanjay_admin?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8_general_ci";
    private final String username = "sanjay_root";
    private final String password = "*Ha;$hOy6B#[";

    private final String urlTest = "jdbc:mysql://148.66.152.172:3306/sanjay_admin?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8_general_ci";
    private final String usernameTest = "sanjay_root";
    private final String passwordTest = "*Ha;$hOy6B#[";

//    private final String url = "jdbc:mysql://172.31.18.111:3306/choic11?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8_general_ci";
//    private final String username = "user_163_20";
//    private final String password = "Db@Api11@432!!@";
//
//    private final String urlTest = "jdbc:mysql:///choic11?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8_general_ci";
//    private final String usernameTest = "";
//    private final String passwordTest = "";

//    private final String urlTest = "jdbc:mysql://43.204.1.125:3306/choic11?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8_general_ci";
//    private final String usernameTest = "user_163_20";
//    private final String passwordTest = "Db@Api11@432!!@";


    @Value("${jdbc.driverClassName}")
    private String driverClass;

    @Value("${hibernate.dialect}")
    private String dialect;

    @Value("${tomcat.initial-size}")
    private int initialSize;

    @Value("${tomcat.max-wait}")
    private int maxWait;

    @Value("${tomcat.max-active}")
    private int maxActive;

    @Value("${tomcat.max-idle}")
    private int maxIdle;

    @Value("${tomcat.min-idle}")
    private int minIdle;

    @Value("${tomcat.default-auto-commit}")
    private boolean defaultAutoCommit;

    @Value("${tomcat.test-while-idle}")
    private boolean testWhileIdle;

    @Value("${tomcat.test-on-borrow}")
    private boolean testOnBorrow;

    @Value("${tomcat.validation-query}")
    private String validationQuery;


    @Bean
    public DataSource getDataSource() {

        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();

        if (GlobalConstant.isProjectTypeProd()) {
            ds.setUrl(url);
            ds.setUsername(username);
            ds.setPassword(password);
        } else {
            ds.setUrl(urlTest);
            ds.setUsername(usernameTest);
            ds.setPassword(passwordTest);
        }

        ds.setDriverClassName(driverClass);
        ds.setInitialSize(initialSize);
        ds.setMaxWait(maxWait);
        ds.setMaxActive(maxActive);
        ds.setMaxIdle(maxIdle);
        ds.setMinIdle(minIdle);
        ds.setDefaultAutoCommit(defaultAutoCommit);
//        ds.setTestWhileIdle(testWhileIdle);
        ds.setTestOnBorrow(testOnBorrow);
        ds.setValidationQuery(validationQuery);

        return ds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(getDataSource());
        factory.setHibernateProperties(hibernateProperties());
        factory.setPackagesToScan(new String[]{"com.choic11.model"});
        return factory;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.hbm2ddl.auto", "none");
        if (GlobalConstant.isProjectTypeProd()) {
            properties.put("hibernate.show_sql", "false");
            properties.put("hibernate.format_sql", "false");
        } else {
            if (GlobalConstant.isBuildTypeProd()) {
                properties.put("hibernate.show_sql", "false");
                properties.put("hibernate.format_sql", "false");
            }else{
                properties.put("hibernate.show_sql", "true");
                properties.put("hibernate.format_sql", "true");
            }
        }
        return properties;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory factory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(factory);
        return transactionManager;
    }
}
