package dreadmoirais.samurais;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by TonTL on 1/14/2017.
 * Does Stuff.
 */
public class BotListener extends ListenerAdapter {

    private static final String BOT_ID = "270044218167132170";//18

    private static User self;

    private static HashMap<String, String> roleResponses;
    private static Random rand;
    private static boolean active;
    private static BotData data;
    private static ArrayList<String> fightWords;
    private static ArrayList<String> shadePhrases;

    public BotListener() { //constructor
        active = true;

        roleResponses = new HashMap<>(); //hashmap for responses to who am i?
        fightWords = new ArrayList<>(); //list of words that will trigger a duel
        shadePhrases = new ArrayList<>(); //What shade will the tree provide when you can have Samurai[Bot]



        //Random object for rolls
        rand = new Random();
    }

    @Override
    public void onReady(ReadyEvent event) {
        loadKeywords(); //loads keywords from file
        JDA jda = event.getJDA();
        //jda.getGuilds();
        self = jda.getSelfUser();
        System.out.println(jda.getSelfUser().getAvatarUrl() + "\n");
        data = new BotData(jda.getGuilds().get(0).getMembers());

    }



    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //when a message is sent in the channel

        JDA jda = event.getJDA(); //JDA, the core of the api.

        //Event specific information
        User author = event.getAuthor(); //The Member that sent the message
        Message message = event.getMessage();  //The message that was received.
        MessageChannel channel = event.getChannel(); //This is the MessageChannel that the message was sent to.
        //  This could be a TextChannel, PrivateChannel, or Group!
        String msg = message.getContent();  //This returns a human readable version of the Message. Similar to
        // what you would see in the client.
        if (!event.isFromType(ChannelType.TEXT)) //If this message was sent to a Guild TextChannel
        {
            channel.sendMessage("Boi why you dm me? Get the fuck outta here.");
        } else {
            if (active) {
                if (hasMention(event)) {
                    flame(event);
                    //supports flame

                } else if (filetransfer(event)) {
                    //supports file

                } else if (simpleResponse(event)) {
                    //!stat
                    //who am i?
                    //!roll
                } else if (exitProtocol(event)) {
                    //!shutdown
                    //!kys
                }
            }
        }
    }

    private boolean hasMention(MessageReceivedEvent event) {
        if (event.getMessage().getMentionedUsers().contains(self)) {
            return true;
        }
        else return false;
    }



    private boolean filetransfer(MessageReceivedEvent event) {
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        if (attachments.size()>0) {
            System.out.println("\nFound Attachment.");
            for (Message.Attachment a : attachments) {
                event.getChannel().sendMessage(String.format("Attachment Found.\n%s :%s bytes\n%s", a.getFileName(), a.getSize(), event.getMessage().getMentionedUsers().get(0).getName())).queue();
            }
            return true;
        }

        return false;
    }

    private boolean flame(MessageReceivedEvent event) {//checks for @samurai
        List<User> mentions = event.getMessage().getMentionedUsers();
        if (mentions.contains(event.getJDA().getSelfUser())) {
            if (event.getMessage().getRawContent().toLowerCase().contains("flame") && mentions.size() == 2) {
                int killNumber = rand.nextInt(shadePhrases.size());
                String killPhrase = shadePhrases.get(killNumber);
                User target = mentions.get((1-mentions.indexOf(event.getJDA().getSelfUser())));
                if (killNumber == 2) {//"[Member] more like stupid[(us)er]
                    killPhrase += target.getName().substring(target.getName().length()/2);
                }
                killPhrase = String.format(killPhrase, target.getAsMention());
                event.getChannel().sendMessage(killPhrase).queue();
                data.addFlame(target.getId());
            }
        }
        return false;
    }

    private boolean simpleResponse(MessageReceivedEvent event) {//basic responses
        String messageReceived = event.getMessage().getRawContent().toLowerCase();
        String messageSent = "";
        if(messageReceived.contains("!stat")) {
            getStat(event);
        } else if (messageReceived.equals("who am i?")) {//who am i? response
            messageSent = " " + roleResponses.get(event.getMember().getRoles().toString());
        } else if (messageReceived.contains("!roll")) {//!roll response
            if (messageReceived.length() > 6) {
                try {
                    int x = Integer.parseInt(messageReceived.substring(6));
                    messageSent = " rolled " + (rand.nextInt(x) + 1) + "!";
                } catch (NumberFormatException e) {
                    messageSent = " what?";
                }
            } else {
                messageSent = " rolled " + (rand.nextInt(100) + 1) + "!";
            }
        }
        if (messageSent.length() > 0) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + messageSent).queue();
            return true;
        }
        return false;
    }

    private List<MessageEmbed.Field> makeFieldList() {
        List<MessageEmbed.Field> fields = new ArrayList<MessageEmbed.Field>();
        fields.add(new MessageEmbed.Field("BodyA", "Lorem Ipsum a la mode", false));
        fields.add(new MessageEmbed.Field("BodyB", "Body like it's drop", false));
        return fields;
    }

    private void getStat(MessageReceivedEvent event) {
        if (event.getMessage().getMentionedUsers().size()==0) {
            event.getChannel().sendMessage(new InfoPanel(data.users.get(event.getAuthor().getId()))).queue();
        } else {
            for (User u: event.getMessage().getMentionedUsers()) {
                event.getChannel().sendMessage(new InfoPanel(data.users.get(u.getId()))).queue();
            }
        }
    }

    private boolean exitProtocol(MessageReceivedEvent event) {
        boolean shutdown = false;
        if (event.getMessage().getRawContent().equalsIgnoreCase("!kys")) {
            event.getChannel().sendMessage("ok :(").queue();
            shutdown = true;
        } else if (event.getMessage().getRawContent().equalsIgnoreCase("!shutdown")) {
            event.getChannel().sendMessage("sad boop").queue();
            shutdown = true;
        }
        if (shutdown) {
            //save data?
            data.saveDataFull();
            event.getJDA().shutdown();
        }
        return false;
    }


    private boolean duel(MessageReceivedEvent event) {
        String message = event.getMessage().getRawContent();
        List<User> mentions = event.getMessage().getMentionedUsers();
        if (event.getMessage().getRawContent() == null)
        return false;
        return false;
    }

    private void loadKeywords() {
        try(BufferedReader br = new BufferedReader(new FileReader("src\\dreadmoirais\\data\\keywords.txt"))) {
            String line = br.readLine();
            System.out.println("Reading " + line);
            while (!(line=br.readLine()).equals("")) {
                roleResponses.put(line, br.readLine());
            }
            line = br.readLine();
            System.out.println("Reading " + line);
            while(!(line=br.readLine()).equals("")) {
                fightWords.add(line);
            }
            line = br.readLine();
            System.out.println("Reading " + line);
            while((line=br.readLine()) != null) {
                shadePhrases.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
