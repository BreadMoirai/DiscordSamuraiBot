package samurai.messages.base;

import samurai.SamuraiDiscord;

import java.io.Serializable;

public interface Reloadable extends Serializable {
    void reload(SamuraiDiscord samuraiDiscord);
}
