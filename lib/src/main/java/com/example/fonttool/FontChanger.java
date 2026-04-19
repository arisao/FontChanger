package com.example.fonttool;

import org.apache.poi.xslf.usermodel.*;
import java.util.List;

public class FontChanger {

    public static void changeShapes(List<XSLFShape> shapes, String fontName) {

        for (XSLFShape shape : shapes) {

        	if (shape instanceof XSLFTextShape) {
        	    XSLFTextShape textShape = (XSLFTextShape) shape;

        	    textShape.getTextParagraphs().forEach(p ->
        	        p.getTextRuns().forEach(run ->
        	            run.setFontFamily(fontName)
        	        )
        	    );
        	}

        	if (shape instanceof XSLFGroupShape) {
        	    XSLFGroupShape group = (XSLFGroupShape) shape;
        	    changeShapes(group.getShapes(), fontName);
        	}
        }
    }
}
