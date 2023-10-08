/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.db;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class HibernateDbSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateDbSingleton.class);
    private static final String LIQUIBASE_CONF = "db/db.changelog.xml";
    private static final String HIBERNATE_CONF = "db/hibernate.cfg.xml";

    private SessionFactory sessionFactory;
    private static volatile HibernateDbSingleton instance;

    private HibernateDbSingleton() {
        try {
            final Configuration configurationHib = new Configuration().configure(HIBERNATE_CONF);

            final org.reflections.Configuration configuration = new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("pl.polsl.skirentalservice"))
                .setScanners(Scanners.TypesAnnotated);

            final Reflections reflections = new Reflections(configuration);
            final Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(EntityInjector.class);
            for (Class<?> entityClazz : annotatedClasses) {
                configurationHib.addAnnotatedClass(entityClazz);
            }
            final String entities = annotatedClasses.stream().map(Class::getSimpleName).collect(Collectors.joining(", "));
            LOGGER.info("Successful loaded Hibernate entities: [ {} ]", entities);

            final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configurationHib.getProperties()).build();

            final MetadataSources sources = new MetadataSources(serviceRegistry);
            final ConnectionProvider provider = sources.getServiceRegistry().getService(ConnectionProvider.class);
            final JdbcConnection jdbcConnection = new JdbcConnection(provider.getConnection());

            final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
            final Liquibase liquibase = new Liquibase(LIQUIBASE_CONF, new ClassLoaderResourceAccessor(), database);

            liquibase.getDatabase().setDatabaseChangeLogTableName("_liquibase_changelog");
            liquibase.getDatabase().setDatabaseChangeLogLockTableName("_liquibase_changelog_lock");
            liquibase.update();

            sessionFactory = configurationHib.buildSessionFactory(serviceRegistry);
        } catch (SQLException ex) {
            LOGGER.error("Unable to connect with database. Exception: {}", ex.getMessage());
        } catch (LiquibaseException ex) {
            LOGGER.error("Unable to load Liquibase configuration. Exception: {}", ex.getMessage());
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static synchronized HibernateDbSingleton getInstance() {
        if (Objects.isNull(instance)) {
            instance = new HibernateDbSingleton();
        }
        return instance;
    }
}
