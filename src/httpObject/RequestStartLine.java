package httpObject;

import exception.ClientException;

import java.util.StringTokenizer;

/**
 * This Class represents the start line in a typical HTTP request message
 */
public class RequestStartLine {
    private String method;
    private String path;
    private String version;

    /**
     * Parse the startLine string
     * @param line the start line in the request message (with or without /r/n)
     * @throws ClientException when token count is not 3 (400 Bad Request)
     */
    public RequestStartLine(String line) throws ClientException {
        StringTokenizer sk = new StringTokenizer(line);
        if (sk.countTokens() !=3) throw new ClientException(400, "Bad Request", "malformed start-line: " + line);

        this.method = sk.nextToken();
        this.path = sk.nextToken();
        this.version = sk.nextToken();
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return method + " " + path + " " + version + "\r\n";
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
