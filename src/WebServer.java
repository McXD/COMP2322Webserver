import logger.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple implementation of the server side of the HTTP protocol
 * The server can handle
 *      1) GET and HEAD requests
 *      2) If-Modified-Since and Last-Modified headers
 *      3) 200, 400, 404 response code
 * In addition, logging service is provided
 */
public class WebServer implements Runnable{
    private Path base; //base directory of served content
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private Logger logger; //the logger in use

    /**
     * Construct a WebServer instance
     * @param port port number to use
     * @param threadNumber maximum worker threads
     * @param logFile path to logfile
     * @param base base directory of served contents
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public WebServer(int port, int threadNumber, String logFile, String base) throws IOException, IllegalArgumentException {
        this.serverSocket =  new ServerSocket(port);
        this.executorService = Executors.newFixedThreadPool(threadNumber);
        this.logger = new Logger(threadNumber,logFile);
        this.base = Paths.get(base);
        if (!this.base.toFile().exists()) throw new IllegalArgumentException(base + " does not exist"); //error check
    }

    @Override
    public void run(){
        //start the logger
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
        if (args.length != 4){
            System.out.println("Usage: java WebServer <port> <thread_number> <logfile> <base_directory>");
            return;
        }

        WebServer server = new WebServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
                args[2],args[3]);
        server.run();
    }
}
