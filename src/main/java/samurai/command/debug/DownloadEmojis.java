/*
 *       Copyright 2017 Ton Ly
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
package samurai.command.debug;

import net.dv8tion.jda.core.entities.Emote;
import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Creator
@Key("/dlemote")
public class DownloadEmojis extends Command{
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        for (Emote emote : context.getGuild().getEmotes()) {
            try {
                final URLConnection connection = new URL(emote.getImageUrl()).openConnection();
                connection.setRequestProperty("User-Agent", Bot.info().getUserAgent());
                final InputStream inputStream = connection.getInputStream();
                final BufferedImage image = ImageIO.read(inputStream);
                inputStream.close();
                ImageIO.write(image, "png", new File(emote.getName() + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
