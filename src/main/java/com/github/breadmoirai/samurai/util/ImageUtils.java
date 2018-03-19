/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.github.breadmoirai.samurai.util;

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
