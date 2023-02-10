/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: HibernateUtil.java
 *  Last modified: 30/01/2023, 17:48
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.db;

import org.slf4j.*;

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

public class HibernateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class);
    private static final String LIQUIBASE_CONF = "db/db.changelog.xml";
    private static final String HIBERNATE_CONF = "db/hibernate.cfg.xml";
    private static final String DB_AUTH_PROP = "/db/hibernate.properties";

    private static final SessionFactory sessionFactory = buildSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static SessionFactory buildSessionFactory() {
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

            return configurationHib.buildSessionFactory(serviceRegistry);
        } catch (SQLException ex) {
            LOGGER.error("Unable to connect with database. Exception: {}", ex.getMessage());
        } catch (LiquibaseException ex) {
            LOGGER.error("Unable to load Liquibase configuration. Exception: {}", ex.getMessage());
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
