/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: OwnerDeleteEmployerServlet.java
 * Last modified: 6/3/23, 1:19 AM
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

package pl.polsl.skirentalservice.domain.owner.employer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import pl.polsl.skirentalservice.util.Utils;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.core.ssh.SshSocketSingleton;
import pl.polsl.skirentalservice.dao.employer.EmployerDao;
import pl.polsl.skirentalservice.dao.employer.IEmployerDao;
import pl.polsl.skirentalservice.ssh.ExecCommandPerformer;
import pl.polsl.skirentalservice.ssh.IExecCommandPerformer;

import static pl.polsl.skirentalservice.exception.NotFoundException.UserNotFoundException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.EmployerHasOpenedRentsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/delete-employer")
public class OwnerDeleteEmployerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerAddEmployerServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final SshSocketSingleton sshSocket = SshSocketSingleton.getInstance();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final IEmployerDao employerDao = new EmployerDao(session);

                final var deletingEmployer = employerDao.findEmployerBasedId(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
                if (employerDao.checkIfEmployerHasOpenedRents(userId)) throw new EmployerHasOpenedRentsException();

                final IExecCommandPerformer commandPerformer = new ExecCommandPerformer(sshSocket);
                commandPerformer.deleteMailbox(deletingEmployer.getEmailAddress());
                session.remove(deletingEmployer);

                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Pomyślnie usunięto pracownika z ID <strong>#" + userId + "</strong> z systemu wraz z " +
                    "jego skrzynką pocztową."
                );
                session.getTransaction().commit();
                LOGGER.info("Employer with id: {} was succesfuly removed from system.", userId);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (Exception ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to remove employer with id: {}. Cause: {}", userId, ex.getMessage());
        }
        httpSession.setAttribute(SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/owner/employers");
    }
}
