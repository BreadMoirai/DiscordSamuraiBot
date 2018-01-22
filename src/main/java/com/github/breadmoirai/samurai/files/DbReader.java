/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.github.breadmoirai.samurai.files;

import com.github.breadmoirai.samurai.osu.enums.GameMode;
import com.github.breadmoirai.samurai.osu.model.Score;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * static methods meh.
 * @author TonTL
 * @version 4.x - 2/16/2017
 */
class DbReader {

    private DbReader() {}

    static int nextInt(RandomAccessFile input) throws IOException {
        byte[] bytes = new byte[4];
        input.readFully(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getInt();
    }

    static long nextLong(RandomAccessFile input) throws IOException {
        byte[] bytes = new byte[8];
        input.readFully(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getLong();
    }

    static Score nextScore(BufferedInputStream input) {
        Score score = null;
        try {
            score = new Score()
                    .setMode(GameMode.get(nextByte(input)))
                    .setVersion(nextInt(input))
                    .setBeatmapHash(nextString(input))
                    .setPlayer(nextString(input))
                    .setReplayHash(nextString(input))
                    .setCount300(nextShort(input))
                    .setCount100(nextShort(input))
                    .setCount50(nextShort(input))
                    .setGeki(nextShort(input))
                    .setKatu(nextShort(input))
                    .setCount0(nextShort(input))
                    .setScore(nextInt(input))
                    .setMaxCombo(nextShort(input))
                    .setPerfectCombo(nextByte(input) != 0x00)
                    .setModCombo(nextInt(input));
            nextString(input);
            score.setTimestamp(nextLong(input));
            skip(input, 4);
            score.setOnlineScoreID(nextLong(input));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return score;
    }

    static byte nextByte(BufferedInputStream input) throws IOException {
        byte[] singleByte = new byte[1];
        if (input.read(singleByte) == -1) throw new EOFException("Unexpected End of File");
        return singleByte[0];
    }

    static short nextShort(BufferedInputStream input) throws IOException {
        byte[] bytes = new byte[2];
        if (input.read(bytes) == -1) throw new EOFException("Unexpected End of File");
        ByteBuffer wrapper = ByteBuffer.wrap(bytes);
        wrapper.order(ByteOrder.LITTLE_ENDIAN);
        return wrapper.getShort();
    }

    static int nextULEB128(BufferedInputStream input) throws IOException {
        byte[] bytes = new byte[10];
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] = nextByte(input);
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

    static String nextString(BufferedInputStream input) throws IOException {
        if (nextByte(input) == 0x0b) {
            int stringSize = nextULEB128(input);

            byte[] b = new byte[stringSize];
            if (input.read(b) == -1) throw new EOFException("Unexpected End of File");
            return new String(b, "UTF-8");

        } else {
            return "Not Found";
        }
    }

    static int nextInt(BufferedInputStream input) throws IOException {
        byte[] bytes = new byte[4];
        if (input.read(bytes) == -1) throw new EOFException("Unexpected End of File");
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getInt();
    }

    static long nextLong(BufferedInputStream input) throws IOException {
        byte[] bytes = new byte[8];
        if (input.read(bytes) == -1) throw new EOFException("Unexpected End of File");
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getLong();
    }

    static double nextDouble(BufferedInputStream input) throws IOException {
        byte[] bytes = new byte[8];
        if (input.read(bytes) == -1) throw new EOFException("Unexpected End of File");
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getDouble();
    }

    static float nextSingle(BufferedInputStream input) throws IOException {
        byte[] bytes = new byte[4];
        if (input.read(bytes) == -1) throw new EOFException("Unexpected End of File");
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getFloat();
    }

    static Map<Integer, Double> nextIntDoublePairs(BufferedInputStream input) throws IOException {
        int count = nextInt(input);
        Map<Integer, Double> pairs = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            skip(input, 1);
            int modCombo = nextInt(input);
            skip(input, 1);
            double starRating = nextDouble(input);
            pairs.put(modCombo, starRating);
        }
        return pairs;
    }

    static void skip(BufferedInputStream input, int n) throws IOException {
        if (input.skip(n) != n) throw new EOFException("Unexpected End of File");
    }
}
