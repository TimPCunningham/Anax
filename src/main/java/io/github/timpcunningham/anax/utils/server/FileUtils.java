package io.github.timpcunningham.anax.utils.server;

import java.io.File;

public class FileUtils {

    public static void deleteFile(File file) {
        if(file.isDirectory()) {
            for(File child : file.listFiles()) {
                deleteFile(child);
            }
        }
        file.delete();
    }
}
