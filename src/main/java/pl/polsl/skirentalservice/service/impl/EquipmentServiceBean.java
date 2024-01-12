/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.service.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import pl.polsl.skirentalservice.core.ModelMapperBean;
import pl.polsl.skirentalservice.core.db.PersistenceBean;
import pl.polsl.skirentalservice.core.s3.S3Bucket;
import pl.polsl.skirentalservice.core.s3.S3ClientBean;
import pl.polsl.skirentalservice.core.servlet.pageable.ServletPagination;
import pl.polsl.skirentalservice.core.servlet.pageable.Slice;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dto.GeneratedBarcodeData;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentReqDto;
import pl.polsl.skirentalservice.dto.equipment.EquipmentDetailsResDto;
import pl.polsl.skirentalservice.dto.equipment.EquipmentRecordResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.entity.EquipmentBrandEntity;
import pl.polsl.skirentalservice.entity.EquipmentColorEntity;
import pl.polsl.skirentalservice.entity.EquipmentEntity;
import pl.polsl.skirentalservice.entity.EquipmentTypeEntity;
import pl.polsl.skirentalservice.exception.AlreadyExistException;
import pl.polsl.skirentalservice.exception.NotFoundException;
import pl.polsl.skirentalservice.service.EquipmentService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Stateless
@SuppressWarnings("unused")
public class EquipmentServiceBean implements EquipmentService {
    private final PersistenceBean persistenceBean;
    private final ModelMapperBean modelMapperBean;
    private final S3ClientBean s3ClientBean;

    @Inject
    public EquipmentServiceBean(
        PersistenceBean persistenceBean,
        ModelMapperBean modelMapperBean,
        S3ClientBean s3ClientBean
    ) {
        this.persistenceBean = persistenceBean;
        this.modelMapperBean = modelMapperBean;
        this.s3ClientBean = s3ClientBean;
    }

