/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: UserDetailsDao.java
 * Last modified: 3/12/23, 11:01 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.dao.user_details;

import lombok.RequiredArgsConstructor;

import org.hibernate.Session;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@RequiredArgsConstructor
public class UserDetailsDao implements IUserDetailsDao {

    private final Session session;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfCustomerWithSamePeselExist(String pesel, Object customerId) {
        final String jpqlFindPesel = """
            SELECT COUNT(c.id) > 0 FROM CustomerEntity c INNER JOIN c.userDetails d
            WHERE d.pesel = :pesel AND c.id <> :cid
        """;
        return session.createQuery(jpqlFindPesel, Boolean.class)
            .setParameter("pesel", pesel)
            .setParameter("cid", customerId)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfCustomerWithSameEmailExist(String email, Object customerId) {
        final String jpqlFindEmailAddress = """
            SELECT COUNT(c.id) > 0 FROM CustomerEntity c INNER JOIN c.userDetails d
            WHERE d.emailAddress = :emailAddress AND c.id <> :cid
        """;
        return session.createQuery(jpqlFindEmailAddress, Boolean.class)
            .setParameter("emailAddress", email)
            .setParameter("cid", customerId)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfCustomerWithSamePhoneNumberExist(String phoneNumber, Object customerId) {
        final String jpqlFindPhoneNumber = """
            SELECT COUNT(c.id) > 0 FROM CustomerEntity c INNER JOIN c.userDetails d
            WHERE d.phoneNumber = :phoneNumber AND c.id <> :cid
        """;
        return session.createQuery(jpqlFindPhoneNumber, Boolean.class)
            .setParameter("phoneNumber", phoneNumber)
            .setParameter("cid", customerId)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfEmployerWithSamePeselExist(String pesel, Object employerId) {
        final String jpqlFindPesel = """
            SELECT COUNT(e.id) > 0 FROM EmployerEntity e INNER JOIN e.userDetails d
            WHERE d.pesel = :pesel AND e.id <> :eid
        """;
        return session.createQuery(jpqlFindPesel, Boolean.class)
            .setParameter("pesel", pesel)
            .setParameter("eid", employerId)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfEmployerWithSamePhoneNumberExist(String phoneNumber, Object employerId) {
        final String jpqlFindPhoneNumber = """
            SELECT COUNT(e.id) > 0 FROM EmployerEntity e INNER JOIN e.userDetails d
            WHERE d.phoneNumber = :phoneNumber AND e.id <> :eid
        """;
        return session.createQuery(jpqlFindPhoneNumber, Boolean.class)
            .setParameter("phoneNumber", phoneNumber)
            .setParameter("eid", employerId)
            .getSingleResult();
    }
}
