package samurai.command.fun;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.dynamic.duel.HangmanGame;

/**
 * @author TonTL
 * @version 3/16/2017
 */
@Key({"hangman", "hm"})
public class Hangman extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        return new HangmanGame(context.getAuthor(), context.getsGuild().getPrefix());
    }
}
