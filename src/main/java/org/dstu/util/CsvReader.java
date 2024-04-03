package org.dstu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {
    public static List<String[]> readCsvFile (String fileName, String regex) {
        List<String[]> strings = new ArrayList<>();
        File file = new File(fileName);
        try {
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file), "windows-1251");
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            String fileLine;
            while ((fileLine = bufferedReader.readLine()) != null) {
                System.out.println(fileLine);
                String[] splitString = fileLine.split(regex);
                strings.add(splitString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }
}
