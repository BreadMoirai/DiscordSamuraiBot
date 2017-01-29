package samurai;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * Created by TonTL on 1/27/2017.
 * Writes binary data to file
 */
public class SamuraiFile extends RandomAccessFile {

    public SamuraiFile(String name, String mode) throws FileNotFoundException {
        super(name, mode);
    }

    public SamuraiFile(File file, String mode) throws FileNotFoundException {
        super(file, mode);
    }





}
