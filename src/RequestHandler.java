import exception.ClientException;
import httpObject.*;
import logger.Logger;
import logger.Record;
import util.time;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * This Class represents the handler for a specific HTTP request
 */
public class RequestHandler implements Runnable{
    private Socket socket;
    private HTTPRequest request;
    private Logger logger;
    private Record record;
    private Path base;
    private File file;

    /**
     * Constructor
     * @param socket socket of the connection on the server side
     * @param logger logger used to log this transaction
     * @param base base directory for searching resources
     */
    public RequestHandler(Socket socket, Logger logger, Path base) {
        this.socket = socket;
        this.logger = logger;
        this.base = base;

        //initialise the record
        this.record = new Record();
        this.record.setAccessTime(LocalDateTime.now());
        this.record.setIpAddress(socket.getInetAddress().toString());
    }

    /**
     * Generate a HTTPRequest from the connection input
     */
    private void getHTTPRequest() throws ClientException, IOException{

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

        int content_length = 0;
        int line_num = 1;

        //read start line and header
        String line;
        StringBuilder sb = new StringBuilder();
        while (!(line = reader.readLine()).equals("")) {
            //capture the content-length field
            try{
                if (line_num > 1){
                    HeaderPair tmp = new HeaderPair(line);
                    if (tmp.getName().toLowerCase().equals("content-length")) content_length = Integer.parseInt(tmp.getValue());
                }
            }catch(ClientException ex){
                //ignore
            }

            sb.append(line).append("\r\n");
            line_num++;
        }

        sb.append("\r\n");

        //read entity
        int length = 0;
        while(length < content_length){
            sb.append((char)reader.read());
            length++;
        }

        this.request = new HTTPRequest(sb.toString());
    }

    /**
     * Handle the GET request
     * @return the HTTPResponse
     * @throws ClientException
     */
    private HTTPResponse get() throws ClientException {
        HTTPResponse response = head();

        //handle entity
        String ims = request.getHeader("if-modified-since");
        if (ims != null && (util.time.convertLocalTime(ims)).isAfter(
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault())
        )){} //do nothing
        else{
            try{
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                response.setEntity(in.readAllBytes());
            } catch(NullPointerException | IOException ex) {
                ex.printStackTrace();
            }
        }

        return response;
    }

    /**
     * Handle the HEAD request
     * @return the HTTPResponse
     * @throws ClientException
     */
    private HTTPResponse head() throws ClientException {
        ResponseStartLine rsl;
        Header header = null;
        byte[] entity = null;

        //Handle startLine

        Path path = Paths.get(request.getPath().substring(1));
        path = base.resolve(path);

        //record it
        this.record.setResourceName(path.toString());

        file = path.toFile();
        if (!file.exists()) throw new ClientException(404, "Not Found", "file " + path.toString() + " does not exist");

        rsl = new ResponseStartLine("HTTP/1.0", 200, "OK");

        //record it
        this.record.setResponseType(rsl.getCode() +"");

        //handle headerString
        String headerString = "server: COMP2322-webserver\r\n" +
                "date: " + util.time.convertString(java.time.LocalDateTime.now()) + "\r\n"+
                "last-modified: " + time.convertLongTime(file.lastModified()) + "\r\n";

        try{
            header = new Header(headerString);
        }catch(ClientException ex){
            //never triggered
            ex.printStackTrace();
        }

        return new HTTPResponse(rsl, header, null);
    }

    private HTTPResponse errorResponse(int code, String description){
        try{
            record.setResponseType(code + "");
            return new HTTPResponse(new ResponseStartLine("HTTP/1.0", code, description),
                    new Header("server: COMP2322-webserver\r\n" +
                            "date: " + util.time.convertString(java.time.LocalDateTime.now()) + "\r\n"),
                    null);
        }catch(ClientException ex){
            //not triggered
            ex.printStackTrace();
        }
        return null;
    }


    @Override
    public void run() {
        HTTPResponse response; //the response message to be sent

        try{
            getHTTPRequest();

            String method = request.getMethod();
            if (method.equalsIgnoreCase("GET")) response = get();
            else if (request.getMethod().equalsIgnoreCase("HEAD")) response = head();

            //error handling
            else throw new ClientException(501, "Not Supported", "Method " + method + " is not supported");

        }catch(ClientException ex){
            response = errorResponse(ex.getCode(), ex.getDescription());
        }catch(IOException ex){
            response = errorResponse(500, "Internal Server Error");
        }

        try{
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

            byte[] tmp = response.toByteArray();
            out.write(tmp,0,tmp.length);
            out.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }

        //close the connection
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //log
        boolean success = false;
        while (!success) {
            success = logger.add(this.record);
        }
    }
}
