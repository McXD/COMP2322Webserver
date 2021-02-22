package httpObject;

import exception.ClientException;

import java.util.StringTokenizer;

public class RequestStartLine {
    private String method;
    private String url;
    private String version;

    public RequestStartLine(String line) throws ClientException {
        StringTokenizer sk = new StringTokenizer(line);
        if (sk.countTokens() !=3) throw new ClientException(400, "Bad Request", "malformed start-line: " + line);

        this.method = sk.nextToken();
        this.url = sk.nextToken();
        this.version = sk.nextToken();
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return method + " " + url + " " + version + "\r\n";
    }

    public static void main(String[] args){
        String line = "GET /index.html HTTP/1.0";
        try{
            RequestStartLine sl = new RequestStartLine(line);
            System.out.println(sl.toString());
        }catch(ClientException ex){
            ex.printStackTrace();
        }
    }
}
