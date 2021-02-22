import logger.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer implements Runnable{
    private Path base;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private Logger logger;

    public WebServer(int port, int threadNumber, String logFile, String base) throws IOException, IllegalArgumentException {
        serverSocket =  new ServerSocket(port);
        executorService = Executors.newFixedThreadPool(threadNumber);
        logger = new Logger(threadNumber,logFile);
        this.base = Paths.get(base);
        if (!this.base.toFile().exists()) throw new IllegalArgumentException(base + " does not exist");
    }

    @Override
    public void run(){
        //start logging
        executorService.execute(logger);

        //handle requests
        while (true) {
            try {
                executorService.execute((new RequestHandler(serverSocket.accept(), logger, base)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        WebServer server = new WebServer(9999, 10, "log.txt","/users/fengyunlin/Documents/LearnHTML");
        server.executorService.execute(server.logger); //start the logger

        while (true){
            server.executorService.execute(new RequestHandler(server.serverSocket.accept(), server.logger, server.base));
        }
    }
}
