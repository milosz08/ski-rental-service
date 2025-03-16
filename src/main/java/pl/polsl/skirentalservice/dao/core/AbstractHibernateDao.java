package pl.polsl.skirentalservice.dao.core;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;

@RequiredArgsConstructor
public abstract class AbstractHibernateDao {
    protected final Session session;
}
