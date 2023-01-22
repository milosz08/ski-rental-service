/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: HibernateFactory.java
 *  Last modified: 27/12/2022, 22:48
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.db;

import org.slf4j.*;
import jakarta.ejb.*;

import org.reflections.util.*;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.*;
import java.sql.SQLException;
import java.util.stream.Collectors;

import liquibase.Liquibase;
import liquibase.database.*;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.MetadataSources;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Startup
@Singleton(name = "HibernateFactoryBean")
public class HibernateBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateBean.class);
    private static final String LIQUIBASE_CONF = "db/db.changelog.xml";
    private static final String HIBERNATE_CONF = "db/hibernate.cfg.xml";
    private static final String DB_AUTH_PROP = "/db/hibernate.properties";

    private SessionFactory sessionFactory;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    HibernateBean() {
        try {
            final Configuration configuration = new Configuration().configure(HIBERNATE_CONF);
            loadMappedHibernateEntities(configuration);

            final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();

            final MetadataSources sources = new MetadataSources(serviceRegistry);
            final ConnectionProvider provider = sources.getServiceRegistry().getService(ConnectionProvider.class);
            final JdbcConnection jdbcConnection = new JdbcConnection(provider.getConnection());

            final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
            final Liquibase liquibase = new Liquibase(LIQUIBASE_CONF, new ClassLoaderResourceAccessor(), database);

            liquibase.getDatabase().setDatabaseChangeLogTableName("_liquibase_changelog");
            liquibase.getDatabase().setDatabaseChangeLogLockTableName("_liquibase_changelog_lock");
            liquibase.update();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            LOGGER.info("Successful connected with database.");
        } catch (SQLException ex) {
            LOGGER.error("Unable to connect with database. Exception: {}", ex.getMessage());
        } catch (LiquibaseException ex) {
            LOGGER.error("Unable to load Liquibase configuration. Exception: {}", ex.getMessage());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void loadMappedHibernateEntities(final Configuration conf) {
        final org.reflections.Configuration configuration = new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage("pl.polsl.skirentalservice"))
            .setScanners(Scanners.TypesAnnotated);

        final Reflections reflections = new Reflections(configuration);
        final Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(EntityInjector.class);
        for (Class<?> entityClazz : annotatedClasses) {
            conf.addAnnotatedClass(entityClazz);
        }
        final String entities = annotatedClasses.stream().map(Class::getSimpleName).collect(Collectors.joining(", "));
        LOGGER.info("Successful loaded Hibernate entities: [ {} ]", entities);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Session open() {
        return sessionFactory.openSession();
    }
}
