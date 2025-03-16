package pl.polsl.skirentalservice.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.IOException;

public class CustomPdfDocument extends Document {
    public static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(0, 0, 0);
    public static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(113, 128, 150);
    private static final String FONT_PATH = "./font/Cambria-Font.ttf";

    public CustomPdfDocument(PdfDocument pdfDoc) throws IOException {
        super(pdfDoc);
        setFont(PdfFontFactory.createFont(FONT_PATH, PdfEncodings.CP1250));
        setFontSize(9f);
    }

    void addHeader(String text) {
        final Paragraph headerParagraph = new Paragraph(text);
        headerParagraph.setFontSize(12f);
        headerParagraph.setMarginBottom(10f);
        add(headerParagraph);
    }

    void addHorizontalDivider() {
        final CustomPdfTable divider = new CustomPdfTable(1);
        divider.setMargins(15f, 0, 15f, 0);
        divider.setBorder(new SolidBorder(SECONDARY_COLOR, .2f));
        add(divider);
    }

    void addPricesDivider() {
        final Table dividerPrice = new CustomPdfTable(new float[]{150f});
        dividerPrice.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        dividerPrice.setBorder(new SolidBorder(SECONDARY_COLOR, .2f));
        dividerPrice.setMargins(8f, 0, 8f, 0);
        add(dividerPrice);
    }

    void addDetailsDescription(String text) {
        final Paragraph descriptionHeader = createParagraph("Dodatkowe uwagi:", 9f, SECONDARY_COLOR);
        final Paragraph description = createParagraph(text == null ? "brak" : text, 10f, PRIMARY_COLOR);
        add(descriptionHeader);
        add(description);
    }

    void addPriceUnitBruttoParagraph(String price) {
        add(createPriceUnitParagraph(price, 9f, false, PRIMARY_COLOR));
    }

    void addPriceUnitNettoParagraph(String price) {
        add(createPriceUnitParagraph(price, 9f, false, SECONDARY_COLOR));
    }

    void addSummaryPriceUnitBruttoParagraph(String price) {
        add(createPriceUnitParagraph(price, 12f, true, PRIMARY_COLOR));
    }

    void addSummaryPriceUnitNettoParagraph(String price) {
        add(createPriceUnitParagraph(price, 12f, true, SECONDARY_COLOR));
    }

    private Paragraph createPriceUnitParagraph(String price, float fontSize, boolean isBold, Color fontColor) {
        final Paragraph paragraph = createParagraph(price + " zł", fontSize, fontColor);
        paragraph.setTextAlignment(TextAlignment.RIGHT);
        if (isBold) {
            paragraph.setBold();
        }
        return paragraph;
    }

    private Paragraph createParagraph(String text, float fontSize, Color fontColor) {
        final Paragraph paragraph = new Paragraph(text);
        paragraph.setFixedLeading(5f);
        paragraph.setFontSize(fontSize);
        paragraph.setFontColor(fontColor);
        return paragraph;
    }
}
