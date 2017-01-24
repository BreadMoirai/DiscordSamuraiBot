package dreadmoirais.samurais;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by TonTL on 1/15/2017.
 * holds data for Bot
 *
 */
public class BotData {

    private static final String BOT_ID = "270044218167132170";//18

    HashMap<String, UserData> users;

    public BotData(List<Member> memberList) {
        users = new HashMap<>();
        System.out.println("Initializing BotData");
        for (Member m : memberList) {
            String userID = m.getUser().getId();
            if (!userID.equals(BOT_ID)) {
                users.put(userID, new UserData(userID, m));
            }
        }
        initData();

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void initData() {

        System.out.println("Parsing userData.samurai");
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream("src\\dreadmoirais\\data\\userData.samurai"))) {
            //Number of users
            byte[] shortBytes = new byte[2];
            bis.read(shortBytes);
            int size = (shortBytes[0]<<8)|shortBytes[1];
            System.out.println("Number of Users: " + size);

            //get last modified Date
            byte[] lastModifiedBytes = new byte[6];
            bis.read(lastModifiedBytes);
            long lastModifiedMillis = 0x00;
            for (int i = 0; i < 6; i++) {
                lastModifiedMillis = lastModifiedMillis | (((long)lastModifiedBytes[i]&0xFF) << ((5-i)*8));
            }
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
            Date resultDate = new Date(lastModifiedMillis);
            System.out.println("Last Modified: " + sdf.format(resultDate));

            //read user data
            for (int i = 0; i < size; i++) {
                //user Id
                byte[] longBytes = new byte[8];
                bis.read(longBytes);
                long userID = 0x00;
                for (int j = 0; j < 8; j++) {
                    userID = userID | (((long)longBytes[j]&0xFF) << ((7-j)*8));
                }

                //stat data
                byte[] userData = new byte[UserData.BYTE_LENGTH-8];
                bis.read(userData);

                //file position
                users.get(Long.toString(userID)).setData(userData).setPosition(7+ UserData.BYTE_LENGTH*i);

                //System.out.println(users.containsKey(Long.toString(userID)) ? "FOUND" : "NOT FOUND");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void incrementStat(String id, String statName) {
        users.get(id).incrementStat(statName);
    }

    public void saveData() {
        try(RandomAccessFile raf = new RandomAccessFile("src\\dreadmoirais\\data\\userData.samurai", "rw")) {


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void saveDataFull() {
        System.out.println("Writing File.");
        try(DataOutputStream out = new DataOutputStream(new FileOutputStream("src\\dreadmoirais\\data\\userData.samurai"))) {
            //WriteNumberOfUsers
            out.writeShort(users.size());
            //writeCurrentTime
            byte[] b = new byte[8];
            long lastModified = System.currentTimeMillis();
            for (int i = 0; i < 8; ++i) {
                b[i] = (byte) (lastModified >> (8 - i - 1 << 3));
            }
            out.write(Arrays.copyOfRange(b, 2, 8));


            for (UserData u : users.values()) {
                for (byte[] bytes : u.getDataBytes()) {
                    out.write(bytes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class UserData {

        static final int BYTE_LENGTH = 14;

        private String userID;
        private String EffectiveName, AvatarUrl;
        private Color color;

        private int position;

        private List<Stat> stats;

        UserData(String id, Member member) {
            userID = id;
            EffectiveName = member.getEffectiveName();
            AvatarUrl = member.getUser().getAvatarUrl();
            color = member.getColor();
            stats = new LinkedList<>();
            stats.add(new Stat("Times Flamed"));
            stats.add(new Stat("Duels Won"));
            stats.add(new Stat("Duels Fought"));
        }

        UserData setData(byte[] data) {
            for (int i = 0; i < data.length; i+=2) {
                byte[] shortBytes = Arrays.copyOfRange(data, i, i + 2);
                stats.get(i/2).value = (short) (((shortBytes[0] & 0xFF) << 8) | (shortBytes[1] & 0xFF));
            }
            return this;
        }

        MessageEmbed buildEmbed() {
            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor(EffectiveName, AvatarUrl, null)
                    .setColor(color)
                    .setFooter("SamuraiStats\u2122", "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg");
            for (Stat stat : stats) {
                eb.addField(stat.toField());
            }
            return eb.build();
        }

        void setPosition(int p) {
            position = p;
        }

        //public void writeData(DataOutputStream os) throws IOException {}

        public void incrementStat(String statName) {
            for (Stat stat : stats) {
                if (statName.equals(stat.name)) {
                    stat.value += 1;
                    if (!stat.changed) {
                        stat.changed = true;
                    }
                }
            }
        }

        List<byte[]> getDataBytes() {
            ArrayList<byte[]> output = new ArrayList<>();
            byte[] idBytes = new byte[8];
            //write userID
            long l = Long.parseLong(userID);
            for (int i = 7; i >= 0; i--) {
                idBytes[i] = (byte)(l & 0xFF);
                l >>= 8;
            }
            output.add(idBytes);
            for (Stat stat : stats) {
                output.add(stat.toBytes());

            }
            return output;
        }

    }

    public class Stat {
        String name;
        short value;
        boolean changed;

        public Stat(String name, short value) {
            this.name = name;
            this.value = value;
            changed = false;
        }

        public Stat(String name) {
            this(name, (short)0);
        }

        @Override
        public String toString() {
            return "Stat{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    '}';
        }

        MessageEmbed.Field toField() {
            return new MessageEmbed.Field(name, Short.toString(value), true);
        }

        byte[] toBytes() {
            byte[] b = new byte[2];
            b[0] = (byte) (value>>8 & 0xFF);
            b[1] = (byte) (value & 0xFF);
            return b;
        }
    }

}
