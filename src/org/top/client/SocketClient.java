package org.top.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

// Класс - клиент
public class SocketClient {

    public static void main(String[] args) {

        Socket client = null;  // сокет клиента
        BufferedReader input = null; // поток чтения

        try {
            // работа
            // создаем сокет клиента + подключение к серверу
            client = new Socket("25.62.60.169", 1024);
            System.out.println("Client created and connected to remote server");

            // поток чтения сообщения с сервера
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            // поток записи на сервер
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));


            // читаем сообщение от сервера
            input = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );

            while (true) {
                System.out.println("Enter a message for the server:");
                String msgClient = reader.readLine(); // client пишет в консоль
                out.write(msgClient + "\n"); // отправляем сообщение на сервер
                out.flush(); // выбрасывает сообщение из буфера в поток записи

                String msgServer = input.readLine(); // ожидание ответа от сервера
                if (msgServer.equalsIgnoreCase
                        ("The maximum number of quotes for the user has been reached. The session is over.")
                ) {
                    System.out.println("Server ask: " + msgServer);
                    break;
                } else {
                    System.out.println("Server ask: " + msgServer); // получив сообщение - выводим на экран
                }
                if (msgServer.equalsIgnoreCase("exit") ||
                        msgClient.equalsIgnoreCase("exit")) {
                    System.out.println("The session is over.");
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Something wrong: " + ex.getMessage());
        } finally {
            try {
                if (client != null && !client.isClosed()) {
                    client.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (Exception ex) {
                System.out.println("Something wrong: " + ex.getMessage());
            }
        }
    }
}
