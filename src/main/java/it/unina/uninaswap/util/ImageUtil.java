package it.unina.uninaswap.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public final class ImageUtil {

    // Cartella relativa “logica” in risorse e nel DB 
    public static final String ANNUNCI_DIR_REL = "images/annunci";

    // Prefisso classpath per foto annunci 
    public static final String ANNUNCI_CLASSPATH_PREFIX = "/" + ANNUNCI_DIR_REL + "/";

    // Prefisso classpath per immagini categorie 
    public static final String CATEGORIE_CLASSPATH_PREFIX = "/images/categories/";

    // Directory su filesystem dove copiamo le foto (repo) 
    public static final String ANNUNCI_DIR_PATH = System.getProperty("user.dir") + "/src/main/resources/"
            + ANNUNCI_DIR_REL + "/";

    // Directory su filesystem dove Maven copia le risorse 
    public static final String ANNUNCI_DIR_TARGET_PATH = System.getProperty("user.dir") + "/target/classes/"
            + ANNUNCI_DIR_REL + "/";

    private ImageUtil() {
    }

    // CARICAMENTO BASE
    public static ImageIcon loadFromClasspath(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank())
            return null;

        String p = resourcePath.startsWith("/") ? resourcePath : ("/" + resourcePath);
        URL url = ImageUtil.class.getResource(p);
        if (url == null)
            return null;
        return new ImageIcon(url);
    }

    public static ImageIcon loadFromFile(File file) {
        if (file == null || !file.exists())
            return null;
        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null)
                return null;
            return new ImageIcon(img);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static ImageIcon scaled(ImageIcon icon, int width, int height) {
        if (icon == null)
            return null;
        if (width <= 0 || height <= 0)
            return icon;

        Image img = icon.getImage();
        if (img == null)
            return null;

        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }


    public static ImageIcon loadImageThumbnail(File file, int width, int height) {
        ImageIcon raw = loadFromFile(file);
        return scaled(raw, width, height);
    }

    // DEFAULT PER CATEGORIA (fallback)
    public static String categoryDefaultResourcePath(String categoria) {
        String path;
        if (categoria == null)
            categoria = "";

        switch (categoria) {
            case "Strumenti_musicali" -> path = "/images/categories/strumenti.png";
            case "Libri" -> path = "/images/categories/libri.jpg";
            case "Informatica" -> path = "/images/categories/informatica.jpg";
            case "Abbigliamento" -> path = "/images/categories/abbigliamento.jpg";
            case "Arredo" -> path = "/images/categories/arredo.jpg";
            default -> path = "/images/categories/altro.jpg";
        }
        return path;
    }

    public static ImageIcon defaultForCategoria(String categoria) {
        return loadFromClasspath(categoryDefaultResourcePath(categoria));
    }


    // NORMALIZZAZIONE COSì CHE RITORNI SEMPRE UN TIPO "/images/annunci/<filename>"
    public static String normalizeAnnuncioFotoPath(String raw) {
        if (raw == null)
            return null;
        String p = raw.trim();
        if (p.isEmpty())
            return null;

        // Rimuovi eventuale leading slash (poi lo rimetto io)
        String noLead = p.startsWith("/") ? p.substring(1) : p;

        // vecchia convenzione: "annunci/<file>"
        if (noLead.startsWith("annunci/")) {
            noLead = ANNUNCI_DIR_REL + "/" + noLead.substring("annunci/".length());
        }

        // nuova convenzione corretta
        if (noLead.startsWith(ANNUNCI_DIR_REL + "/")) {
            return "/" + noLead;
        }

        // se arriva solo filename
        return ANNUNCI_CLASSPATH_PREFIX + onlyFilename(noLead);
    }

    private static String onlyFilename(String anyPath) {
        if (anyPath == null)
            return null;
        String p = anyPath;
        int slash = p.lastIndexOf('/');
        return slash >= 0 ? p.substring(slash + 1) : p;
    }


    //CARICA IMMAGINE ANNUNCIO DAL BD
    public static ImageIcon annuncioImageFromDbPath(String dbPathOrFilename) {
        if (dbPathOrFilename == null || dbPathOrFilename.isBlank())
            return null;

        String normalizedClasspath = normalizeAnnuncioFotoPath(dbPathOrFilename);
        if (normalizedClasspath == null)
            return null;

        // 1) classpath
        ImageIcon fromCp = loadFromClasspath(normalizedClasspath);
        if (fromCp != null && fromCp.getIconWidth() > 0)
            return fromCp;

        // 2) filesystem src/main/resources/images/annunci/<filename>
        String filename = onlyFilename(normalizedClasspath);
        File f1 = new File(ANNUNCI_DIR_PATH + filename);
        ImageIcon fromFs1 = loadFromFile(f1);
        if (fromFs1 != null && fromFs1.getIconWidth() > 0)
            return fromFs1;

        // 3) filesystem target/classes/images/annunci/<filename>
        File f2 = new File(ANNUNCI_DIR_TARGET_PATH + filename);
        ImageIcon fromFs2 = loadFromFile(f2);
        if (fromFs2 != null && fromFs2.getIconWidth() > 0)
            return fromFs2;

        return null;
    }




    // COPIA FOTO SIA NELLA CARTELLA DEL PROGETTO E SIA IN TARGET PER PROVARE A VEDERLA A RUNTIME
    public static void copyToAnnunciDirs(File source, String destFilename) throws Exception {
        if (source == null || !source.exists()) {
            throw new IllegalArgumentException("File sorgente non valido");
        }
        if (destFilename == null || destFilename.isBlank()) {
            throw new IllegalArgumentException("Nome file destinazione non valido");
        }

        // src/main/resources/images/annunci
        Files.createDirectories(Path.of(ANNUNCI_DIR_PATH));
        Path dest1 = Path.of(ANNUNCI_DIR_PATH, destFilename);
        Files.copy(source.toPath(), dest1, StandardCopyOption.REPLACE_EXISTING);

        // target/classes/images/annunci
        try {
            Files.createDirectories(Path.of(ANNUNCI_DIR_TARGET_PATH));
            Path dest2 = Path.of(ANNUNCI_DIR_TARGET_PATH, destFilename);
            Files.copy(source.toPath(), dest2, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ignored) {
        }
    }

}
