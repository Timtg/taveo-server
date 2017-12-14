package no.timesaver.service;

import no.timesaver.domain.Receipt;
import no.timesaver.domain.ReceiptProduct;
import no.timesaver.service.user.UserInfoService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class StoreReceiptToPdfService {

    private final static Logger log = LoggerFactory.getLogger(StoreReceiptToPdfService.class);

    private final UserInfoService userInfoService;

    @Autowired
    public StoreReceiptToPdfService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public Optional<byte[]> storeReceiptToPdf(Receipt receipt, Long storeId,String storeName) throws IOException {
        Optional<List<ReceiptProduct>> receiptProductsForStore = receipt.productsForStore(storeId);
        if(!receiptProductsForStore.isPresent()){
            //there is no products from the store in question in this receipt
            return Optional.empty();
        }
        LocalDateTime readyAt = receipt.readyAtForStore(storeId).map(t -> t).orElse(null);
        String userNameForReceipt = userInfoService.getUserNameForReceipt(receipt).map(n ->n).orElse("");
        String fileName = storeName+"_"+receipt.getConfirmationCode()+"_"+receipt.getOrderTime().format(DateTimeFormatter.ofPattern("dd/MM hh:mm"))+".pdf";

        return Optional.ofNullable(getPdfAsBytes(receipt.getConfirmationCode(),receipt.getOrderTime(),userNameForReceipt,receiptProductsForStore.get(),readyAt,fileName,storeName));
    }

    private byte[] getPdfAsBytes(String confirmationCode, LocalDateTime orderTime, String userNameForReceipt, List<ReceiptProduct> receiptProducts, LocalDateTime readyAt,String fileName,String storeName) throws IOException {
        PDDocument doc = new PDDocument();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            PDDocumentInformation docInfo = doc.getDocumentInformation();
            docInfo.setAuthor("Taveo server");
            docInfo.setCreator("Taveo server");
            docInfo.setTitle(fileName.replace("_"," ").replace(".pdf",""));
            docInfo.setCreationDate(Calendar.getInstance());


            int height = 800;
            int width = 320;
            PDRectangle rec = new PDRectangle(width, height);
//            PDRectangle rec = new PDRectangle();
            PDPage page = new PDPage(rec);
            PDFont font = PDType1Font.HELVETICA_BOLD;
            PDPageContentStream contents = new PDPageContentStream(doc, page);
            contents.beginText();
            contents.newLineAtOffset(25, height-60);
            contents.setFont(font, 12);
            contents.setLeading(14.5f);
            contents.showText("Butikk: " + storeName);
            contents.newLine();
            contents.newLine();
            contents.showText("Bestilling: " + confirmationCode);
            contents.newLine();
            contents.newLine();
            contents.showText("Bestilt: " + orderTime.format(DateTimeFormatter.ofPattern("dd/MM-yyyy hh:mm")));
            contents.newLine();
            contents.newLine();
            contents.showText("Bestilt av: " + userNameForReceipt);

            if(readyAt != null) {
                contents.newLine();
                contents.newLine();
                contents.showText("Klar til:" + readyAt.format(DateTimeFormatter.ofPattern("dd/MM hh:mm")));
            }

            contents.newLine();
            contents.newLine();
            contents.newLine();

            contents.showText("Varer/Produkter:");
            contents.newLine();
            contents.endText();



            drawTable(page,contents,((height / 4) * 3) - 30,width/11,getStringMatrixForProducts(receiptProducts));
            contents.close();

            doc.addPage(page);
            doc.save(bos);

            doc.close();
            bos.close();
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("PDFBox error during creation: " + e.getMessage());
            return null;
        }
    }

    private String[][] getStringMatrixForProducts(List<ReceiptProduct> receiptProducts) {
        String [][] matrix = new String[receiptProducts.size()][2];

        for(int i = 0; i < receiptProducts.size();i++){
            String [] row = new String[2];
            ReceiptProduct receiptProduct = receiptProducts.get(i);
            row[0] = receiptProduct.getProduct().getName();
            row[1] = ""+receiptProduct.getCount();
            matrix[i] = row;
        }

        return matrix;
    }

    /**
     * @param page
     * @param contentStream
     * @param y the y-coordinate of the first row
     * @param margin the padding on left and right of table
     * @param content a 2d array containing the table data
     * @throws IOException
     */
    public void drawTable(PDPage page, PDPageContentStream contentStream,
                                 float y, float margin,
                                 String[][] content) throws IOException {
        final int rows = content.length;
        final int cols = content[0].length;
        final float rowHeight = 20f;
        final float tableWidth = page.getMediaBox().getWidth() - margin - margin;
        final float tableHeight = rowHeight * rows;
        final float colWidth = tableWidth/(float)cols;
        final float cellMargin=5f;

        //draw the rows
        float nexty = y ;
        for (int i = 0; i <= rows; i++) {
            contentStream.moveTo(margin, nexty);
            contentStream.lineTo(margin+tableWidth, nexty);
            nexty-= rowHeight;
        }

        //draw the columns
        float nextx = margin;
        for (int i = 0; i <= cols; i++) {
            contentStream.drawLine(nextx, y, nextx, y-tableHeight);
            nextx += colWidth;
        }

        //now add the text
        contentStream.setFont( PDType1Font.HELVETICA_BOLD , 12 );

        float textx = margin+cellMargin;
        float texty = y-15;
        for (String[] aContent : content) {
            for (String text : aContent) {
                contentStream.beginText();
                contentStream.newLineAtOffset(textx, texty);
                contentStream.showText(text);
                contentStream.endText();
                textx += colWidth;
            }
            texty -= rowHeight;
            textx = margin + cellMargin;
        }
    }
}
