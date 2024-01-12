/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.pdf;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class CustomPdfCell extends Cell {
    public static CustomPdfCell createRootCell() {
        final CustomPdfCell customCell = new CustomPdfCell();
        customCell.setBorder(Border.NO_BORDER);
        customCell.setMarginBottom(5f);
        return customCell;
    }

    public static Cell createTableCell(String text, Color color) {
        final Cell tableCell = new Cell();
        tableCell.add(new Paragraph(text));
        tableCell.setBackgroundColor(color);
        return tableCell;
    }

    public static Cell createHeaderTableCell(String documentIdentifier) {
        final Cell headerTableCell = new Cell();
        final Paragraph identifierParagraph = new Paragraph("DOKUMENT\n" + documentIdentifier.toUpperCase());
        identifierParagraph.setFontSize(13f);
        identifierParagraph.setFixedLeading(14f);
        identifierParagraph.setVerticalAlignment(VerticalAlignment.MIDDLE);
        headerTableCell.add(identifierParagraph);
        headerTableCell.setBorder(Border.NO_BORDER);
        return headerTableCell;
    }

    public static Cell createParagraphCell(String text, float fontSize, Color fontColor) {
        final Cell paragraphCell = new Cell();
        paragraphCell.add(new Paragraph(text).setFontColor(fontColor));
        paragraphCell.setBorder(Border.NO_BORDER);
        paragraphCell.setFontSize(fontSize);
        return paragraphCell;
    }

    public void addParagraphCell(String text) {
        add(createParagraphCell(text, 10f, CustomPdfDocument.PRIMARY_COLOR));
    }

    public void addEntryCell(Map.Entry<String, String> entry) {
        add(createBasicCell(entry.getKey() + ":", 8f, 9f, CustomPdfDocument.SECONDARY_COLOR));
        add(createBasicCell(entry.getValue(), 0, 10f, CustomPdfDocument.PRIMARY_COLOR));
    }

    private Cell createBasicCell(String text, float marginTop, float fontSize, Color color) {
        final Cell cell = new Cell();
        cell.add(new Paragraph(text).setFixedLeading(9f).setMarginTop(marginTop));
        cell.setBorder(Border.NO_BORDER);
        cell.setFontColor(color);
        cell.setFontSize(fontSize);
        return cell;
    }
}
