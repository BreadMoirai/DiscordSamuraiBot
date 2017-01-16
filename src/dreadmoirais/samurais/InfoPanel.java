package dreadmoirais.samurais;

import net.dv8tion.jda.core.entities.EmbedType;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by TonTL on 1/15/2017.
 */
public class InfoPanel extends MessageEmbedImpl {

    private static final String avatarUrl = "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg";


    public InfoPanel(UserStat stat) {
        super();
        this.setAuthor(new AuthorInfo(stat.Member.getEffectiveName(),null,stat.Member.getUser().getAvatarUrl(),null));
        this.setTitle(null);
        this.setColor(stat.Member.getColor());
        //this.setThumbnail(new Thumbnail(stat.Member.getUser().getAvatarUrl(), null, 100, 100));
        this.setType(EmbedType.RICH);
        this.setFooter(new Footer("SamuraiStats™",avatarUrl,null));
        this.setFields(generateFields(stat));


    }

    private List<Field> generateFields(UserStat stat) {
        List<Field> fields = new LinkedList<>();
        fields.add(new Field("Times Flamed", Short.toString(stat.timesFlamed), true));

        return fields;

    }


}
