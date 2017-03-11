package samurai.util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author TonTL
 * @version 3/7/2017
 */
public class ImageUtils {

    /**
     * turns a Buffered Image greyscale
     *
     * @param image base image
     * @return a new BufferedImage with TYPE_BYTE_GRAY
     */
    public static BufferedImage greyScale(BufferedImage image) {
        BufferedImage gs = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = (Graphics2D) gs.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return gs;
    }

    /**
     * changes the hue of image. hue 0-360
     *
     * @param image base image
     * @param hue   hue change 0-360
     * @return a new hue-shifted image
     */
    public static BufferedImage hueShift(BufferedImage image, int hue) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        float ihue = hue / 360.0f;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int RGB = image.getRGB(x, y);
                int R = (RGB >> 16) & 0xff;
                int G = (RGB >> 8) & 0xff;
                int B = (RGB) & 0xff;
                float HSV[] = new float[3];
                Color.RGBtoHSB(R, G, B, HSV);
                result.setRGB(x, y, Color.getHSBColor(ihue, HSV[1], HSV[2]).getRGB());
            }
        }
        return result;
    }
}
