package org.top.server;

import org.top.communication.Receiver;
import org.top.communication.Sender;
import org.top.quotagen.PlugGenerator;

import java.io.IOException;
import java.net.Socket;

// Класс-обработчик одного клиента
// Запускается в отдельном потоке и работает с клиентом
public class ClientProcessor {

    // поля
    private boolean isFree; // свободен ли обработчик

    // Метод для того,чтобы распознать свободен ли обработчик
    public boolean isFree() {
        return isFree;
    }

    private Socket remoteClient;
    private PlugGenerator generator;
    int countQuotaClient = 1; // счётчик для подсчета отправленных цитат клиенту
    final int countQuotaMax = 3;

    // конструктор
    public ClientProcessor(PlugGenerator generator) {
        isFree = true;
        remoteClient = null;
        this.generator = generator;
    }

    // подготовка работы с клиентом
    public void prepareClient(Socket socket) throws Exception {
        if (!isFree) {
            throw new Exception("Not free clientProcessor!");
        }
        remoteClient = socket;
        isFree = false;
    }

    // работа с клиентом
    public void processClient() throws IOException {
        Sender sender = null;
        Receiver receiver = null;

        try {
            // объекты для отправки и получения данных
            sender = new Sender(remoteClient);
            receiver = new Receiver(remoteClient);

            // цикл работы с клиентом
            while (true) {
                // TODO: добавить логи работы с клиентом
                // 1. читаем сообщение

                String msg = receiver.receiveMsg();

                // 2. анализируем сообщение
                if (msg.equals("quota") && countQuotaClient <= countQuotaMax) {
                    // то отправить цитату
                    sender.sendMsg(generator.getRandomQuota());
                    System.out.println("Sent " + countQuotaClient++ + " quota for client " + remoteClient.getInetAddress() + ":"
                            + remoteClient.getPort()); //вывод на экран кол-ва полученных цитат для каждого клиента
                } else if (countQuotaClient > countQuotaMax) {
                    sender.sendMsg("The maximum number of quotes for the user has been reached. The session is over.");
                    countQuotaClient = 1;
                    return;
                } else if (msg.equals("exit")) {
                    sender.sendMsg("\nbye");
                    break;
                } else {
                    sender.sendMsg("invalid command");
                }
            }
        } catch (Exception ex) {
            System.out.println("Something wrong during processing client: " + ex.getMessage());
        } finally {
            // по окончанию цикла
            if (sender != null) {
                sender.close();
            }
            if (receiver != null) {
                receiver.close();
            }
            if (remoteClient != null && !remoteClient.isClosed()) {
                remoteClient.close();
            }

            // освободить исполнителя
            remoteClient = null;
            isFree = true;
        }
    }
}
