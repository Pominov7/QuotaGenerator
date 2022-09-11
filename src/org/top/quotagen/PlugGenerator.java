package org.top.quotagen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// Класс генератор цитат
public class PlugGenerator implements IGenerator {

    public String getRandomQuota() {
        List<String> quotaList = new ArrayList<>();                // создается новый список

        // 15-я строка для теста через telnet
//        try (FileReader reader = new FileReader("src/EngQuotaList.txt")) { // читаем из файла

        try (FileReader reader = new FileReader("src/RusQuotaList.txt")) { // читаем из файла
            BufferedReader buffer = new BufferedReader(reader);
            String text;
            LinkedList<String> tempQuoteList = new LinkedList<>(); // временный список

            while ((text = buffer.readLine()) != null) {
                //разделяем строку на подстроки, используя разделитель ;
                final LinkedList<String> endList = new LinkedList<>(Arrays.stream(text.split("[;]"))
                        .map(String::trim) //Метод trim() — удаляет пробелы в начале и конце строки, и возвращает строку
                        .filter(s -> s.length() > 0) // если длина больше 0
                        .toList()); // возвращаем список
                tempQuoteList.addAll(endList); // добавляет все элементы из данной коллекции в конец списка
            }
            quotaList = tempQuoteList.stream().toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //получаем случайную цитату из заданного диапазона
        Random random = new Random();
        return quotaList.get(random.nextInt(quotaList.size() - 1));
    }
}
