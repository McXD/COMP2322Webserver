package logger;

import java.time.LocalDateTime;

public class Record {
    private final String DELIM = ";";

    private String ipAddress;
    private LocalDateTime accessTime;
    private String resourceName;
    private String responseType;

    public Record(String ipAddress, LocalDateTime accessTime, String resourceName, String responseType) {
        this.ipAddress = ipAddress;
        this.accessTime = accessTime;
        this.resourceName = resourceName;
        this.responseType = responseType;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setAccessTime(LocalDateTime accessTime) {
        this.accessTime = accessTime;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public Record(){

    }

    @Override
    public String toString(){
        return ipAddress + DELIM + util.time.convertString(accessTime) + DELIM + resourceName + DELIM + responseType + "\r\n";
    }
}
