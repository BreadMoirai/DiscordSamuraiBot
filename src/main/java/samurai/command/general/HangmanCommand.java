package samurai.command.general;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.entities.base.SamuraiMessage;
import samurai.entities.dynamic.duel.Hangman;

/**
 * @author TonTL
 * @version 3/16/2017
 */
@Key({"hangman", "hm"})
@Source
public class HangmanCommand extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        return new Hangman(context.getAuthor(), context.getGuild().getPrefix());
    }
}
