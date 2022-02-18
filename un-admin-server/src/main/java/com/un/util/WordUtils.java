package com.un.util;

import java.util.stream.Stream;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Service;

@Service
public class WordUtils {
	public XWPFDocument mergeDocuments(Stream<XWPFDocument> documents) {
		XWPFDocument mergedDocuments = new XWPFDocument();
		CTDocument1 mergedCTDocument = mergedDocuments.getDocument();
		mergedCTDocument.unsetBody(); // to remove blank first page in merged document
		documents.forEach(srcDocument -> {
			CTDocument1 srcCTDocument = srcDocument.getDocument();
			if (srcCTDocument != null) {
				CTBody srcCTBody = srcCTDocument.getBody();
				if (srcCTBody != null) {
					CTBody mergedCTBody = mergedCTDocument.addNewBody();
					mergedCTBody.set(srcCTBody);
					XWPFParagraph paragraph = mergedDocuments.createParagraph();
					XWPFRun run = paragraph.createRun();
					run.addBreak(BreakType.PAGE);
				}
			}
		});
		return mergedDocuments;
	}
}
