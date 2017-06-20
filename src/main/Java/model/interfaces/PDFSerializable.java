package model.interfaces;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import window.ViewContext;

import java.io.IOException;

/**
 * Created by Souverain73 on 20.06.2017.
 */
public interface PDFSerializable {
    void SerializeToPDF(PdfWriter writer, ViewContext vc) throws DocumentException, IOException;
}
