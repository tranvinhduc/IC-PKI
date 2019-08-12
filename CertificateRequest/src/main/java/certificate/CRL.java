package certificate;

import java.math.BigInteger;
import java.util.List;

public class CRL {
    private String Curve;
    private String Hash;
    private BigInteger signature;
    List<CertificateRevocation> list;

    public String getCurve() {
        return Curve;
    }

    public void setCurve(String curve) {
        Curve = curve;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

    public BigInteger getSignature() {
        return signature;
    }

    public void setSignature(BigInteger signature) {
        this.signature = signature;
    }

    public List<CertificateRevocation> getList() {
        return list;
    }

    public void setList(List<CertificateRevocation> list) {
        this.list = list;
    }
}
