package info.rsdev.mysite.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import info.rsdev.mysite.gallery.domain.Image;

public abstract class ThumbnailCreator {

    private ThumbnailCreator() {}
    
    /**
     * Create the thumbnail belonging to the given thumbnail resource if it does not yet exist. The original image that the 
     * thumbnail is supposed to be taken of, is assumed to be present in the same directory.
     * 
     * @param thumbnailPath the {@link Path} to the thumbnail resource
     */
    public static void make(Path thumbnailPath) {
        make(getOriginal(thumbnailPath), thumbnailPath);
    }
    
    /**
     * Create the thumbnail belonging to the given thumbnail resource based on the original image file
     * 
     * @param original the image that is represented by the thumbnail resource
     * @param thumbnailPath the {@link Path} to the thumbnail resource
     */
    public static void make(Path original, Path thumbnailPath) {
        if (Files.notExists(thumbnailPath)) {
            if (Files.notExists(original)) {
                throw new IllegalArgumentException(String.format("Image file does not exist: ", original));
            }
            try {
                BufferedImage img = ImageIO.read(original.toFile());
                BufferedImage thumbImg = Scalr.resize(img, Method.ULTRA_QUALITY, Mode.FIT_TO_WIDTH, 75, 100);
                ImageIO.write(thumbImg, "png", thumbnailPath.toFile());
            } catch (IOException e) {
                throw new RuntimeException(String.format("Error while creating thumbnail %s", thumbnailPath));
            }
        }
    }
    
    private static Path getOriginal(Path thumbnailPath) {
        String fileName = thumbnailPath.getFileName().toString();
        if (!Image.isThumbnail(fileName)) {
            throw new IllegalArgumentException("Not a thumbnail filename pattern: " + fileName);
        }
        
        int uptoIndex = fileName.lastIndexOf(Image.THUMBNAIL_INDICATOR + Image.THUMBNAIL_EXTENSION);
        fileName = fileName.substring(0, uptoIndex);
        File parentDir = thumbnailPath.getParent().toFile();
        File[] candidates = parentDir.isDirectory()?parentDir.listFiles(new OriginalMatcher(fileName)):new File[]{};
        if (candidates == null) {
            throw new IllegalStateException(String.format("Error while searching for original of %s", thumbnailPath));
        } else if (candidates.length == 0) {
            throw new IllegalArgumentException(String.format("No original found for thumbnail: %s", thumbnailPath));
        } else if (candidates.length > 1) {
            throw new IllegalStateException(String.format("Ambiguous original file for thumnail %s: %s", thumbnailPath, candidates));
        }
        return candidates[0].toPath();
    }
    
    private static class OriginalMatcher extends ImageFileFilter {
        
        private final String startOfFilename;
        
        private OriginalMatcher(String startOfFilename) {
            this.startOfFilename = startOfFilename + ".";
        }

        @Override
        public boolean accept(File pathName) {
            boolean isImageFile = super.accept(pathName);
            return isImageFile && pathName.getName().startsWith(startOfFilename);
        }
    }
    
}
