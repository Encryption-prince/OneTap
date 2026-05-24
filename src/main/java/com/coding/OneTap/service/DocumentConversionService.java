package com.coding.OneTap.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Converts DOCX, PPTX, and XLSX files to PDF using LibreOffice headless mode.
 * LibreOffice must be installed in the runtime environment (see Dockerfile).
 */
@Service
public class DocumentConversionService {

    private static final Logger log = Logger.getLogger(DocumentConversionService.class.getName());

    // Extensions that should be converted to PDF before storage
    private static final Set<String> CONVERTIBLE_EXTENSIONS = Set.of(
            "docx", "doc", "pptx", "ppt", "xlsx", "xls", "odt", "odp", "ods"
    );

    private static final Set<String> CONVERTIBLE_MIME_TYPES = Set.of(
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",   // docx
            "application/msword",                                                          // doc
            "application/vnd.openxmlformats-officedocument.presentationml.presentation", // pptx
            "application/vnd.ms-powerpoint",                                              // ppt
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",         // xlsx
            "application/vnd.ms-excel",                                                   // xls
            "application/vnd.oasis.opendocument.text",                                   // odt
            "application/vnd.oasis.opendocument.presentation",                           // odp
            "application/vnd.oasis.opendocument.spreadsheet"                             // ods
    );

    /**
     * Returns true if this file should be converted to PDF.
     */
    public boolean shouldConvert(String filename, String mimeType) {
        if (filename != null) {
            String ext = getExtension(filename).toLowerCase();
            if (CONVERTIBLE_EXTENSIONS.contains(ext)) return true;
        }
        if (mimeType != null && CONVERTIBLE_MIME_TYPES.contains(mimeType.toLowerCase())) {
            return true;
        }
        return false;
    }

    /**
     * Converts the given file bytes to PDF using LibreOffice headless.
     * Returns the PDF bytes, or throws if conversion fails.
     *
     * @param fileBytes    raw bytes of the source document
     * @param originalName original filename (used to determine extension)
     * @return PDF bytes
     */
    public byte[] convertToPdf(byte[] fileBytes, String originalName) throws IOException, InterruptedException {
        Path tempDir = Files.createTempDirectory("onetap_conv_");
        try {
            // Write input file to temp dir
            String ext = getExtension(originalName);
            Path inputFile = tempDir.resolve("input." + (ext.isEmpty() ? "docx" : ext));
            Files.write(inputFile, fileBytes);

            // Run LibreOffice headless conversion
            ProcessBuilder pb = new ProcessBuilder(
                    "soffice",
                    "--headless",
                    "--norestore",
                    "--nofirststartwizard",
                    "--convert-to", "pdf",
                    "--outdir", tempDir.toString(),
                    inputFile.toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Capture output for logging
            String output = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                log.warning("LibreOffice conversion failed (exit " + exitCode + "): " + output);
                throw new IOException("Document conversion failed. LibreOffice exit code: " + exitCode);
            }

            // Find the output PDF (LibreOffice names it input.pdf)
            Path outputPdf = tempDir.resolve("input.pdf");
            if (!Files.exists(outputPdf)) {
                // Sometimes LibreOffice uses the original stem
                outputPdf = Files.list(tempDir)
                        .filter(p -> p.toString().endsWith(".pdf"))
                        .findFirst()
                        .orElseThrow(() -> new IOException("Converted PDF not found in output directory"));
            }

            return Files.readAllBytes(outputPdf);

        } finally {
            // Clean up temp directory
            deleteDirectory(tempDir.toFile());
        }
    }

    /**
     * Returns the PDF filename for a given original filename.
     * e.g. "report.docx" → "report.pdf"
     */
    public String toPdfFilename(String originalName) {
        if (originalName == null) return "document.pdf";
        int dot = originalName.lastIndexOf('.');
        String stem = dot >= 0 ? originalName.substring(0, dot) : originalName;
        return stem + ".pdf";
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1) : "";
    }

    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) deleteDirectory(f);
            }
        }
        dir.delete();
    }
}
