/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.core;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;

@RequiredArgsConstructor
public abstract class AbstractHibernateDao {
    protected final Session session;
}
