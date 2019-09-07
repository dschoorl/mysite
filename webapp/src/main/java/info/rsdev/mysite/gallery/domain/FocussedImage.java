package info.rsdev.mysite.gallery.domain;

public class FocussedImage implements Image {
    
    private final Image image;
    
    private final int imageNumber;

    public FocussedImage(Image image, int imageNumber) {
        this.image = image;
        this.imageNumber = imageNumber;
    }

    @Override
    public String getPath() {
        return this.image.getPath();
    }

    @Override
    public String getThumbnailPath() {
        return this.image.getThumbnailPath();
    }
    
    public int getImageNumber() {
        return this.imageNumber;
    }
}
