package dreadmoirais.samurais;

import net.dv8tion.jda.core.entities.Member;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TonTL on 1/15/2017.
 */
public class BotData {

    private static final String BOT_ID = "270044218167132170";//18

    HashMap<String, UserStat> users;

    public BotData(List<Member> memberList) {
        users = new HashMap<>();
        System.out.println("Initializing BotData");
        for (Member m : memberList) {
            String userID = m.getUser().getId();
            if (!userID.equals(BOT_ID)) {
                users.put(userID, new UserStat(userID, m));
            }
        }
        //printid();
        initData();

    }

    public void addFlame(String id) {
        users.get(id).addFlame();
    }

    private void initData() {
        //try(RandomAccessFile raf = new RandomAccessFile("src\\dreadmoirais\\data\\userData.samurai", "rw")) {
        System.out.println("Parsing userData.samurai");
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream("src\\dreadmoirais\\data\\userData.samurai"))) {
            byte[] sizebyte = new byte[2];
            bis.read(sizebyte);
            //System.out.println(Hex.encodeHexString(size));
            int size = (sizebyte[0]<<8)|sizebyte[1];
            System.out.println("Number of Users: " + size);
            byte[] lastModifiedBytes = new byte[6];
            bis.read(lastModifiedBytes);
            //System.out.println(Hex.encodeHexString(lastModifiedBytes) + "\n" + Long.toHexString(System.currentTimeMillis()));
            long lastModifiedMillis = 0x00;
            for (int i = 0; i < 6; i++) {
                lastModifiedMillis = lastModifiedMillis | (((long)lastModifiedBytes[i]&0xFF) << ((5-i)*8));
            }
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
            Date resultDate = new Date(lastModifiedMillis);
            System.out.println("Last Modified: " + sdf.format(resultDate));
            for (int i = 0; i < size; i++) {
                byte[] userBytes = new byte[8];
                bis.read(userBytes);
                //System.out.println(Hex.encodeHexString(userBytes));
                long userID = 0x00;
                for (int j = 0; j < 8; j++) {
                    //System.out.println(Long.toHexString(userID));
                    userID = userID | (((long)userBytes[j]&0xFF) << ((7-j)*8));
                }
                byte[] userData = new byte[UserStat.BYTE_LENGTH-8];
                bis.read(userData);
                users.get(Long.toString(userID)).setData(userData).setPosition(7+UserStat.BYTE_LENGTH*i);

                //System.out.println(users.containsKey(Long.toString(userID)));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        System.out.println("Saving Data.");
    }

    public void saveDataFull() {
        System.out.println("Writing File.");
        try(DataOutputStream out = new DataOutputStream(new FileOutputStream("src\\dreadmoirais\\data\\userData.samurai"))) {
            //WriteNumberofUsers
            out.writeShort(users.size());
            //writeCurrentTime
            byte[] b = new byte[8];
            long lastModified = System.currentTimeMillis();
            for (int i = 0; i < 8; ++i) {
                b[i] = (byte) (lastModified >> (8 - i - 1 << 3));
            }
            out.write(Arrays.copyOfRange(b, 2, 8));
            for (UserStat u : users.values()) {
                out.write(u.getDataBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
