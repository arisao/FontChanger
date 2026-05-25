package com.example.fonttool;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileProcessor {

    private final String fontName;

    public FileProcessor(String fontName) {
        this.fontName = fontName;
    }

    public void processDirectory(Path inputDir, Path outputDir) throws Exception {

        Files.walk(inputDir)
                .filter(path -> path.toString().toLowerCase().endsWith(".pptx"))
                .forEach(path -> {

                    try {

                        Path relative = inputDir.relativize(path);

                        Path outputPath = outputDir.resolve(relative);

                        Files.createDirectories(outputPath.getParent());

                        processPptx(path, outputPath);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
    }

    private void processPptx(Path inputPptx, Path outputPptx) throws Exception {

        System.out.println("処理開始: " + inputPptx);

        // 作業用temp
        Path tempDir = Files.createTempDirectory("pptx_edit");

        try {

            unzip(inputPptx, tempDir);

            // XML編集
            processXmlFiles(tempDir);

            // zip戻す
            zip(tempDir, outputPptx);

            System.out.println("完了: " + outputPptx);

        } finally {

            deleteDirectory(tempDir);

        }
    }

    private void processXmlFiles(Path tempDir) throws Exception {

        Files.walk(tempDir)
                .filter(path -> {

                    String p = path.toString().replace("\\", "/");

                    return p.endsWith(".xml")
                            && (p.contains("/ppt/slides/")
                                    || p.contains("/ppt/theme/")
                                    || p.contains("/ppt/slideMasters/"));
                })
                .forEach(path -> {

                    try {

                        String xml = Files.readString(path);

                        xml = replaceTypeface(xml, fontName);

                        Files.writeString(path, xml);

                        System.out.println("XML修正: " + path);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
    }

    /**
     * typeface属性だけ安全変更
     */
    private String replaceTypeface(String xml, String fontName) {

        xml = xml.replaceAll(
                "<a:latin([^>]*)typeface=\"[^\"]*\"",
                "<a:latin$1typeface=\"" + fontName + "\"");

        xml = xml.replaceAll(
                "<a:ea([^>]*)typeface=\"[^\"]*\"",
                "<a:ea$1typeface=\"" + fontName + "\"");

        xml = xml.replaceAll(
                "<a:cs([^>]*)typeface=\"[^\"]*\"",
                "<a:cs$1typeface=\"" + fontName + "\"");

        xml = xml.replaceAll(
                "<a:sym([^>]*)typeface=\"[^\"]*\"",
                "<a:sym$1typeface=\"" + fontName + "\"");

        return xml;
    }

    private void unzip(Path zipFile, Path targetDir) throws Exception {

        try (
                InputStream fis = Files.newInputStream(zipFile);
                ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                Path filePath = targetDir.resolve(entry.getName());

                if (entry.isDirectory()) {

                    Files.createDirectories(filePath);

                } else {

                    Files.createDirectories(filePath.getParent());

                    Files.copy(zis, filePath,
                            StandardCopyOption.REPLACE_EXISTING);
                }

                zis.closeEntry();
            }
        }
    }

    private void zip(Path sourceDir, Path outputZip) throws Exception {

        try (
                OutputStream fos = Files.newOutputStream(outputZip);
                ZipOutputStream zos = new ZipOutputStream(fos)) {

            Files.walk(sourceDir)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {

                        try {

                            String entryName = sourceDir.relativize(path)
                                    .toString()
                                    .replace("\\", "/");

                            ZipEntry zipEntry = new ZipEntry(entryName);

                            zos.putNextEntry(zipEntry);

                            Files.copy(path, zos);

                            zos.closeEntry();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
        }
    }

    private void deleteDirectory(Path path) throws Exception {

        Files.walkFileTree(path,
                new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult visitFile(
                            Path file,
                            BasicFileAttributes attrs)
                            throws IOException {

                        Files.delete(file);

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(
                            Path dir,
                            IOException exc)
                            throws IOException {

                        Files.delete(dir);

                        return FileVisitResult.CONTINUE;
                    }
                });
    }
}