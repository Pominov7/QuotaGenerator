package org.top.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

// Клиент
public class SocketClient {

    public static void main(String[] args) {

        Socket client = null;  // сокет клиента
        BufferedReader input = null; // поток чтения
        ; // поток записи
        ; //
        try {
            // работа
            // 1. создаем сокет клиента + подключение к серверу
            client = new Socket("25.62.60.169", 1024);
            System.out.println("Client created and connected to remote server");

            // поток чтения сообщения с сервера
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));


            // 2. Чтение сообщения от сервера
            input = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );


            while (true) {
                System.out.println("print what something to pass to server:");
                String msgClient = reader.readLine(); // ждём пока клиент что-нибудь напишет в консоль
                out.write(msgClient + "\n"); // отправляем сообщение на сервер
                out.flush();

                String msgServer = input.readLine(); // ждём, что скажет сервер
                System.out.println("Server ask: \n" + msgServer); // получив - выводим на экран

                if (msgServer.equalsIgnoreCase("exit") ||
                        msgClient.equalsIgnoreCase("exit")) {
                    System.out.println("let`s end communication ;)");
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
                System.out.println("Something wrong in finally: " + ex.getMessage());
            }
        }
    }
}
