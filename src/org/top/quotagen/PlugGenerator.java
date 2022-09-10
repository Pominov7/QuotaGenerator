package org.top.quotagen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PlugGenerator implements IGenerator {

    public String getRandomQuota() {
        List<String> quotaList = new ArrayList<>();                // создается новый список цитат
//        try (FileReader reader = new FileReader("src/quotaList.txt")) { // читаем из файла
            try (FileReader reader = new FileReader("src/RusquotaList.txt")) { // читаем из файла
            BufferedReader buffer = new BufferedReader(reader);
            String text;
            LinkedList<String> ll = new LinkedList<>();
            while ((text = buffer.readLine()) != null) {

                //lets to clean up the sentence of spaces by left (spaces by right are clearing during splitting)
                final LinkedList<String> lll = new LinkedList<>(Arrays.stream(text.split("[;]"))
                        .map(String::trim)
                        .filter(s -> s.length() > 0)
                        .toList());
                ll.addAll(lll);
            }
            quotaList = ll.stream().toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random random = new Random();
        return quotaList.get(random.nextInt(quotaList.size() - 1));
    }
}
