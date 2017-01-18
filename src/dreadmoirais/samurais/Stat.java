package dreadmoirais.samurais;

import net.dv8tion.jda.core.entities.MessageEmbed;

/**
 * Created by TonTL on 1/17/2017.
 */
public class Stat {
    String name;
    short value;

    public Stat(String name, short value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Stat{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    public MessageEmbed.Field toField() {
        return new MessageEmbed.Field(name, Double.toString(value), true);
    }

    public byte[] toBytes() {
        byte[] b = new byte[2];
        b[0] = (byte) (value>>8 & 0xFF);
        b[1] = (byte) (value & 0xFF);
        return b;
    }
}
