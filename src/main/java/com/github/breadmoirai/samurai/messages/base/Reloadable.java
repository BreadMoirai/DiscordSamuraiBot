package com.github.breadmoirai.samurai.messages.base;

import com.github.breadmoirai.samurai.SamuraiDiscord;

import java.io.Serializable;

public interface Reloadable extends Serializable {
    void reload(SamuraiDiscord samuraiDiscord);
}
