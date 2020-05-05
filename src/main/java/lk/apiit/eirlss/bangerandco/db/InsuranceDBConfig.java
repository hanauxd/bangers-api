package lk.apiit.eirlss.bangerandco.db;

import lk.apiit.eirlss.bangerandco.insurance.models.Fraud;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "insuranceEntityManagerFactory",
        transactionManagerRef = "insuranceTransactionManager",
        basePackages = {"lk.apiit.eirlss.bangerandco.insurance.repositories"}
)
public class InsuranceDBConfig {

    @Bean(name = "insuranceDataSource")
    @ConfigurationProperties("spring.insurance.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("insuranceEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean insuranceEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("insuranceDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages(Fraud.class).persistenceUnit("fraud")
                .build();
    }

    @Bean("insuranceTransactionManager")
    public PlatformTransactionManager insuranceTransactionManager(
            @Qualifier("insuranceEntityManagerFactory") EntityManagerFactory insuranceEntityManagerFactory
    ) {
        return new JpaTransactionManager(insuranceEntityManagerFactory);
    }
}
