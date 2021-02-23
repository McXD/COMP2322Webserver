package httpObject;

import exception.ClientException;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * This Class represents a HTTP request message
 */
public class HTTPRequest {
    public void setStartline(RequestStartLine startline) {
        this.startline = startline;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public void setEntity(byte[] entity) {
        this.entity = entity;
    }

    private RequestStartLine startline;
    private Header header;
    private byte[] entity;

    /**
     * Parse the request string
     * Entity should be UTF-8 encoded string
     * @param requestString
     * @throws ClientException
     */
    public HTTPRequest(String requestString) throws ClientException {
        Scanner sc = new Scanner(requestString);

        if (!sc.hasNextLine()) throw new ClientException(400, "Bad Request", "start-line not found");
        this.startline = new RequestStartLine(sc.nextLine());

        if (!sc.hasNextLine()) throw new ClientException(400 , "Bad Request", "headers not found");
        StringBuilder headerString = new StringBuilder();
        String line;
        while (sc.hasNextLine() && !(line = sc.nextLine()).equals("")){ //an empty line indicates end of header
            headerString.append(line + "\r\n");
        }
        this.header = new Header(headerString.toString());

        if (sc.hasNext()) entity = sc.next().getBytes(StandardCharsets.UTF_8);
        else entity = null;
    }

    public HTTPRequest(RequestStartLine startLine, Header header, byte[] entity){
        this.startline = startLine;
        this.header = header;
        this.entity = entity;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(startline);
        sb.append(header + "\r\n");
        if (entity != null) sb.append("[...Entity Body...](" + entity.length + ") bytes");

        return sb.toString();
    }

    public String getMethod(){
        return this.startline.getMethod();
    }

    public String getPath(){
        return this.startline.getPath();
    }

    public String getVersion(){
        return this.startline.getVersion();
    }

    public String getHeader(String name){
        //case-insensitive
        for (HeaderPair hp : header){
            if (hp.getName().toLowerCase().compareTo(name.toLowerCase()) == 0){
                return hp.getValue();
            }
        }

        return null;
    }

    public Object getEntity(){
        return this.entity;
    }

    public static void main(String[] args){
        String request = "GET /index.html HTTP/1.0\r\nclient:mozilla\r\ndate:Mon Feb 22 00:03:51 CST 2021\r\naccept:text/html\r\n\r\n";

        try{
            HTTPRequest hr = new HTTPRequest(request);
            System.out.print(hr);
            System.out.print("(EOF)");
        }catch(ClientException ex){
            ex.printStackTrace();
        }
    }
}
