package exception;

public class ClientException extends Exception{
    private int code;
    private String description;
    private String detail;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getDetail(){
        return detail;
    }

    public ClientException(int code, String description, String detail){
        super();
        this.code = code;
        this.description = description;
        this.detail = detail;
    }
}
