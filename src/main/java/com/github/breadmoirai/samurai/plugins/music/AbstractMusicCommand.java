package com.github.breadmoirai.samurai.plugins.music;

import com.github.breadmoirai.breadbot.framework.command.AbstractCommand;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;

public abstract class AbstractMusicCommand extends AbstractCommand {

    public AbstractMusicCommand() {
        setName(this.getClass().getSimpleName().replace("Command", ""));
        setKeys(getName());
        setGroup("music");
    }

    protected MusicPlugin getPlugin(CommandEvent event) {
        return event.getClient().getPlugin(MusicPlugin.class);
    }
}
