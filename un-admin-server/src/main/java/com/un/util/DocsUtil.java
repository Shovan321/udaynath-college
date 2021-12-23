package com.un.util;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class DocsUtil {
	public static void replaceParagraph(XWPFParagraph p, String pText, String netText){
		List<XWPFRun> runs = p.getRuns();
	    if (runs != null) {
	        for (XWPFRun r : runs) {
	            String text = r.getText(0);
	            if (text != null && text.contains(pText)) {
	                text = text.replace(pText, netText);
	                r.setText(text, 0);
	            }
	        }
	    }
	}
}
