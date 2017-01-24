package dreadmoirais.samurais.osu.parse;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class Parser {

    InputStream in;

    Parser() {};

    public Parser(InputStream i) {
        in = i;
    }

    abstract void parse();

    protected byte nextByte() {
        try {
            byte a = (byte) in.read();
            return a;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return 0x00;
    }
    protected String nextString() {
        try {
            byte a = (byte) in.read();
            if (a == 0x0b) {
                int sLength = in.read();
                String s = "";
                if (sLength > 0) {
                    try {
                        byte[] b = new byte[sLength];
                        in.read(b);
                        s = new String(b, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return s;
                }
                else {
                    return "";
                }
            }
            else if (a == 0x00) {
                return "Not Found";
            }
            else return "Error";
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected int nextInt() {
        byte[] a = new byte[4];
        try {
            in.read(a);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //System.out.println(bytesToHex(intbyte));
        return byteToInt(a);
    }

    private static int byteToInt(byte[] r) {
        ByteBuffer wrapped = ByteBuffer.wrap(r);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getInt();
    }

    protected void skip(int n) {
        try {
            in.skip(n);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
