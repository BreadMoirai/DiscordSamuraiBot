package samurai.util;

import samurai.core.data.SamuraiStore;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author TonTL
 * @version 3/7/2017
 */
public class GifGenerator {

    Queue<BufferedImage> imageQueue;
    private BufferedImage base;
    private BufferedImage overlay;
    private String filename;

    public GifGenerator(BufferedImage base, BufferedImage overlay, String filename) {

        this.base = base;
        this.overlay = overlay;
        this.filename = filename;
        imageQueue = new LinkedList<>();
    }

    public File write() {
        ImageOutputStream output;
        GifSequenceWriter writer;
        try {
            File f = SamuraiStore.getTempFile(filename);
            output = new FileImageOutputStream(f);
            writer = new GifSequenceWriter(output, BufferedImage.TYPE_3BYTE_BGR, 63, true);
            while (!imageQueue.isEmpty()) writer.writeToSequence(imageQueue.poll());
            writer.close();
            output.close();
            return f;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void generate(int frameCount, boolean spin, boolean rave, boolean grey) {
        if (grey) {
            base = ImageUtils.greyScale(base);
        }
        //20 fps
        int width = base.getWidth();
        int height = base.getHeight();
        Queue<BufferedImage> queue = new LinkedList<>();
        AffineTransform at = new AffineTransform();
        for (int i = 0; i < frameCount; i++) {
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            if (spin) {
                at.translate(width / 2, height / 2);
                at.rotate(Math.PI / frameCount * 2);
                if (i == 0) {
                    at.scale(1.1, 1.1);
                }
                at.translate(-width / 2, -height / 2);
            }
            Graphics2D g = (Graphics2D) bi.getGraphics();

            if (rave) {
                BufferedImage base2 = ImageUtils.hueShift(base, 360 * i / frameCount);
                g.drawImage(base2, at, null);
            } else g.drawImage(base, at, null);
            g.drawImage(overlay, width / 6, height / 6, width * 2 / 3, height * 2 / 3, null);
            g.dispose();

            queue.add(bi);
        }

        imageQueue = queue;
    }
}
