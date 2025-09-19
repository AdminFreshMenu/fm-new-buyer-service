package com.buyer.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
        basePackages = "com.buyer.delivery.repository",
        entityManagerFactoryRef = "fmDeliveryEntityManagerFactory",
        transactionManagerRef = "fmDeliveryTransactionManager"
)
public class FmDeliveryDbConfig {

    @Bean(name = "fmDeliveryDataSource")
    public DataSource fmDeliveryDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/fm_delivery_db_buyer?useSSL=false&serverTimezone=Asia/Kolkata")
                .username("root")
                .password("root")
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
                .packages("com.buyer.delivery.entity")
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