package lk.apiit.eirlss.bangerandco.db;

import lk.apiit.eirlss.bangerandco.models.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        entityManagerFactoryRef = "bangerAndCoEntityManagerFactory",
        basePackages = {"lk.apiit.eirlss.bangerandco.repositories"}
)
public class BangerAndCoDBConfig {

    @Primary
    @Bean(name = "bangerAndCoDatasource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "bangerAndCoEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("bangerAndCoDatasource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages(Booking.class).persistenceUnit("booking")
                .packages(BookingUtility.class).persistenceUnit("bookingUtility")
                .packages(User.class).persistenceUnit("user")
                .packages(UserDetailsImpl.class).persistenceUnit("userDetailsImpl")
                .packages(UserDocument.class).persistenceUnit("userDocument")
                .packages(Utility.class).persistenceUnit("utility")
                .packages(Vehicle.class).persistenceUnit("vehicle")
                .packages(VehicleImage.class).persistenceUnit("vehicleImage")
                .packages(ReportedLicense.class).persistenceUnit("reportedLicense")
                .build();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("bangerAndCoEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
