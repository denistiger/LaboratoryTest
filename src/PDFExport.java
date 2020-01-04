import com.itextpdf.html2pdf.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFExport {
    static boolean exportPDF(String htmlString, File pdfDest) {
        ConverterProperties converterProperties = new ConverterProperties();
        try {
            HtmlConverter.convertToPdf(htmlString,
                    new FileOutputStream(pdfDest), converterProperties);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
