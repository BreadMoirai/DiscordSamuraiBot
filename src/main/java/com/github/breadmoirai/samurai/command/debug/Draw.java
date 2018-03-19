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
package com.github.breadmoirai.samurai.command.debug;

import com.github.breadmoirai.samurai.command.Command;
import com.github.breadmoirai.samurai.command.CommandContext;
import com.github.breadmoirai.samurai.command.annotations.Key;
import com.github.breadmoirai.samurai.command.annotations.Source;
import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;
import com.github.breadmoirai.samurai.messages.impl.FileMessage;
import com.github.breadmoirai.samurai.messages.impl.FixedMessage;
import com.github.breadmoirai.samurai.messages.impl.PermissionFailureMessage;
import com.github.breadmoirai.samurai.messages.impl.black_jack.Card;
import com.github.breadmoirai.samurai.messages.impl.black_jack.CardFactory;
import net.dv8tion.jda.core.Permission;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Key("draw")
@Source
public class Draw extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        if (!context.getSelfMember().hasPermission(context.getChannel(), Permission.MESSAGE_ATTACH_FILES)) {
            return new PermissionFailureMessage(context.getSelfMember(), context.getChannel(), Permission.MESSAGE_ATTACH_FILES);
        }
        final List<String> args = context.getArgs();
        if (args.size() == 0) return null;
        if (args.get(0).equals("stack")) {
            List<Card> cards = new ArrayList<>(args.size() - 1);
            args.stream().skip(1).forEach(s -> {
                try {
                    cards.add(Card.parseCard(s));
                } catch (ParseException ignored) {
                    System.out.println("Parse Error:" + s);
                }
            });
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(CardFactory.drawCardStack(cards), "png", os);
            } catch (IOException e) {
                return FixedMessage.build("Failure to draw Stack.");
            }
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            return new FileMessage(context.getChannelId(), is, cards.stream().map(Card::toString).collect(Collectors.joining()) + ".png", null);
        }
        try {
            Card c;
            c = Card.parseCard(args.get(0));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(CardFactory.drawCard(c), "jpg", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            return new FileMessage(context.getChannelId(), is, c.toString() + ".png", null);
        } catch (ParseException ignored) {
        } catch (IOException e) {
            return FixedMessage.build("Failed to save and upload drawing");
        }
        try {
            return new FileMessage(context.getChannelId(), textToImage(args.get(0)), args.get(0) + ".png", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream textToImage(String text) throws IOException {
        int WIDTH = 100;
        int HEIGHT = 20;
        BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.drawString(text, 8, 13);
        g.dispose();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", os);
        return new ByteArrayInputStream(os.toByteArray());
    }
}
