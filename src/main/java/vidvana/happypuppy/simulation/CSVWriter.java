package vidvana.happypuppy.simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;




import java.io.*;

public class CSVWriter {
    private BufferedWriter buffWriter;
    private BufferedWriter buffWriter1;
    private BufferedWriter buffWriter2;


    public CSVWriter(String filename, String prefix1, String prefix2, boolean h,  String header) {
        String path1 = "output/" + prefix1 + filename + ".csv";
        String path2 = "output/" + prefix2 + filename + ".csv";
        try {
            FileWriter w1 = new FileWriter(path1);
            FileWriter w2 = new FileWriter(path2);
            buffWriter1 = new BufferedWriter(w1);
            buffWriter2 = new BufferedWriter(w2);
            if (header != null && h) {
                buffWriter1.write(header);
                buffWriter2.write(header);
                buffWriter1.newLine();
                buffWriter2.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public CSVWriter(String filename, String prefix, boolean h,  String header) {
        String path = "output/" + prefix + filename + ".csv";
        try {
            FileWriter w = new FileWriter(path, true);
            buffWriter1 = new BufferedWriter(w);
            if (header != null && h) {
                buffWriter1.write(header);
                buffWriter1.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

/*
    public CSVWriter(String filename, String prefix, String previousPrefix, boolean h, String header) {
        String previousPath = "output/" + previousPrefix + filename + ".csv";
        String path = "output/" + prefix + filename + ".csv";
            try {
                File f = new File(path);
                f.createNewFile();
                Files.copy(Path.of(previousPath), Path.of(path), StandardCopyOption.REPLACE_EXISTING);
                FileWriter w = new FileWriter(path, true);
                buffWriter = new BufferedWriter(w);
                if (header != null && h) {
                    buffWriter.write(header);
                    buffWriter.newLine();
                }
            } catch (IOException x) {
                throw new RuntimeException(x);
            }
    }
*/

    /*
    public void addRecord(String record) {
        try {
            buffWriter.write(record);
            buffWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    public void addRecord(String record) {
        try {
            buffWriter1.write(record);
            buffWriter1.newLine();
            if (buffWriter2 != null) {
                buffWriter2.write(record);
                buffWriter2.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void flush() {
        try {
            buffWriter1.flush();
            if (buffWriter2 != null) {
              buffWriter2.flush();
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
