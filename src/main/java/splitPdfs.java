import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.utils.PageRange;
import com.itextpdf.kernel.utils.PdfSplitter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class splitPdfs {
    // rename these to your local destination folder and your source pdf path
    public static final String DESTFOLDER = "/Users/jsalido/test/res/";
    public static final String RESOURCE = "/Users/jsalido/test/test2.pdf";

    public static void main(String[] args) throws IOException {
        new splitPdfs().manipulatePdf(DESTFOLDER);
    }

    public void manipulatePdf(final String destFolder) throws IOException {
        final PdfDocument pdfDoc = new PdfDocument(new PdfReader(RESOURCE));

        List<PdfDocument> splitDocuments = new PdfSplitter(pdfDoc) {
            int splitFirstPage = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();
                    PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
                    // Here we open a page and extract its text. You can change which page by changing the
                    // number used in getPage (splitFirstPage has the first page number of current split
                    // add 1 for the 2nd page, or add 2 for the 2nd page
                    parser.processPageContent(pdfDoc.getPage(splitFirstPage));
                    String pageText = strategy.getResultantText();
                    splitFirstPage += 3;
                    // todo: extract some significant data and use it on the file name
                    // Find something useful to use in the filenames, such as the RFC or person's name
                    // and extract it here and use it below to name the PdfWriter
                    String extractedText = "xyz";
                    if (pageText.length() > 400) {
                        extractedText = pageText.substring(265, 270);
                    }

                    return new PdfWriter(destFolder + "_" + splitFirstPage + "_" + extractedText + ".pdf");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException();
                }
            }
        }.splitByPageCount(3);

        for (PdfDocument doc : splitDocuments) {
            doc.close();
        }
        pdfDoc.close();
    }
}