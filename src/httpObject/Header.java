package httpObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import exception.ClientException;


public class Header implements Iterable<HeaderPair>{
    private ArrayList<HeaderPair> pairs;

    public Header(String headerString) throws ClientException {
        pairs = new ArrayList<HeaderPair>();

        Scanner sc = new Scanner(headerString);
        if (!sc.hasNextLine()) throw new ClientException(400, "Bad Request", "header not found");

        while (sc.hasNextLine()){
            pairs.add(new HeaderPair(sc.nextLine()));
        }
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
