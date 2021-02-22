package httpObject;

import java.util.StringTokenizer;

public class ResponseStartLine {
    private String version;
    private int code;
    private String description;

    public ResponseStartLine(String version, int code, String description) {
        this.version = version;
        this.code = code;
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return version + " " + code + " " + description + "\r\n";
    }
}
