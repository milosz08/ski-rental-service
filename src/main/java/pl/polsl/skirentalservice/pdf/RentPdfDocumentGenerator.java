package pl.polsl.skirentalservice.pdf;

import pl.polsl.skirentalservice.pdf.dto.PdfDocumentData;

public class RentPdfDocumentGenerator extends AbstractPdfGenerator {
    public RentPdfDocumentGenerator() {
        super("wypożyczenia", "Wypożyczone sprzęty:");
    }

    @Override
    protected String setHeaderDate(PdfDocumentData pdfDocumentData) {
        return pdfDocumentData.getIssuedDate();
    }

    @Override
    protected void appendToRightComponents(PdfDocumentData pdfDocumentData) {
        rightComponents.put("Data utworzenia dokumentu", pdfDocumentData.getIssuedDate());
        rightComponents.put("Data wypożyczenia", pdfDocumentData.getRentDate());
        rightComponents.put("Przewidywana data zwrotu", pdfDocumentData.getReturnDate());
        rightComponents.put("Przewidywany czas wypożyczenia", pdfDocumentData.getRentTime());
        rightComponents.put("Wartość podatku VAT", pdfDocumentData.getTax() + "%");
    }
}
