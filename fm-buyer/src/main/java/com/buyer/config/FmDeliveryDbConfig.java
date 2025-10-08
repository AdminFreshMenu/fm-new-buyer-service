package com.buyer.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.buyer.deliveryDB.repository",
        entityManagerFactoryRef = "fmDeliveryEntityManagerFactory",
        transactionManagerRef = "fmDeliveryTransactionManager"
)
public class FmDeliveryDbConfig {

    @Value("${spring.datasource.delivery.url}")
    private String deliveryDbUrl;

    @Value("${spring.datasource.delivery.username}")
    private String deliveryDbUsername;

    @Value("${spring.datasource.delivery.password}")
    private String deliveryDbPassword;

    @Value("${spring.datasource.delivery.driver-class-name}")
    private String deliveryDbDriverClassName;

    @Bean(name = "fmDeliveryDataSource")
    public DataSource fmDeliveryDataSource() {
        return DataSourceBuilder.create()
                .url(deliveryDbUrl)
                .username(deliveryDbUsername)
                .password(deliveryDbPassword)
                .driverClassName(deliveryDbDriverClassName)
                .build();
    }

    @Bean(name = "fmDeliveryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean fmDeliveryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("fmDeliveryDataSource") DataSource dataSource) {
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.jdbc.time_zone", "Asia/Kolkata");
        properties.put("hibernate.connection.init_sql", "SET sql_mode=''");
        properties.put("hibernate.show_sql", "true");
        
        return builder
                .dataSource(dataSource)
                .packages("com.buyer.deliveryDB.entity")
                .persistenceUnit("fmDelivery")
                .properties(properties)
                .build();
    }

    @Bean(name = "fmDeliveryTransactionManager")
    public PlatformTransactionManager fmDeliveryTransactionManager(
            @Qualifier("fmDeliveryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}