    @Override
    public Slice<EquipmentRecordResDto> getPageableEquipments(PageableDto pageableDto) {
        return persistenceBean.startNonTransactQuery(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
            final Long totalEquipments = equipmentDao.findAllEquipmentsCount(pageableDto.filterData());

            final ServletPagination pagination = new ServletPagination(pageableDto.page(),
                pageableDto.total(), totalEquipments);
            if (pagination.checkIfIsInvalid()) {
                return new Slice<>(pagination);
            }
            final List<EquipmentRecordResDto> equipmentsList = equipmentDao
                .findAllPageableEquipmentRecords(pageableDto);
            return new Slice<>(pagination, equipmentsList);
        });
    }

    @Override
    public AddEditEquipmentReqDto getEquipmentDetails(Long equipmentId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
            return equipmentDao
                .findAddEditEquipmentDetails(equipmentId)
                .orElseThrow(() -> new NotFoundException.EquipmentNotFoundException(equipmentId));
        });
    }

    @Override
    public EquipmentDetailsResDto getFullEquipmentDetails(Long equipmentId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
            return equipmentDao
                .findEquipmentDetailsPage(equipmentId)
                .orElseThrow(() -> new NotFoundException.EquipmentNotFoundException(equipmentId));
        });
    }

    @Override
    public boolean checkIfEquipmentExist(Long equipmentId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
            return equipmentDao.checkIfEquipmentExist(equipmentId);
        });
    }

    @Override
    public void createNewEquipment(AddEditEquipmentReqDto reqDto, LoggedUserDataDto loggedUser) {
        final AtomicReference<String> barcodeKey = new AtomicReference<>(StringUtils.EMPTY);
        persistenceBean.startTransaction(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

            if (equipmentDao.checkIfEquipmentModelExist(reqDto.getModel(), null)) {
                throw new AlreadyExistException.EquipmentAlreadyExistException();
            }
            final EquipmentEntity persistNewEquipment = modelMapperBean.map(reqDto, EquipmentEntity.class);
            persistNewEquipment.setType(session.get(EquipmentTypeEntity.class, reqDto.getType()));
            persistNewEquipment.setBrand(session.get(EquipmentBrandEntity.class, reqDto.getBrand()));
            persistNewEquipment.setColor(session.get(EquipmentColorEntity.class, reqDto.getColor()));
            persistNewEquipment.setAvailableCount(Integer.parseInt(reqDto.getCountInStore()));

            final GeneratedBarcodeData generatedBarCode = generateEquipmentBarCode(equipmentDao);

            final String fileName = generatedBarCode.codeKey() + ".png";
            final byte[] data = generatedBarCode.data();
            barcodeKey.set(fileName);

            try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
                s3ClientBean.putObject(S3Bucket.BARCODES, fileName, inputStream,
                    ContentType.IMAGE_PNG, data.length);
            } catch (IOException ex) {
                throw new RuntimeException(ex.getMessage());
            }
            persistNewEquipment.setBarcode(generatedBarCode.codeKey());

            session.persist(persistNewEquipment);
            session.getTransaction().commit();

            log.info("Successful created new equipment with bar code image by: {}. Equipment data: {}",
                loggedUser.getLogin(), reqDto);
        }, () -> {
            final String barcodeKeyValue = barcodeKey.get();
            if (!barcodeKeyValue.equals(StringUtils.EMPTY)) {
                s3ClientBean.deleteObject(S3Bucket.BARCODES, barcodeKeyValue);
            }
        });
    }

    @Override
    public void editEquipment(Long equipmentId, AddEditEquipmentReqDto reqDto, LoggedUserDataDto loggedUser) {
        persistenceBean.startTransaction(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

            final EquipmentEntity equipmentEntity = session.get(EquipmentEntity.class, equipmentId);
            if (equipmentEntity == null) {
                throw new NotFoundException.EquipmentNotFoundException(equipmentId);
            }
            if (equipmentDao.checkIfEquipmentModelExist(reqDto.getModel(), equipmentId)) {
                throw new AlreadyExistException.EquipmentAlreadyExistException();
            }
            modelMapperBean.map(reqDto, equipmentEntity);

            equipmentEntity.setType(session.getReference(EquipmentTypeEntity.class, reqDto.getType()));
            equipmentEntity.setBrand(session.getReference(EquipmentBrandEntity.class, reqDto.getBrand()));
            equipmentEntity.setColor(session.getReference(EquipmentColorEntity.class, reqDto.getColor()));

            session.getTransaction().commit();

            log.info("Successful edit existing equipment with id: {} by: {}. Equipment data: {}",
                equipmentId, loggedUser.getLogin(), reqDto);
        });
    }

    @Override
    public void deleteEquipment(Long equipmentId, LoggedUserDataDto loggedUser) {
        persistenceBean.startTransaction(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

            if (equipmentDao.checkIfEquipmentHasOpenedRents(equipmentId)) {
                throw new AlreadyExistException.EquipmenHasOpenedRentsException();
            }
            final EquipmentEntity equipmentEntity = session.getReference(EquipmentEntity.class, equipmentId);
            if (equipmentEntity == null) {
                throw new NotFoundException.EquipmentNotFoundException(equipmentId);
            }
            session.remove(equipmentEntity);
            s3ClientBean.deleteObject(S3Bucket.BARCODES, equipmentEntity.getBarcode() + ".png");

            session.getTransaction().commit();
            log.info("Equipment with id: {} was succesfuly removed from system by {}.", equipmentId,
                loggedUser.getLogin());
        });
    }

    private GeneratedBarcodeData generateEquipmentBarCode(EquipmentDao equipmentDao) {
        byte[] outputByteData;
        boolean barcodeExist;
        String generatedBarcode;
        do {
            generatedBarcode = getBarcodeChecksum(RandomStringUtils.randomNumeric(12));
            barcodeExist = equipmentDao.checkIfBarCodeExist(generatedBarcode);
        } while (barcodeExist);
        try {
            final EAN13Bean barcodeGenerator = new EAN13Bean();
            final var canvas = new BitmapCanvasProvider(250, BufferedImage.TYPE_BYTE_BINARY, true, 0);
            barcodeGenerator.generateBarcode(canvas, generatedBarcode);
            final BufferedImage barcodeBufferedImage = canvas.getBufferedImage();

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(barcodeBufferedImage, "png", outputStream);

            outputByteData = outputStream.toByteArray();
            outputStream.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return new GeneratedBarcodeData(generatedBarcode, outputByteData);
    }

    private String getBarcodeChecksum(String barcode) {
        int result = 0;
        for (int i = 0; i < barcode.length(); i++) {
            int barSign = Character.getNumericValue(barcode.charAt(i));
            result += barSign * (i % 2 == 0 ? 1 : 3);
        }
        result = (10 - result % 10) % 10;
        barcode += result;
        return barcode;
    }
}
