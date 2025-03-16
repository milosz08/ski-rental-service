package pl.polsl.skirentalservice.pdf;

import pl.polsl.skirentalservice.pdf.dto.PdfDocumentData;
import pl.polsl.skirentalservice.util.DateUtils;

public class ReturnPdfDocumentGenerator extends AbstractPdfGenerator {
    public ReturnPdfDocumentGenerator() {
        super("zwrotu", "Zwrócone sprzęty:");
    }

    @Override
    protected String setHeaderDate(PdfDocumentData pdfDocumentData) {
        return pdfDocumentData.getReturnDate();
    }

    @Override
    protected void appendToRightComponents(PdfDocumentData pdfDocumentData) {
        rightComponents.put("Data wypożyczenia", DateUtils.toISO8601Format(pdfDocumentData.getRentDate()));
        rightComponents.put("Data zwrotu", pdfDocumentData.getReturnDate());
        rightComponents.put("Czas wypożyczenia", pdfDocumentData.getRentTime());
        rightComponents.put("Wartość podatku VAT", pdfDocumentData.getTax() + "%");
    }
}
