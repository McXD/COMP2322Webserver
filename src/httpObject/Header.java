package httpObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import exception.ClientException;

/**
 * This class represents the head field of a HTTP message
 */
public class Header implements Iterable<HeaderPair>{
    private ArrayList<HeaderPair> pairs;

    /**
     * Parse the client header string
     * eg."client: mozilla\r\naccept: *\r\n"
     * @param headerString
     * @throws ClientException when at least one header is malformed
     */
    public Header(String headerString) throws ClientException {
        pairs = new ArrayList<HeaderPair>();

        if (headerString == null) return;

        Scanner sc = new Scanner(headerString);

        while (sc.hasNextLine()){
            String nextLine = sc.nextLine();
            if (!nextLine.isBlank() && !nextLine.isEmpty()) pairs.add(new HeaderPair(nextLine));
        }
    }

    /**
     * Construct a HeadPair instance from the given ArrayList. Used by server side.
     * @param headerPairs
     */
    public Header(ArrayList<HeaderPair> headerPairs){
        this.pairs = headerPairs;
    }

    /**
     * Fetch the value of name, null if name is not in the header
     * @param name
     * @return corresponding value
     */
    public String get(String name){
        for (HeaderPair p : this.pairs){
            if (p.getName().equals(name)) return p.getValue();
        }

        return null;
    }

    @Override
    public Iterator<HeaderPair> iterator() {
        return pairs.iterator();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (HeaderPair hp : pairs){
            sb.append(hp.toString() + "\r\n");
        }

        return sb.toString();
    }

    public static void main(String[] args){
        String headerString = "content-type: text/html\r\ndate:Sun Feb 21\r\n";
        try{
            Header h = new Header(headerString);
            for (HeaderPair hp : h){
                System.out.println(hp);
            }
        }catch(ClientException ex){
            ex.printStackTrace();
        }
    }
}
