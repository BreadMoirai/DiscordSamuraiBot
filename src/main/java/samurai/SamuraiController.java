package samurai;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import samurai.data.SamuraiFile;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by TonTL on 1/28/2017.
 * does work
 */
class SamuraiController {

    private static final String AVATAR = "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg";

    private long initializationTime;
    private SamuraiFile samuraiFile;
    private EventListener listener;

    SamuraiController(EventListener eventListener) {
        initializationTime = System.currentTimeMillis();
        samuraiFile = new SamuraiFile();
        listener = eventListener;
    }


    void action(String key, MessageReceivedEvent event, List<User> mentions, String[] args) {
        boolean success = true;
        switch (key) {
            case "status":
                event.getChannel().sendMessage(buildStatus(event, args)).queue();
                break;
            case "reset":
                if (validateAdmin(event))
                    for (Guild guild : event.getJDA().getGuilds())
                        SamuraiFile.writeGuild(guild);
                break;
            case "uptime":
                event.getChannel().sendMessage("Samurai has been online for " + getActiveTime()).queue();
                break;
            case "stat":
                for (Message message : buildUserStats(event, mentions)) {
                    event.getChannel().sendMessage(message).queue();
                }
                break;
            case "prefix":
                if (args == null || args.length != 1 || args[0].length() > 4)
                    event.getMessage().addReaction("❌").queue();
                else {
                    //wait
                    if (SamuraiFile.setPrefix(Long.parseLong(event.getGuild().getId()), args[0])) {
                        listener.updatePrefix(Long.parseLong(event.getGuild().getId()), args[0]);
                        event.getMessage().addReaction("✅").queue();
                    } else {
                        event.getMessage().addReaction("❌").queue();
                    }
                }
                break;
            case "help":
                // wait
                event.getChannel().sendMessage(new SamuraiBuilder(SamuraiFile.getPrefix(Long.parseLong(event.getGuild().getId()))).build()).queue();
                break;
            case "increment":
                if (validateAdmin(event)) {
                    if (mentions == null || args.length < 2) {
                        event.getMessage().addReaction("❌").queue();
                        break;
                    }
                    int value;
                    try {
                        value = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        event.getMessage().addReaction("❌").queue();
                        break;
                    }
                    for (User u : mentions) {
                        SamuraiFile.incrementUserData(Long.parseLong(event.getGuild().getId()), Long.parseLong(u.getId()), value, Arrays.copyOfRange(args, 1, args.length));
                    }
                    event.getMessage().addReaction("✅").queue();
                }
                break;
            case "shutdown":
                event.getJDA().shutdown();
                break;
            default:
                success = false;
        }
        if (success)
            SamuraiFile.incrementUserData(Long.parseLong(event.getGuild().getId()), Long.parseLong(event.getAuthor().getId()), 1, "commands used");
    }

    private boolean validateAdmin(MessageReceivedEvent event) {
        return event.getAuthor().getId().equalsIgnoreCase("232703415048732672");

    }

    private String getActiveTime() {
        long timeDifference = System.currentTimeMillis() - initializationTime;
        int seconds = (int) ((timeDifference / 1000) % 60);
        int minutes = (int) ((timeDifference / 60000) % 60);
        int hours = (int) ((timeDifference / 3600000) % 24);
        int days = (int) (timeDifference / 86400000);
        if (days > 0) {
            return String.format("%d days, %d hours, %d minutes, and %d seconds.", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%d hours, %d minutes, and %d seconds.", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d minutes, and %d seconds.", minutes, seconds);
        } else {
            return String.format("%d seconds.", seconds);
        }
    }

    private Message buildStatus(MessageReceivedEvent event, String[] args) {
        SamuraiBuilder sb = new SamuraiBuilder(event.getJDA());
        return new MessageBuilder().setEmbed(sb.build()).build();
    }

    private List<Message> buildUserStats(MessageReceivedEvent event, List<User> mentions) {
        List<Message> userStats = new LinkedList<>();
        SamuraiBuilder samuraiBuilder = new SamuraiBuilder(event.getMember());
        MessageBuilder messageBuilder = new MessageBuilder();
        if (mentions != null) {
            for (User user : mentions) {
                samuraiBuilder = new SamuraiBuilder(event.getGuild().getMember(user));
                userStats.add(messageBuilder.setEmbed(samuraiBuilder.build()).build());
            }
        } else {
            userStats.add(messageBuilder.setEmbed(samuraiBuilder.build()).build());
        }
        return userStats;
    }


    class SamuraiBuilder extends EmbedBuilder {

        SamuraiBuilder(String prefix) {
            setAuthor("Samurai - Help", null, AVATAR);
            List<String> help = samuraiFile.readHelpFile();
            StringBuilder stringBuilder = new StringBuilder();
            for (String line : help) {
                stringBuilder.append("`").append(prefix).append(line).append("\n");
            }
            setDescription(stringBuilder.toString());
        }

        SamuraiBuilder(JDA jda) {
            setTitle("Status: " + jda.getPresence().getStatus().name());
            setColor(Color.GREEN);
            setFooter("Samurai\u2122", AVATAR);
            addField("Guild Count", String.valueOf(jda.getGuilds().size()), true);
            addField("User Count", String.valueOf(jda.getUsers().size() - 1), true);
            addField("Time Active", getActiveTime(), true);
            addBlankField(true);
        }

        SamuraiBuilder(Member member) {
            setAuthor(member.getEffectiveName(), null, member.getUser().getAvatarUrl());
            setColor(member.getColor());
            // wait update
            for (SamuraiFile.Data field : samuraiFile.getUserData(Long.parseLong(member.getGuild().getId()), Long.parseLong(member.getUser().getId())))
                addField(field.name, String.valueOf(field.value), true);
            setFooter("SamuraiStats\u2122", AVATAR);

        }
    }


}
