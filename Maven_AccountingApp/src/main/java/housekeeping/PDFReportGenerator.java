package housekeeping;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PDFReportGenerator {

    public static void saveToPDF(Transaction t, String path, String formatType) throws Exception {
        saveToPDF(java.util.Collections.singletonList(t), path, new ArrayList<>());
    }

    public static void saveToPDF(List<Transaction> transactions, String path, List<String> imagePaths) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();

        BaseFont baseFont = BaseFont.createFont("C:/Windows/Fonts/malgun.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(baseFont, 18, Font.BOLD);
        Font labelFont = new Font(baseFont, 12, Font.BOLD);
        Font contentFont = new Font(baseFont, 12);

        Transaction representative = transactions.get(0); // 대표 거래 1건

        // 제목
        Paragraph title = new Paragraph("지출결의서", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // 상단 결재란 테이블 (4칸)
        PdfPTable approvalTable = new PdfPTable(4);
        approvalTable.setWidths(new int[]{1, 1, 1, 1});
        approvalTable.setWidthPercentage(30);
        approvalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        for (int i = 0; i < 4; i++) {
            PdfPCell cell = new PdfPCell(new Phrase("", contentFont));
            cell.setFixedHeight(30);
            approvalTable.addCell(cell);
        }
        document.add(approvalTable);

        document.add(Chunk.NEWLINE);

        // 금액 및 계좌 정보 테이블
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidths(new int[]{1, 3, 1, 3});
        infoTable.setWidthPercentage(100);

        double totalAmount = transactions.stream().mapToDouble(Transaction::getAmount).sum();

        infoTable.addCell(new PdfPCell(new Phrase("금  액", labelFont)));
        infoTable.addCell(new PdfPCell(new Phrase(totalAmount + " 원정 (￦)", contentFont)));
        infoTable.addCell(new PdfPCell(new Phrase("입금은행", labelFont)));
        infoTable.addCell(new PdfPCell(new Phrase("신한은행", contentFont)));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        infoTable.addCell(new PdfPCell(new Phrase("발의", labelFont)));
        infoTable.addCell(new PdfPCell(new Phrase(sdf.format(representative.getDate()), contentFont)));
        infoTable.addCell(new PdfPCell(new Phrase("성명 / 처리사항", labelFont)));
        infoTable.addCell(new PdfPCell(new Phrase(representative.getName() + " / " + representative.getNote(), contentFont)));

        infoTable.addCell(new PdfPCell(new Phrase("결재", labelFont)));
        infoTable.addCell(new PdfPCell(new Phrase(sdf.format(representative.getDate()), contentFont)));
        infoTable.addCell(new PdfPCell(new Phrase("계정과목", labelFont)));
        infoTable.addCell(new PdfPCell(new Phrase(representative.getType(), contentFont)));

        infoTable.addCell(new PdfPCell(new Phrase("지출", labelFont)));
        infoTable.addCell(new PdfPCell(new Phrase(sdf.format(representative.getDate()), contentFont)));
        infoTable.addCell(new PdfPCell(new Phrase("단위", labelFont)));
        infoTable.addCell(new PdfPCell(new Phrase("원", contentFont)));

        document.add(infoTable);
        document.add(Chunk.NEWLINE);

        // 세부 내역 테이블
        PdfPTable detailTable = new PdfPTable(3);
        detailTable.setWidths(new int[]{3, 2, 3});
        detailTable.setWidthPercentage(100);

        detailTable.addCell(new PdfPCell(new Phrase("적 요", labelFont)));
        detailTable.addCell(new PdfPCell(new Phrase("금 액", labelFont)));
        detailTable.addCell(new PdfPCell(new Phrase("비 고", labelFont)));

        for (Transaction t : transactions) {
            detailTable.addCell(new PdfPCell(new Phrase(t.getNote(), contentFont)));
            detailTable.addCell(new PdfPCell(new Phrase(t.getAmount() + " 원", contentFont)));
            detailTable.addCell(new PdfPCell(new Phrase("-", contentFont)));
        }

        // 빈 줄 추가
        int extraRows = Math.max(0, 10 - transactions.size());
        for (int i = 0; i < extraRows; i++) {
            detailTable.addCell(new PdfPCell(new Phrase(" ")));
            detailTable.addCell(new PdfPCell(new Phrase(" ")));
            detailTable.addCell(new PdfPCell(new Phrase(" ")));
        }

        // 총계
        PdfPCell totalLabel = new PdfPCell(new Phrase("계", labelFont));
        totalLabel.setColspan(1);
        detailTable.addCell(totalLabel);
        PdfPCell totalValue = new PdfPCell(new Phrase(totalAmount + " 원", contentFont));
        totalValue.setColspan(2);
        detailTable.addCell(totalValue);

        document.add(detailTable);
        document.add(Chunk.NEWLINE);

        // 하단 문구
        Paragraph foot = new Paragraph("위 금액을 지출 결의합니다.", contentFont);
        foot.setSpacingBefore(20);
        document.add(foot);
        
        Paragraph date = new Paragraph(sdf.format(representative.getDate()), contentFont);
        date.setSpacingBefore(10);
        document.add(date);

        Paragraph sign = new Paragraph("영수자: (인)", contentFont);
        sign.setSpacingBefore(20);
        document.add(sign);
        
        if(imagePaths != null && !imagePaths.isEmpty()) {
        	for(String imgPath : imagePaths) {
        		try {
        			document.newPage();
        			document.add(new Paragraph("첨부 이미지", titleFont));
        			document.add(Chunk.NEWLINE);
        			
        			Image img = Image.getInstance(imgPath);
                    img.scaleAbsolute(100, 100);
                    img.setAlignment(Element.ALIGN_CENTER);
                    document.add(img);
        		} catch(Exception e) {
        			 System.err.println("이미지 추가 실패: " + imgPath);
                     e.printStackTrace();
        		}
        	}
        }

       

        document.close();
    }
} 

