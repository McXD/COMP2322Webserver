package logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Logger implements Runnable{
    BlockingQueue<Record> queue;
    File file;

    public Logger(int capacity, String des){
        this.queue = new LinkedBlockingQueue<Record>(capacity);
        this.file = new File(des);
        if (!file.exists()) throw new IllegalArgumentException("File " + des + "does not exist");
    }

    public boolean add(Record r){
        //return true if the record can be added
        //false otherwise
        return queue.offer(r);
    }

    private void log(){
        //write one record to the log file
        try{
            Record r = queue.take();
            System.out.print(r.toString());
            Files.writeString(file.toPath(), r.toString(), StandardOpenOption.APPEND);
        }catch(InterruptedException | IOException ex){
            ex.printStackTrace();
        }
    }

    public void run(){
        while(true) log();
    }
}
