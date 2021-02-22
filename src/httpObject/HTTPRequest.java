package httpObject;

import exception.ClientException;

import java.util.Locale;
import java.util.Scanner;

public class HTTPRequest {
    private RequestStartLine startline;
    private Header header;
    private Object entity;

    public HTTPRequest(String requestString) throws ClientException {
        Scanner sc = new Scanner(requestString);

        if (!sc.hasNextLine()) throw new ClientException(400, "Bad Request", "start-line not found");
        this.startline = new RequestStartLine(sc.nextLine());

        if (!sc.hasNextLine()) throw new ClientException(400 , "Bad Request", "headers not found");
        StringBuilder headerString = new StringBuilder();
        String line;
        while (sc.hasNextLine() && !(line = sc.nextLine()).equals("")){
            headerString.append(line + "\r\n");
        }
        System.out.println(headerString.toString());
        this.header = new Header(headerString.toString());

        if (sc.hasNext()) entity = sc.next();
        else entity = null;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(startline);
        sb.append(header + "\r\n");
        sb.append("[...Entity Body...]");

        return sb.toString();
    }

    public String getMethod(){
        return this.startline.getMethod();
    }

    public String getUrl(){
        return this.startline.getUrl();
    }

    public String getVersion(){
        return this.startline.getVersion();
    }

    public String getHeader(String name){
        //case-insensitive
        for (HeaderPair hp : header){
            if (hp.getName().toLowerCase(Locale.ROOT).compareTo(name.toLowerCase(Locale.ROOT)) == 0){
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
