package certificate;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;

public class CertificateRevocation {
    private BigInteger serial;  //serial of certificate
    private Date time;                //time that certificate is revoked
    private String reason;            //the reason that certificate is revoked

    //getSerialString
    public String getSerialHexString(){
        String serialStr = serial.toString(16);
        int len = serialStr.length();
        while (serialStr.length() < 16){
            serialStr = '0' + serialStr;
        }
        return serialStr;
    }

    public BigInteger getSerial() {
        return serial;
    }

    public void setSerial(BigInteger serial) {
        this.serial = serial;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
