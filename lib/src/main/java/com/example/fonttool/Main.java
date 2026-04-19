package com.example.fonttool;

import java.nio.file.*;
import java.util.Properties;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws Exception {

        Properties prop = new Properties();

        // config読み込み
        try (InputStream is = Files.newInputStream(Paths.get("config.properties"))) {
            prop.load(is);
        }

        Path inputDir = Paths.get(prop.getProperty("inputDir"));
        Path outputDir = Paths.get(prop.getProperty("outputDir"));
        String fontName = prop.getProperty("fontName", "Arial");

        System.out.println("Input: " + inputDir);
        System.out.println("Output: " + outputDir);
        System.out.println("Font: " + fontName);

        if (!Files.exists(inputDir)) {
            System.out.println("inputフォルダが存在しません");
            return;
        }

        Files.createDirectories(outputDir);

        FileProcessor processor = new FileProcessor(fontName);
        processor.processDirectory(inputDir, outputDir);

        System.out.println("完了");
    }
}
