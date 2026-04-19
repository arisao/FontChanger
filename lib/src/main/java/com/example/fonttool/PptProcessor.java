package com.example.fonttool;

import org.apache.poi.xslf.usermodel.*;
import java.io.*;
import java.nio.file.Path;

public class PptProcessor {

    public void process(Path input, Path output, String fontName) throws Exception {

        try (FileInputStream fis = new FileInputStream(input.toFile());
             XMLSlideShow ppt = new XMLSlideShow(fis)) {

            // スライド
            for (XSLFSlide slide : ppt.getSlides()) {
                FontChanger.changeShapes(slide.getShapes(), fontName);
            }

            // マスター・レイアウト
            for (XSLFSlideMaster master : ppt.getSlideMasters()) {
                FontChanger.changeShapes(master.getShapes(), fontName);

                for (XSLFSlideLayout layout : master.getSlideLayouts()) {
                    FontChanger.changeShapes(layout.getShapes(), fontName);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(output.toFile())) {
                ppt.write(fos);
            }
        }
    }
}
