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
package com.github.breadmoirai.samurai.files;

import net.dv8tion.jda.core.entities.Message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


/**
 * @author TonTL
 * @version 4.5 - 2/20/2017
 */
public class SamuraiStore {
    public static final int VERSION = 20170103;

    public static String downloadFile(Message.Attachment attachment) {
        String path = String.format("%s/%s.db", SamuraiStore.class.getResource("temp").getPath(), attachment.getId());
        attachment.download(new File(path));
        return path;
    }

    public static String getHelp(String fileName) {
        StringBuilder sb = new StringBuilder();
        final InputStream fileInput = SamuraiStore.class.getResourceAsStream("help/" + fileName + ".txt");
        if (fileInput == null)
            return String.format("Nothing found for `%s`. Sorry!", fileName);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fileInput, StandardCharsets.UTF_8))) {
            br.lines().map(s -> s + '\n').forEachOrdered(sb::append);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static File saveToFile(BufferedImage img, String filename) throws IOException {
        File file = getTempFile(filename);
        ImageIO.write(img, "jpg", file);
        return file;
    }

    private static File getTempFile(String filename) {
        return new File(SamuraiStore.class.getResource("temp").getPath() + '/' + filename);
    }

    public static BufferedImage getImage(String s) {
        try {
            return ImageIO.read(SamuraiStore.class.getResource("images/" + s));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getModuleInfo(String filename) {
        final InputStream fileInput = SamuraiStore.class.getResourceAsStream("module/" + filename + ".txt");
        if (fileInput == null) {
            return "No info found";
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fileInput, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
