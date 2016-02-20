package info.rsdev.mysite.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import info.rsdev.mysite.gallery.domain.DefaultImage;

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
                throw new IllegalArgumentException(String.format("DefaultImage file does not exist: ", original));
            }
            int maxWith = 100, maxHeight = 100;
            try {
                BufferedImage img = ImageIO.read(original.toFile());
                Mode mode = img.getWidth() > img.getHeight() ? Mode.FIT_TO_WIDTH : Mode.FIT_TO_HEIGHT;
                BufferedImage thumbImg = Scalr.resize(img, Method.ULTRA_QUALITY, mode, maxWith, maxHeight);
                
                //add transparancy around it to make the image size exactly maxWith x maxHeight pixels
                int width = thumbImg.getWidth();
                int height = thumbImg.getHeight();
                BufferedImage resizedCanvas = null;
                if ((width < maxWith) || (height < maxHeight)) {
                    //center thumbnail on a canvas of exactly maxHeight x maxWidth
                    resizedCanvas = new BufferedImage(maxWith, maxHeight, BufferedImage.TYPE_INT_ARGB);
                    Graphics graphics = resizedCanvas.getGraphics();
                    graphics.drawImage(thumbImg, (maxWith-width)/2, (maxHeight-height)/2, width, height, null);
                    graphics.dispose();
                }
                
                ImageIO.write(resizedCanvas==null?thumbImg:resizedCanvas, "png", thumbnailPath.toFile());
            } catch (IOException e) {
                throw new RuntimeException(String.format("Error while creating thumbnail %s", thumbnailPath));
            }
        }
    }
    
    private static Path getOriginal(Path thumbnailPath) {
        String fileName = thumbnailPath.getFileName().toString();
        if (!DefaultImage.isThumbnail(fileName)) {
            throw new IllegalArgumentException("Not a thumbnail filename pattern: " + fileName);
        }
        
        int uptoIndex = fileName.lastIndexOf(DefaultImage.THUMBNAIL_INDICATOR + DefaultImage.THUMBNAIL_EXTENSION);
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
    
//    public static BufferedImage convertRGBAToIndexed(BufferedImage src) {
//        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
//        Graphics g = dest.getGraphics();
//        g.setColor(new Color(231, 20, 189));
//        g.fillRect(0, 0, dest.getWidth(), dest.getHeight());
//        dest = makeTransparent(dest, 0, 0);
//        dest.createGraphics().drawImage(src, 0, 0, null);
//        return dest;
//    }
//
//    public static BufferedImage makeTransparent(BufferedImage image, int x, int y) {
//        ColorModel cm = image.getColorModel();
//        if (!(cm instanceof IndexColorModel))
//            return image; // sorry...
//        IndexColorModel icm = (IndexColorModel) cm;
//        WritableRaster raster = image.getRaster();
//        int pixel = raster.getSample(x, y, 0);
//        int size = icm.getMapSize();
//        byte[] reds = new byte[size];
//        byte[] greens = new byte[size];
//        byte[] blues = new byte[size];
//        icm.getReds(reds);
//        icm.getGreens(greens);
//        icm.getBlues(blues);
//        IndexColorModel icm2 = new IndexColorModel(8, size, reds, greens, blues, pixel);
//        return new BufferedImage(icm2, raster, image.isAlphaPremultiplied(), null);
//    }
//
//    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
//        int original_width = imgSize.width;
//        int original_height = imgSize.height;
//        int bound_width = boundary.width;
//        int bound_height = boundary.height;
//        int new_width = 0;
//        int new_height = 0;
//
//        if (original_width > original_height) {
//            new_width = bound_width;
//            new_height = (new_width*original_height)/original_width;
//        } else {
//            new_height = bound_height;
//            new_width = (new_height*original_width)/original_height;
//        }
//
//        return new Dimension(new_width, new_height);
//    }
//
//    public static void resizeImage(File original_image, File resized_image, int IMG_SIZE) {
//        try {
//            BufferedImage originalImage = ImageIO.read(original_image);
//
//            String extension = Files.getFileExtension(original_image.getName());
//
//            int type = extension.equals("gif") || (originalImage.getType() == 0) ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
//
//            Dimension new_dim = getScaledDimension(new Dimension(originalImage.getWidth(), originalImage.getHeight()), new Dimension(IMG_SIZE,IMG_SIZE));
//
//            BufferedImage resizedImage = new BufferedImage((int) new_dim.getWidth(), (int) new_dim.getHeight(), type);
//            Graphics2D g = resizedImage.createGraphics();
//            g.drawImage(originalImage, 0, 0, (int) new_dim.getWidth(), (int) new_dim.getHeight(), null);
//            g.dispose();            
//
//            if (!extension.equals("gif")) {
//                ImageIO.write(resizedImage, extension, resized_image);
//            } else {
//                // Gif Transparence workarround
//                ImageIO.write(convertRGBAToIndexed(resizedImage), "gif", resized_image);
//            }
//
//        } catch (IOException e) {
//            Utils.log("resizeImage", e.getMessage());
//        }
//    }
}
