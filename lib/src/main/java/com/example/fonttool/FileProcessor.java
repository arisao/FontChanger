package com.example.fonttool;

import java.nio.file.*;
import java.util.concurrent.*;

public class FileProcessor {

    private final String fontName;
    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    public FileProcessor(String fontName) {
        this.fontName = fontName;
    }

    public void processDirectory(Path inputDir, Path outputDir) throws Exception {

        Files.walk(inputDir)
                .filter(p -> p.toString().endsWith(".pptx"))
                .forEach(file -> executor.submit(() -> processFile(file, inputDir, outputDir)));

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    private void processFile(Path file, Path inputDir, Path outputDir) {
        try {
            Path relative = inputDir.relativize(file);
            Path outputFile = outputDir.resolve(relative);

            Files.createDirectories(outputFile.getParent());

            new PptProcessor().process(file, outputFile, fontName);

            System.out.println("[SUCCESS] " + file);

        } catch (Exception e) {
            System.out.println("[ERROR] " + file + " : " + e.getMessage());
        }
    }
}
