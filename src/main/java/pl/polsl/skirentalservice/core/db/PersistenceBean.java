/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.db;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
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
import pl.polsl.skirentalservice.core.XMLConfigLoader;

import java.sql.SQLException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Singleton
@Startup
public class PersistenceBean {
    private static final String LIQUIBASE_CONF = "db/db.changelog.xml";
    private static final String HIBERNATE_CONF = "db/hibernate.cfg.xml";

    private SessionFactory sessionFactory;

    public PersistenceBean() {
        final Configuration configurationHib = new Configuration().configure(HIBERNATE_CONF);
        configurationHib.setImplicitNamingStrategy(new CustomPhysicalNamingStrategy());
        XMLConfigLoader.replaceAllPlaceholders(configurationHib.getProperties());

        final org.reflections.Configuration configuration = new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage("pl.polsl.skirentalservice"))
            .setScanners(Scanners.TypesAnnotated);

        final Reflections reflections = new Reflections(configuration);
        final Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(EntityInjector.class);
        for (Class<?> entityClazz : annotatedClasses) {
            configurationHib.addAnnotatedClass(entityClazz);
        }
        final String entities = annotatedClasses.stream().map(Class::getSimpleName).collect(Collectors.joining(", "));
        log.info("Successful loaded Hibernate entities: [{}]", entities);
        try {
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
            log.error("Unable to connect with database. Exception: {}", ex.getMessage());
        } catch (LiquibaseException ex) {
            log.error("Unable to load Liquibase configuration. Exception: {}", ex.getMessage());
        }
    }

    public <T> T wrapToTransaction(Function<Session, T> executableTransaction) {
        T response;
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                response = executableTransaction.apply(session);
                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    log.warn("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                throw ex;
            }
        } catch (Exception ex) {
            throw new TransactionalException(ex);
        }
        return response;
    }

    public void wrapToTransaction(Consumer<Session> executableTransaction) {
        wrapToTransaction(session -> {
            executableTransaction.accept(session);
            return null;
        });
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }
}
