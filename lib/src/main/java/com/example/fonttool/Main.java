package com.example.fonttool;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {

        try {

            // =========================
            // config.properties 読み込み
            // =========================

            Properties prop = new Properties();

            // jar/exe の場所取得
            Path appDir = Paths.get(
                    Main.class
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI())
                    .getParent();

            // config.properties
            Path configPath = appDir.resolve("config.properties");

            System.out.println("Config: " + configPath);

            if (!Files.exists(configPath)) {

                System.out.println("config.properties が存在しません");

                return;
            }

            try (InputStream is = Files.newInputStream(configPath)) {

                prop.load(is);
            }

            // =========================
            // 設定取得
            // =========================

            Path inputDir = appDir.resolve(
                    prop.getProperty("inputDir", "input"));

            Path outputDir = appDir.resolve(
                    prop.getProperty("outputDir", "output"));

            String fontName = prop.getProperty("fontName", "Meiryo");

            System.out.println("Input : " + inputDir);
            System.out.println("Output: " + outputDir);
            System.out.println("Font  : " + fontName);

            // =========================
            // 入力フォルダ確認
            // =========================

            if (!Files.exists(inputDir)) {

                System.out.println("inputフォルダが存在しません");

                return;
            }

            // output作成
            Files.createDirectories(outputDir);

            // =========================
            // 処理開始
            // =========================

            FileProcessor processor = new FileProcessor(fontName);

            processor.processDirectory(
                    inputDir,
                    outputDir);

            System.out.println("完了");

        } catch (Exception e) {

            e.printStackTrace();

            System.out.println("エラーが発生しました");
        }

        // ダブルクリック実行で閉じないように
        System.out.println("Enterキーで終了");

        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}