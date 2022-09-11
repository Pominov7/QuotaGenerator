package org.top.server;

import org.top.communication.Sender;
import org.top.quotagen.PlugGenerator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Класс Сервера - реализует логику сервера
public class Server {
    /**
     * Алгоритм работы сервера:
     * - метод сервера ожидает входящие подключения
     * - при подключении очередного клиента, сервер помещает его в список клиентов
     * - и запускает метод работы с клиентом в отдельном потоке
     * - при отключении клиента он удаляется из списка
     * - [Client Client null null null]
     */

    // поля
    private String ipStr;           // адрес сервера
    private int port;               // порт сервера
    private int limit;              // максимльное кол-во входящих подключений
    private PlugGenerator generator;   // генератор цитат
    private boolean isStarted;      // запущен ли сервер


    private ClientProcessor[] processors; // массив обработчиков клиентов
    ExecutorService threadPool; // пул потоков

    // конструктор с параметрами
    public Server(String ipStr, int port, int limit, PlugGenerator generator) {
        this.ipStr = ipStr;
        this.port = port;
        this.limit = limit + 1;     // одно подключение для спецаильных ответов
        this.generator = generator;

        // создадим обработчики
        threadPool = Executors.newFixedThreadPool(limit);   // пул потоков
        processors = new ClientProcessor[limit];    // не увеличинный лимит
        for (int i = 0; i < processors.length; i++) {
            processors[i] = new ClientProcessor(generator);  // создали пустые обработчики
        }
    }

    // метод работы сервера
    public void run() throws IOException {
        ServerSocket server = null; // сокет сервера

        try {
            System.out.println(getPrefix() + " starting server ...");
            server = new ServerSocket(port, limit, InetAddress.getByName(ipStr));

            // цикл работы сервера: подключать клиентов и запускать потоки на них
            while (true) {
                System.out.println(getPrefix() + " waiting connection ...");
                Socket nextClient = server.accept();    // тут подключился очередной клиент

                // вывести в консоль ip, port подключившегося клиента и
                System.out.println(getPrefix() + " Connected client: " + nextClient.getInetAddress() + ":"
                        + nextClient.getPort());
                // вывести в консоль время подключения клиента
                System.out.println(getPrefix() + " You connection time = " + LocalDateTime.now());
                System.out.println("\n-------------------------------------------------------------\n");

                // получить свободного исполнителя
                ClientProcessor processor = getFreeProcessor();
                if (processor != null) {
                    // если есть свободный, то запустить его
                    processor.prepareClient(nextClient);
                    threadPool.execute(() -> {
                        try {
                            processor.processClient();

                            //если клиент отключился, выводим время отключения
                            if (nextClient.isClosed()) {
                                System.out.println(getPrefix() + nextClient.getInetAddress() + ":"
                                        + nextClient.getPort() + " You disconnection time = " + LocalDateTime.now());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    // если нет свободного обработчика

                    Sender sender = new Sender(nextClient);
                    sender.sendMsg("No available processor, you will be disconnected :c");
                    sender.close();
                    nextClient.close();
                }
            }
        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
        } finally {
            if (server != null && !server.isClosed()) {
                server.close();
            }
        }
    }

    // метод получения свободного исполнителя
    private ClientProcessor getFreeProcessor() {
        for (ClientProcessor processor : processors) {
            if (processor.isFree()) {
                return processor;
            }
        }
        return null;
    }

    // вспомогательный метод префикса сервера
    private String getPrefix() {
        return "server " + ipStr + ":" + port + " > ";
    }

}

