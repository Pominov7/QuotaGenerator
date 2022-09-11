package org.top;

import org.top.quotagen.PlugGenerator;
import org.top.server.Server;

import java.io.IOException;

public class MainApp {

    public static void main(String[] args) throws IOException {
        Server server = new Server("25.62.60.169", 1024, 10, new PlugGenerator());
        server.run();

    }
}
