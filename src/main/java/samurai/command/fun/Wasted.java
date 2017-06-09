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
package samurai.command.fun;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.files.SamuraiStore;
import samurai.messages.impl.FileMessage;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.PermissionFailureMessage;
import samurai.util.GifGenerator;
import samurai.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Key("wasted")
@Source
public class Wasted extends Command {
    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES};

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (!context.getSelfMember().hasPermission(context.getChannel(), PERMISSIONS)) {
            return new PermissionFailureMessage(context.getSelfMember(), context.getChannel(), PERMISSIONS);
        }
        final java.util.List<Member> mentions = context.getMentionedMembers();
        final List<String> args = context.getArgs();
        if (mentions.size() != 1) return null;
        String targetUrl = mentions.get(0).getUser().getEffectiveAvatarUrl();
        try {
            boolean spin;
            boolean rave;
            spin = args.contains("spin");
            rave = args.contains("rave");
            boolean grey = args.contains("grey") || args.contains("gs");
            InputStream is = generateImage(new URL(targetUrl), spin, rave, grey);
            if (is == null) return FixedMessage.build("Failure");
            return new FileMessage(context.getChannelId(), is, "_" + mentions.get(0).getEffectiveName() + "_" + (spin ? 'S' : "") + (rave ? 'R' : "") + (grey ? 'G' : "") + ".gif", null);
        } catch (MalformedURLException e) {
            return FixedMessage.build("Invalid Profile Picture. I cannot work with gifs yet");
        }
    }

    private InputStream generateImage(URL url, boolean spin, boolean rave, boolean grey) {
        URLConnection connection;
        BufferedImage avatarBI;
        try {
            connection = url.openConnection();
            connection.addRequestProperty("User-Agent", Bot.info().getUserAgent());
            avatarBI = ImageIO.read(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        BufferedImage wastedBI = SamuraiStore.getImage("wasted.png");
        int width = avatarBI.getWidth();
        int height = avatarBI.getHeight();
        assert wastedBI != null;


        if (!spin && !rave) {
            BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = (Graphics2D) result.getGraphics();
            g.drawImage(ImageUtils.greyScale(avatarBI), 0, 0, null);
            g.drawImage(wastedBI, width / 6, height / 6, width * 2 / 3, height * 2 / 3, null);
            g.dispose();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(result, "png", os);
            } catch (IOException e) {
                return null;
            }
            return new ByteArrayInputStream(os.toByteArray());
        }

        GifGenerator gg = new GifGenerator(avatarBI, wastedBI, String.valueOf((System.currentTimeMillis() - getContext().getMessageId())).substring(10) + ".gif");
        int frames = 60;
        for (String s : getContext().getArgs()) {
            if (s.contains("frame") || s.contains("f:")) {
                String sc = s.substring(s.indexOf(':') + 1);
                try {
                    frames = Integer.parseInt(sc);
                    break;
                } catch (NumberFormatException e) {
                    break;
                }
            }
        }
        gg.generate(frames, spin, rave, grey);
        return gg.getInputStream();
    }

}

