/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.pdf;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import pl.polsl.skirentalservice.dto.PriceUnitsDto;
import pl.polsl.skirentalservice.exception.ServletException;
import pl.polsl.skirentalservice.pdf.dto.GeneratedPdfData;
import pl.polsl.skirentalservice.pdf.dto.PdfDocumentData;
import pl.polsl.skirentalservice.pdf.dto.PdfEquipmentDataDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractPdfGenerator {
    private final String headerText;
    private final String contentText;

    protected final Map<String, String> leftComponents;
    protected final Map<String, String> rightComponents;

    private static final ContentType APPLICATION_PDF = ContentType.create("application/pdf");
    private static final String[] TABLE_HEADERS = {
        "Nazwa sprzętu", "Ilość", "Cena netto", "Cena brutto", "Kaucja netto", "Kaucja brutto",
    };

    AbstractPdfGenerator(String headerText, String contentText) {
        this.headerText = headerText;
        this.contentText = contentText;
        leftComponents = new HashMap<>();
        rightComponents = new HashMap<>();
    }

    public GeneratedPdfData generate(PdfDocumentData pdfData) {
        GeneratedPdfData generatedPdfData;

        final String fileName = pdfData.getIssuedIdentifier().replaceAll("/", "-") + ".pdf";
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (final PdfWriter pdfWriter = new PdfWriter(outputStream)) {
            final CustomPdfDocument document = createDocument(pdfWriter);
            final PriceUnitsDto units = pdfData.getPriceUnits();

            appendToLeftComponents(pdfData);
            appendToRightComponents(pdfData);

            // header
            final CustomPdfTable headerTable = new CustomPdfTable(new float[]{ 60, 40 });
            headerTable.addCell(CustomPdfCell.createHeaderTableCell(headerText));

            final CustomPdfTable headerRightContentTable = new CustomPdfTable(2);
            headerRightContentTable.addInlineKeyValueData("Nr " + headerText, pdfData.getIssuedIdentifier());
            headerRightContentTable.addInlineKeyValueData("Data " + headerText, setHeaderDate(pdfData));
            headerTable.appendTableToTable(headerRightContentTable);

            document.add(headerTable);
            document.addHorizontalDivider();

            // left and right details
            final CustomPdfTable detailsTable = new CustomPdfTable(2);
            detailsTable.appendColumnCells("Dane klienta", leftComponents);
            detailsTable.appendColumnCells("Dane " + headerText, rightComponents);
            document.add(detailsTable);
            document.addHorizontalDivider();

            // equipments table data
            document.addHeader(contentText);
            final CustomPdfTable equipmentsTable = new CustomPdfTable(TABLE_HEADERS.length, 10f);
            equipmentsTable.addHeader(TABLE_HEADERS);
            for (final PdfEquipmentDataDto pdfEquipment : pdfData.getEquipments()) {
                equipmentsTable.addDataCell(pdfEquipment.getName());
                equipmentsTable.addDataCell(pdfEquipment.getCount() + " szt.");
                equipmentsTable.addDataCell(pdfEquipment.getPriceNetto() + " zł");
                equipmentsTable.addDataCell(pdfEquipment.getPriceBrutto() + " zł");
                equipmentsTable.addDataCell(pdfEquipment.getDepositPriceNetto() + " zł");
                equipmentsTable.addDataCell(pdfEquipment.getDepositPriceBrutto() + " zł");
            }
            document.add(equipmentsTable);

            // prices section
            document.addPriceUnitBruttoParagraph("Cena za wypożyczenie (brutto): " + units.getTotalPriceBrutto());
            document.addPriceUnitNettoParagraph("(netto): " + units.getTotalPriceNetto() + " zł");
            document.add(new Paragraph("\n").setFixedLeading(2f));
            document.addPriceUnitBruttoParagraph("+ kaucja (brutto): " + units.getTotalDepositPriceBrutto());
            document.addPriceUnitNettoParagraph("(netto): " + units.getTotalDepositPriceNetto() + " zł");
            document.addPricesDivider();

            // summary prices section
            document.addSummaryPriceUnitBruttoParagraph("Łącznie (brutto): " + pdfData.getTotalSumPriceBrutto());
            document.addSummaryPriceUnitNettoParagraph("(netto): " + pdfData.getTotalSumPriceNetto());
            document.addHorizontalDivider();

            // description section
            document.addDetailsDescription(pdfData.getDescription());

            document.close();
            generatedPdfData = GeneratedPdfData.builder()
                .filename(fileName)
                .pdfData(outputStream.toByteArray())
                .type(APPLICATION_PDF)
                .build();

            outputStream.close();
            log.info("Pdf FV file: {} was successfully generated. Pdf data: {}", fileName, pdfData);
        } catch (IOException ex) {
            throw new ServletException.PdfGeneratorException(ex);
        }
        return generatedPdfData;
    }

    protected CustomPdfDocument createDocument(PdfWriter pdfWriter) throws IOException {
        final PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        return new CustomPdfDocument(pdfDocument);
    }

    protected void appendToLeftComponents(PdfDocumentData pdfDocumentData) {
        leftComponents.put("Imię i nazwisko", pdfDocumentData.getFullName());
        leftComponents.put("Nr PESEL", pdfDocumentData.getPesel());
        leftComponents.put("Nr telefonu", pdfDocumentData.getPhoneNumber());
        leftComponents.put("Adres email", pdfDocumentData.getEmail());
        leftComponents.put("Adres", pdfDocumentData.getAddress());
    }

    protected abstract String setHeaderDate(PdfDocumentData pdfDocumentData);
    protected abstract void appendToRightComponents(PdfDocumentData pdfDocumentData);
}
