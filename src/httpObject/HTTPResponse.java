package httpObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class HTTPResponse {
    private ResponseStartLine startLine;
    private Header header;
    private byte[] entity;


    public HTTPResponse(ResponseStartLine startLine, Header header, byte[] entity) {
        this.startLine = startLine;
        this.header = header;
        this.entity = entity;
    }

    @Override
    public String toString(){
        return startLine.toString() + header.toString() + "\r\n" + "[...Entity Body...]";
    }

    public byte[] toByteArray(){
        try {
            byte[] bStartLine = startLine.toString().getBytes(StandardCharsets.US_ASCII);
            byte[] bHeader = header.toString().getBytes(StandardCharsets.US_ASCII);
            byte[] bEmptyLine = "\r\n".getBytes(StandardCharsets.US_ASCII);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            out.write(bStartLine);
            out.write(bHeader);
            out.write(bEmptyLine);
            if (entity != null) out.write(entity);

            return out.toByteArray();
        }catch (IOException e) {
            //not triggered
            e.printStackTrace();
        }

        return null;
    }
}
