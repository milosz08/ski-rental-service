package pl.polsl.skirentalservice.pdf;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.util.Map;

public class CustomPdfTable extends Table {
    private static final DeviceRgb TABLE_HEADER_BGC = new DeviceRgb(221, 221, 221);

    public CustomPdfTable(float[] percentageColumnWidths) {
        super(UnitValue.createPercentArray(percentageColumnWidths));
        useAllAvailableWidth();
    }

    public CustomPdfTable(int numColumns) {
        super(numColumns);
        useAllAvailableWidth();
    }

    public CustomPdfTable(int numColumns, float marginBottom) {
        super(numColumns);
        useAllAvailableWidth();
        setMarginBottom(marginBottom);
    }

    void addHeader(String[] cells) {
        for (final String cell : cells) {
            addCell(CustomPdfCell.createTableCell(cell, TABLE_HEADER_BGC));
        }
    }

    void addDataCell(String textContent) {
        final Cell dataTableCell = new Cell();
        dataTableCell.add(new Paragraph(textContent).setFixedLeading(9f));
        dataTableCell.setFontSize(9f);
        addCell(dataTableCell);
    }

    void addInlineKeyValueData(String header, String content) {
        addCell(CustomPdfCell.createParagraphCell(header + ":", 9f, CustomPdfDocument.SECONDARY_COLOR));
        addCell(CustomPdfCell.createParagraphCell(content, 9f, CustomPdfDocument.PRIMARY_COLOR));
    }

    void appendTableToTable(CustomPdfTable table) {
        final Cell tableCell = new Cell();
        tableCell.add(table);
        tableCell.setBorder(Border.NO_BORDER);
        addCell(tableCell);
    }

    void appendColumnCells(String headerText, Map<String, String> components) {
        final CustomPdfCell side = CustomPdfCell.createRootCell();
        side.addParagraphCell(headerText);
        for (final Map.Entry<String, String> component : components.entrySet()) {
            side.addEntryCell(component);
        }
        addCell(side);
    }
}
