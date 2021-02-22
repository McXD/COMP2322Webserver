import exception.ClientException;
import httpObject.*;
import logger.Logger;
import logger.Record;
import util.time;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class RequestHandler implements Runnable{
    private Socket socket;
    private HTTPRequest request;
    private Logger logger;
    private Record record;
    private Path base;

    public RequestHandler(Socket socket, Logger logger, Path base) {
        this.socket = socket;
        this.record = new Record();
        this.logger = logger;
        this.record.setAccessTime(LocalDateTime.now());
        this.record.setIpAddress(socket.getInetAddress().toString().substring(1));
        this.base = base;
    }

    /*
    Generate a HTTPRequest from the connection input
     */
    private void getHTTPRequest() throws ClientException, IOException{

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String line;
        StringBuilder sb = new StringBuilder();
        while (!(line = reader.readLine()).equals("")) {
            sb.append(line).append("\r\n");
        }

        sb.append("\r\n");

        //read the entity
        //!buggy(no indication of end of request)
        int length = 0;
        while(length < 400 && reader.ready()){
            sb.append(reader.readLine() + "\r\n");
        }

        this.request = new HTTPRequest(sb.toString());
    }

    private HTTPResponse get() throws ClientException {
        ResponseStartLine rsl;
        Header header = null;
        File file;
        byte[] entity = null;

        //Handle startLine

        Path path = Paths.get(request.getUrl().substring(1));
        System.out.println(path.toString());
        path = base.resolve(path);

        System.out.println(path.toString());
        //record it
        this.record.setResourceName(path.toString());

        file = path.toFile();
        if (!file.exists()) throw new ClientException(404, "Not Found", "file " + path.toString() + " does not exist");

        rsl = new ResponseStartLine("HTTP/1.0", 200, "OK");

        //record it
        this.record.setResponseType(rsl.getCode() +"");

        //handle headerString
        String headerString = "server: COMP2322-webserver\r\n" +
                              "date: " + java.time.LocalDateTime.now() + "\r\n"+
                              "content: text/html\r\n" +
                              "last-modified: " + time.convertLongTime(file.lastModified()) + "\r\n";

        try{
            header = new Header(headerString);
        }catch(ClientException ex){
            //never triggered
            ex.printStackTrace();
        }

        //handle entity
        String ims = request.getHeader("if-modified-since");
        if (ims != null && (util.time.convertLocalTime(ims)).isAfter(
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault())
        )) entity = null;
        else{
            try{
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                entity = in.readAllBytes();
            } catch(NullPointerException | IOException ex) {
                ex.printStackTrace();
            }
        }

        return new HTTPResponse(rsl, header, entity);
    }

    private HTTPResponse head() throws ClientException {
        ResponseStartLine rsl;
        Header header = null;
        File file = null;
        byte[] entity = null;

        //Handle startLine

        Path path = Paths.get(request.getUrl().substring(1));
        System.out.println(path.toString());
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
                "date: " + java.time.LocalDateTime.now() + "\r\n"+
                "content: text/html\r\n" +
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
                    new Header("server: COMP2322-webserver\r\n"),
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
            if (method.equals("GET")) response = get();
            else if (request.getMethod().equals("HEAD")) response = head();
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

        //log
        boolean success = false;
        while (!success) {
            success = logger.add(this.record);
        }
    }
}
