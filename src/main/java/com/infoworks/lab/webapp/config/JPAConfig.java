package com.infoworks.lab.webapp.config;

import com.infoworks.lab.domain.entities.Username;
import com.infoworks.lab.jsql.ExecutorType;
import com.infoworks.lab.jsql.JsqlConfig;
import com.it.soul.lab.sql.SQLExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Optional;

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.infoworks.lab.domain.repositories"}
)
@PropertySource("classpath:mysql-db.properties")
@PropertySource("classpath:h2-db.properties")
public class JPAConfig {

    private static Logger LOG = LoggerFactory.getLogger(JPAConfig.class);
    private Environment env;

    public JPAConfig(Environment env) {
        this.env = env;
    }

    @Value("${spring.datasource.driver-class-name}")
    String driverClassName;
    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String username;
    @Value("${spring.datasource.password}")
    String password;
    @Value("${app.db.name}")
    String persistenceUnitName;

    @Bean
    JsqlConfig getJsqlConfig(DataSource dataSource){
        return new JsqlConfig(dataSource);
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    //Since SQLExecutor is a WebApplicationContext.SCOPE_REQUEST Variable, it will automatically close connection when garbage-collected.
    SQLExecutor executor(JsqlConfig config) throws Exception {
        SQLExecutor exe = (SQLExecutor) config.create(ExecutorType.SQL, env.getProperty("app.db.name"));
        LOG.info("Executor-Connection Has been Created.");
        return exe;
    }

    @Primary @Bean
    public DataSource dataSource(){
        DataSource dataSource = DataSourceBuilder
                .create()
                .username(username)
                .password(password)
                .url(url)
                .driverClassName(driverClassName)
                .build();
        return dataSource;
    }

    @Primary @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, DataSource dataSource){
        return builder
                .dataSource(dataSource)
                .packages("com.infoworks.lab.domain.entities")
                .persistenceUnit(persistenceUnitName)
                .build();
    }

    @Primary @Bean
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory entityManagerFactory){
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public AuditorAware<Username> auditor() {
        return () -> Optional.ofNullable(new Username("GOD-ADMIN-USERNAME"));
    }

}
