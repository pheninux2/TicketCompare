package pheninux.xdev.ticketcompare.config;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour Tesseract OCR
 */
@Configuration
@Slf4j
public class TesseractConfig {

    /**
     * Initialise et configure Tesseract pour l'OCR
     */
    @Bean
    public Tesseract tesseract() {
        Tesseract instance = new Tesseract();

        // Configuration de base
        instance.setLanguage("fra");  // Français
        instance.setOcrEngineMode(1); // Tesseract only
        instance.setPageSegMode(3);   // Automatic page segmentation

        // 1. Vérifier d'abord la variable d'environnement TESSDATA_PREFIX (Docker)
        String tessdataPrefix = System.getenv("TESSDATA_PREFIX");
        if (tessdataPrefix != null && !tessdataPrefix.isEmpty()) {
            java.io.File dataDir = new java.io.File(tessdataPrefix);
            if (dataDir.exists()) {
                instance.setDatapath(tessdataPrefix);
                log.info("✅ Tesseract data found via TESSDATA_PREFIX: {}", tessdataPrefix);
                verifyLanguageFiles(tessdataPrefix);
                return instance;
            } else {
                log.warn("⚠️ TESSDATA_PREFIX set but directory not found: {}", tessdataPrefix);
            }
        }

        // 2. Détecter le système d'exploitation et tenter les chemins standards
        String os = System.getProperty("os.name").toLowerCase();
        log.info("Detecting OS: {}", os);

        try {
            if (os.contains("win")) {
                // Windows - tente plusieurs chemins possibles
                String[] possiblePaths = {
                    "C:\\Program Files\\Tesseract-OCR\\tessdata",
                    "C:\\Program Files (x86)\\Tesseract-OCR\\tessdata",
                    System.getenv("TESSERACT_HOME") + "\\tessdata"
                };

                for (String path : possiblePaths) {
                    if (path != null && trySetDataPath(instance, path)) {
                        return instance;
                    }
                }
                log.warn("⚠️ Tesseract data not found in Windows paths");

            } else if (os.contains("mac")) {
                // macOS
                String[] possiblePaths = {
                    "/usr/local/share/tessdata",
                    "/opt/homebrew/share/tessdata"
                };

                for (String path : possiblePaths) {
                    if (trySetDataPath(instance, path)) {
                        return instance;
                    }
                }
                log.warn("⚠️ Tesseract data not found in macOS paths");

            } else {
                // Linux / Docker
                String[] possiblePaths = {
                    "/usr/share/tessdata",                      // Alpine Docker
                    "/usr/share/tesseract-ocr/5.00/tessdata",  // Ubuntu/Debian 22.04+
                    "/usr/share/tesseract-ocr/4.00/tessdata",  // Ubuntu/Debian 20.04
                    "/usr/share/tesseract-ocr/tessdata"        // Fallback
                };

                for (String path : possiblePaths) {
                    if (trySetDataPath(instance, path)) {
                        return instance;
                    }
                }
                log.warn("⚠️ Tesseract data not found in Linux paths");
            }

        } catch (Exception e) {
            log.error("❌ Error configuring Tesseract data path", e);
        }

        log.warn("⚠️ Tesseract configured without datapath - OCR may not work!");
        return instance;
    }

    /**
     * Tente de configurer le datapath et vérifie qu'il existe
     */
    private boolean trySetDataPath(Tesseract instance, String path) {
        java.io.File dataDir = new java.io.File(path);
        if (dataDir.exists() && dataDir.isDirectory()) {
            instance.setDatapath(path);
            log.info("✅ Tesseract data found at: {}", path);
            verifyLanguageFiles(path);
            return true;
        }
        return false;
    }

    /**
     * Vérifie que les fichiers de langue existent
     */
    private void verifyLanguageFiles(String datapath) {
        String[] languages = {"fra", "eng"};
        for (String lang : languages) {
            java.io.File langFile = new java.io.File(datapath, lang + ".traineddata");
            if (langFile.exists()) {
                log.info("  ✓ Language file found: {}.traineddata", lang);
            } else {
                log.warn("  ✗ Language file NOT found: {}.traineddata", lang);
            }
        }
    }
}

