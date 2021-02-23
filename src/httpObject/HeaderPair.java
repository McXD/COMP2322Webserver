package httpObject;

import exception.ClientException;

/**
 * Used to represent a header pair (eg. "client: mozilla")
 */
public class HeaderPair{

    //name and value are space free
    private String name;
    private String value;

    /**
     * Construct a HeaderPair instance from a client header line
     * @param line
     * @throws ClientException if the header pair if malformed
     */
    public HeaderPair(String line) throws ClientException {
        int ind;
        if ((ind = line.indexOf(":")) == -1) throw new ClientException(400, "Bad Request", "malformed header pair: " + line);

        //trim the white spaces
        name = line.substring(0,ind).trim();
        value = line.substring(ind+1).trim();
    }

    /**
     * Construct a HeadPair instance from the given pair
     * @param name
     * @param value
     */
    public HeaderPair(String name, String value){
        this.name = name;
        this.value = value;
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
}