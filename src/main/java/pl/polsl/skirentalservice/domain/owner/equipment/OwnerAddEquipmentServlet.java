/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: AddEquipmentServlet.java
 *  Last modified: 23/01/2023, 18:57
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.equipment;

import org.slf4j.*;
import org.hibernate.*;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.dto.equipment.*;
import pl.polsl.skirentalservice.core.ConfigBean;

import java.io.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static java.io.File.separator;
import static java.util.Objects.isNull;
import static java.lang.Integer.parseInt;
import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.PageTitle.OWNER_ADD_EQUIPMENT_PAGE;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/add-equipment")
public class OwnerAddEquipmentServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerAddEquipmentServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    @EJB private ValidatorBean validator;
    @EJB private ModelMapperBean modelMapper;
    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = getAndDestroySessionAlert(req, OWNER_ADD_EQUIPMENT_PAGE_ALERT);
        var resDto = getFromSessionAndDestroy(req, getClass().getName(), AddEditEquipmentResDto.class);
        if (isNull(resDto)) resDto = new AddEditEquipmentResDto();

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String jpqlFindEquipmentTypes = "SELECT new pl.polsl.skirentalservice.dto.FormSelectTupleDto(" +
                    "CAST(t.id AS string), t.name) FROM EquipmentTypeEntity t ORDER BY t.id";
                final List<FormSelectTupleDto> equipmentTypes = session
                    .createQuery(jpqlFindEquipmentTypes, FormSelectTupleDto.class)
                    .getResultList();
                resDto.insertTypesSelects(equipmentTypes);

                final String jpqlFindEquipmentBrands = "SELECT new pl.polsl.skirentalservice.dto.FormSelectTupleDto(" +
                    "CAST(t.id AS string), t.name) FROM EquipmentBrandEntity t ORDER BY t.id";
                final List<FormSelectTupleDto> equipmentBrands = session
                    .createQuery(jpqlFindEquipmentBrands, FormSelectTupleDto.class)
                    .getResultList();
                resDto.insertBrandsSelects(equipmentBrands);

                final String jpqlFindEquipmentColors = "SELECT new pl.polsl.skirentalservice.dto.FormSelectTupleDto(" +
                    "CAST(t.id AS string), t.name) FROM EquipmentColorEntity t ORDER BY t.id";
                final List<FormSelectTupleDto> equipmentColors = session
                    .createQuery(jpqlFindEquipmentColors, FormSelectTupleDto.class)
                    .getResultList();
                resDto.insertColorsSelects(equipmentColors);

                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("addEditEquipmentData", resDto);
        req.setAttribute("addEditText", "Dodaj");
        req.setAttribute(EQ_TYPES_MODAL_DATA.getName(), getAndDestroySessionModalData(req, EQ_TYPES_MODAL_DATA));
        req.setAttribute(EQ_BRANDS_MODAL_DATA.getName(), getAndDestroySessionModalData(req, EQ_BRANDS_MODAL_DATA));
        req.setAttribute(EQ_COLORS_MODAL_DATA.getName(), getAndDestroySessionModalData(req, EQ_COLORS_MODAL_DATA));
        req.setAttribute("title", OWNER_ADD_EQUIPMENT_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/equipment/owner-add-edit-equipment.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = getLoggedUserLogin(req);

        final AddEditEquipmentReqDto reqDto = new AddEditEquipmentReqDto(req);
        final AddEditEquipmentResDto resDto = new AddEditEquipmentResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/owner/add-equipment");
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String jpqlEquipmentModelExist =
                    "SELECT COUNT(e.id) > 0 FROM EquipmentEntity e WHERE LOWER(e.model) = LOWER(:model)";
                final Boolean equipmentModelExist = session.createQuery(jpqlEquipmentModelExist, Boolean.class)
                    .setParameter("model", reqDto.getModel())
                    .getSingleResult();
                if (equipmentModelExist) throw new EquipmentAlreadyExistException();

                modelMapper.getModelMapper().getConfiguration().setAmbiguityIgnored(false);
                final EquipmentEntity persistNewEquipment = modelMapper.map(reqDto, EquipmentEntity.class);
                persistNewEquipment.setEquipmentType(session.get(EquipmentTypeEntity.class, reqDto.getType()));
                persistNewEquipment.setEquipmentBrand(session.get(EquipmentBrandEntity.class, reqDto.getBrand()));
                persistNewEquipment.setEquipmentColor(session.get(EquipmentColorEntity.class, reqDto.getColor()));
                persistNewEquipment.setAvailableCount(parseInt(reqDto.getCountInStore()));
                modelMapper.getModelMapper().getConfiguration().setAmbiguityIgnored(true);

                boolean barcodeExist;
                String generatedBarcode;
                do {
                    generatedBarcode = getBarcodeChecksum(randomNumeric(12));
                    final String jpqlFindBarcode =
                        "SELECT COUNT(e.id) > 0 FROM EquipmentEntity e WHERE e.barcode = :barcode";
                    barcodeExist = session.createQuery(jpqlFindBarcode, Boolean.class)
                        .setParameter("barcode", generatedBarcode)
                        .getSingleResult();
                } while (barcodeExist);

                final EAN13Bean barcodeGenerator = new EAN13Bean();
                final var canvas = new BitmapCanvasProvider(250, TYPE_BYTE_BINARY, true, 0);
                barcodeGenerator.generateBarcode(canvas, generatedBarcode);
                final BufferedImage barcodeBufferedImage = canvas.getBufferedImage();

                final File barCodesDir = new File(config.getUploadsDir() + separator + "bar-codes");
                barCodesDir.mkdir();
                final File outputFile = new File(barCodesDir, generatedBarcode + ".png");
                if (outputFile.createNewFile()) {
                    ImageIO.write(barcodeBufferedImage, "png", outputFile);
                } else throw new RuntimeException("Nieudane zapisanie kodu kreskowego sprzętu.");

                persistNewEquipment.setBarcode(generatedBarcode);
                session.persist(persistNewEquipment);
                session.getTransaction().commit();
                alert.setType(INFO);
                alert.setMessage(
                    "Nastąpiło pomyślne zapisanie nowego sprzętu oraz wygenerowanie dla niego kodu kreskowego."
                );
                httpSession.setAttribute(COMMON_EQUIPMENTS_PAGE_ALERT.getName(), alert);
                httpSession.removeAttribute(getClass().getName());
                LOGGER.info("Successful created new equipment with bar code image by: {}. Equipment data: {}",
                    loggedUser, reqDto);
                res.sendRedirect("/owner/equipments");
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(OWNER_ADD_EQUIPMENT_PAGE_ALERT.getName(), alert);
            LOGGER.error("Unable to create new equipment. Cause: {}", ex.getMessage());
            res.sendRedirect("/owner/add-equipment");
        }
    }
}
