package exception;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathTest {
    public static void main(String[] args){
        Path base = Paths.get("/users/fengyunlin/Documents");
        Path rel = Paths.get("index.html");
        System.out.println(base.resolve(rel));
    }
}
