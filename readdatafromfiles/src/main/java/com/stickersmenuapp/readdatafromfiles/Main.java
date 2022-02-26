package com.stickersmenuapp.readdatafromfiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public Main() throws IOException {

        main();
    }

    public static void main() throws IOException{
        File dir = new File("src/main/assets");
        File[] files = dir.listFiles();
        // Fetching all the files
        for (File file : files) {
            if(file.isFile()) {
                BufferedReader inputStream = null;
                String line;
                try {
                    inputStream = new BufferedReader(new FileReader(file));
                    while ((line = inputStream.readLine()) != null) {
                        System.out.println(line);
                    }
                }catch(IOException e) {
                    System.out.println(e);
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        }

    }
}