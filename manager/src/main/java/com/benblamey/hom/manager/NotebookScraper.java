package com.benblamey.hom.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NotebookScraper {

    private static Logger logger = LoggerFactory.getLogger(NotebookScraper.class);

    public static List<String> getFunctions(String directory) throws IOException, InterruptedException {
        String stdOut = Util.executeShellLogAndBlock(new String[]{
                        "bash",
                        "-ec",
                // Unescaped:
                // grep --extended-regexp --only-matching "^\s*\"\s*def\s*[_a-zA-Z]+\w*\([_a-zA-Z]+\w*\)" *.ipynb | sed -E "s/(.+):\s+\"def (.+)\(/\1,\2/"
                        "grep --extended-regexp --only-matching \"^\\s*\\\"def\\s*[_a-zA-Z]+\\w*\\([_a-zA-Z]+\\w*\\)\" *.ipynb | sed -E \"s/(.+):\\s+\\\"def (.+)\\(/\\1::\\2/\""

                }, new File(directory), null, true).stdOut;
        return Arrays.stream(stdOut.split("\n")).toList();
    }

    // For testing...
    public static void main(String[] args) throws IOException, InterruptedException {
        getFunctions("C:\\Users\\Savior_Hn\\Desktop\\HASTE-o-MATIC-main\\HASTE-o-MATIC-main\\persistentvolume");
    }
}
