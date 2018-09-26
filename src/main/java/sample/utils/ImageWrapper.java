package sample.utils;

import javafx.scene.image.Image;

public class ImageWrapper {
    private Image image;
    private String imageName;

    ImageWrapper(Image image, String imageName) {
        this.image = image;
        this.imageName = imageName;
    }

    public Image getImage() {
        return image;
    }

    public String getImageName() {
        return imageName;
    }
}
