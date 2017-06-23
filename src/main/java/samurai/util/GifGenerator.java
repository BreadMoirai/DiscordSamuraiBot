/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package samurai.util;

import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    public ByteArrayInputStream getInputStream() {
        ByteArrayOutputStream output;
        GifSequenceWriter writer;
        try {

            output = new ByteArrayOutputStream();
            ImageOutputStream ios = new MemoryCacheImageOutputStream(output);
            writer = new GifSequenceWriter(ios, BufferedImage.TYPE_3BYTE_BGR, 63, true);
            while (!imageQueue.isEmpty()) writer.writeToSequence(imageQueue.poll());
            writer.close();
            output.close();
            return new ByteArrayInputStream(output.toByteArray());
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
                at.translate(width >> 1, height >> 1);
                at.rotate(Math.PI / frameCount * 2);
                if (i == 0) {
                    at.scale(1.1, 1.1);
                }
                at.translate(-width / 2.0, -height / 2.0);
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
