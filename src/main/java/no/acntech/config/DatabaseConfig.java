package no.acntech.config;

import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

import no.acntech.listener.ExceptionTranslatorExecuteListener;

@Configuration
public class DatabaseConfig {
/*
    private final DataSource dataSource;

    public DatabaseConfig(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public ConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    public DSLContext dslContext() {
        var configuration = new DefaultConfiguration();
        configuration.set(connectionProvider());
        configuration.set(new DefaultExecuteListenerProvider(new ExceptionTranslatorExecuteListener()));
        return new DefaultDSLContext(configuration);
    }

 */
}
