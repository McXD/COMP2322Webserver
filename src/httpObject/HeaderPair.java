package httpObject;

import exception.ClientException;

public class HeaderPair{
    private String name;
    private String value;

    public HeaderPair(String line) throws ClientException {
        int ind;
        if ((ind = line.indexOf(":")) == -1) throw new ClientException(400, "Bad Request", "malformed header pair: " + line);

        name = line.substring(0,ind).trim();
        value = line.substring(ind+1).trim();
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static void main(String[] args){
        try{
            HeaderPair hp = new HeaderPair("If-Modified-Since: 2021.1.10 21:10:20\r\n");
            System.out.print(hp);
            System.out.print("(EOF)");
        }catch(ClientException ex){
            ex.printStackTrace();
        }
    }
}