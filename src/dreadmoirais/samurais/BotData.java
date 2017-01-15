package dreadmoirais.samurais;

import net.dv8tion.jda.core.entities.Member;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TonTL on 1/15/2017.
 */
public class BotData {

    private static final String BOT_ID = "270044218167132170";//18

    private HashMap<String, userStat> users;

    public BotData(List<Member> memberList) {
        users = new HashMap<>();
        for (Member m : memberList) {
            String userID = m.getUser().getId();
            if (!userID.equals(BOT_ID)) {
                users.put(userID, new userStat(userID));
            }
        }
        initData();
        //printid();
    }

    private void printid() {
        for (String id : users.keySet()) {
            System.out.println(id);
        }
    }

    private void initData() {
        //try(RandomAccessFile raf = new RandomAccessFile("src\\dreadmoirais\\data\\userData.samurai", "rw")) {
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream("src\\dreadmoirais\\data\\userData.samurai"))) {
            byte[] sizebyte = new byte[2];
            bis.read(sizebyte);
            //System.out.println(Hex.encodeHexString(size));
            int size = (sizebyte[0]<<8)|sizebyte[1];
            System.out.println("size: " + size);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
