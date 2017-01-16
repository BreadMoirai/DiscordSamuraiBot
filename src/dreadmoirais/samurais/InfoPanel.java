package dreadmoirais.samurais;

import net.dv8tion.jda.core.entities.EmbedType;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TonTL on 1/15/2017.
 */
public class InfoPanel extends MessageEmbedImpl {

    private List<Field> fields;

    public InfoPanel() {
        fields = new ArrayList<>();
        fields.add(new Field("Body", "Body my ass", false));

    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String getTitle() {
        return "IsthisanEmbed?";
    }

    @Override
    public String getDescription() {
        return "what?";
    }

    @Override
    public EmbedType getType() {
        return null;
    }

    @Override
    public Thumbnail getThumbnail() {
        return null;
    }

    @Override
    public Provider getSiteProvider() {
        return null;
    }

    @Override
    public AuthorInfo getAuthor() {
        return new AuthorInfo("Samurai",null,null,null);
    }

    @Override
    public VideoInfo getVideoInfo() {
        return null;
    }

    @Override
    public Footer getFooter() {
        return null;
    }

    @Override
    public ImageInfo getImage() {
        return null;
    }

    @Override
    public List<Field> getFields() {
        return fields;
    }

    @Override
    public Color getColor() {
        return Color.CYAN;
    }

    @Override
    public OffsetDateTime getTimestamp() {
        return null;
    }
}
