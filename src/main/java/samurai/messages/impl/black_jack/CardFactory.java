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
package samurai.messages.impl.black_jack;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * @author TonTL
 * @version 3/8/2017
 */
public class CardFactory {

    private static final Random scatter = new Random();

    private static final int CARD_WIDTH = 100, CARD_HEIGHT = 150;
    private static final int CARD_BORDER = 5;
    private static final float SYMBOL_OFFSET = 0.1f;
    private static final int FONT_SIZE = 20;
    private static final int STACK_OFFSET_Y = 38, STACK_OFFSET_X = 25;
    private static final int SCATTER_STRENGTH = 5;

    public static BufferedImage drawCard(Card c) {
        BufferedImage b = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) b.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRoundRect(CARD_BORDER, CARD_BORDER, CARD_WIDTH - 2 * CARD_BORDER, CARD_HEIGHT - 2 * CARD_BORDER, CARD_BORDER * 2, CARD_BORDER * 2);
        g.setColor(Color.lightGray);
        g.drawRoundRect(CARD_BORDER, CARD_BORDER, CARD_WIDTH - 2 * CARD_BORDER, CARD_HEIGHT - 2 * CARD_BORDER, CARD_BORDER * 2, CARD_BORDER * 2);
        g.setFont(new Font(Font.DIALOG, Font.PLAIN, FONT_SIZE));
        switch (c.suit()) {
            case SPADES:
            case CLUBS:
                g.setColor(Color.BLACK);
                break;
            case DIAMONDS:
            case HEARTS:
                g.setColor(Color.RED);
                break;
            default:
                g.setColor(Color.GREEN);
        }
        int xpos = (int) (CARD_WIDTH * SYMBOL_OFFSET);
        int ypos = (int) (CARD_HEIGHT * SYMBOL_OFFSET + 10);
        if (c.getSymbolValue().length() == 1) {
            if (c.isFace())
                g.drawString(c.getSymbolValue(), xpos + 2, ypos);
            else g.drawString(c.getSymbolValue(), xpos + 3, ypos);
        } else {
            g.drawString(c.getSymbolValue(), xpos - 3, ypos);
        }
        ypos += FONT_SIZE;
        g.drawString(c.getSymbolSuit(), xpos, ypos);
        ypos -= FONT_SIZE;
        g.rotate(Math.PI, CARD_WIDTH / 2, CARD_HEIGHT / 2);
        if (c.getSymbolValue().length() == 1) {
            g.drawString(c.getSymbolValue(), xpos + 2, ypos);
        } else {
            g.drawString(c.getSymbolValue(), xpos - 3, ypos);
        }
        ypos += FONT_SIZE;
        g.drawString(c.getSymbolSuit(), xpos, ypos);
        g.dispose();
        return b;
    }

    public static BufferedImage drawCardStack(List<Card> cards) {
        if (cards.size() == 0) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
        } else if (cards.size() == 1) {
            return drawCard(cards.get(0));
        }
        BufferedImage b = new BufferedImage(CARD_WIDTH + STACK_OFFSET_X * (cards.size() - 1), CARD_HEIGHT + STACK_OFFSET_Y * (cards.size() - 1), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) b.getGraphics();
        AffineTransform at0 = new AffineTransform();
        at0.translate(0, b.getHeight() - CARD_HEIGHT);
        AffineTransform atn = new AffineTransform();
        atn.translate(STACK_OFFSET_X, -STACK_OFFSET_Y);
        AffineTransform atx = new AffineTransform();
        atx.preConcatenate(at0);
        //double rotation = scatter.nextDouble() * (Math.PI / 6) - Math.PI / 12;
        for (Card c : cards) {
            BufferedImage bc = drawCard(c);
            AffineTransform atf = (AffineTransform) atx.clone();
            atx.translate(CARD_WIDTH / 2 + scatter.nextInt(SCATTER_STRENGTH), CARD_HEIGHT / 2 + scatter.nextInt(SCATTER_STRENGTH));
            atx.rotate(scatter.nextDouble() * (Math.PI / 6) - Math.PI / 12);
            atx.translate(-CARD_WIDTH / 2 - scatter.nextInt(SCATTER_STRENGTH * 2), -CARD_HEIGHT / 2 - scatter.nextInt(SCATTER_STRENGTH * 2));
            g.drawImage(bc, atf, null);

            atx.concatenate(atn);
        }
        g.dispose();
        return b;
    }


}
