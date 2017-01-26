package dreadmoirais.samurais.osu.parse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

abstract class Parser {

    InputStream in;

    Parser(InputStream i) {
        in = i;
    }

    public abstract Parser parse() throws IOException;

    byte nextByte() {
        try {
            byte a = (byte) in.read();
            return a;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0x00;
    }

    short nextShort() throws IOException {
        byte[] bytes = new byte[2];
        in.read(bytes);
        ByteBuffer wrapper = ByteBuffer.wrap(bytes);
        wrapper.order(ByteOrder.LITTLE_ENDIAN);
        return wrapper.getShort();
    }

    private int nextULEB128() throws IOException {
        byte[] bytes = new byte[10];
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) in.read();
            if ((bytes[i] & 128) != 128) {
                break;
            }
        }
        int uleb = 0;
        for (int j = 0; j <= i; j++) {
            int b = bytes[j];
            b = (b & 127) << 7 * j;
            uleb = b ^ uleb;
        }
        return uleb;
    }

    String nextString() throws IOException {
        if (in.read() == 0x0b) {
            int stringSize = nextULEB128();

            byte[] b = new byte[stringSize];
            in.read(b);
            return new String(b, "UTF-8");

        } else {
            return "Not Found";
        }
    }


    int nextInt() throws IOException {
        byte[] bytes = new byte[4];
        in.read(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getInt();
    }

    long nextLong() throws  IOException {
        byte[] bytes = new byte[8];
        in.read(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getLong();
    }

    double nextDouble() throws IOException {
        byte[] bytes = new byte[8];
        in.read(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getDouble();
    }

    float nextSingle() throws IOException {
        byte[] bytes = new byte[4];
        in.read(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getFloat();
    }

    Map<Integer, Double> nextIntDoublePairs() throws IOException {
        int count = nextInt();
        Map<Integer, Double> pairs = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            skip(1);
            int modCombo = nextInt();
            skip (1);
            double starRating = nextDouble();
            pairs.put(modCombo, starRating);
        }
        return pairs;
    }

    void skip(int n) throws IOException {
        in.skip(n);

    }


}
