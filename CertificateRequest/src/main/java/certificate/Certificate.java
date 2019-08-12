package certificate;

import java.math.BigInteger;

public class Certificate {
    private String mesType;                    //T2 standard: 1 byte
    private BigInteger serialNumber;           //8 byte
    private String curve;                     //curve generate signature and public key: 1byte
    private String hash;                       //1byte
    private BigInteger issuerID;              //8 byte
    private BigInteger validFrom;               //5byte: second
    private BigInteger validDuration;           //4byte: second
    private BigInteger subject;                  //8 byte
    private Integer keyUsage;                   //1 byte
    private String PU;                             //25byte
    private BigInteger signature = BigInteger.ZERO;
    //optional
    private String email;                       //0-126
    private String publicKeyAlgorithm;             //1 byte

    public String getMesType() {
        return mesType;
    }

    public void setMesType(String mesType) {
        this.mesType = mesType;
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCurve() {
        return curve;
    }

    public void setCurve(String curve) {
        this.curve = curve;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public BigInteger getIssuerID() {
        return issuerID;
    }

    public void setIssuerID(BigInteger issuerID) {
        this.issuerID = issuerID;
    }

    public BigInteger getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(BigInteger validFrom) {
        this.validFrom = validFrom;
    }

    public BigInteger getValidDuration() {
        return validDuration;
    }

    public void setValidDuration(BigInteger validDuration) {
        this.validDuration = validDuration;
    }

    public BigInteger getSubject() {
        return subject;
    }

    public void setSubject(BigInteger subject) {
        this.subject = subject;
    }

    public Integer getKeyUsage() {
        return keyUsage;
    }

    public void setKeyUsage(Integer keyUsage) {
        this.keyUsage = keyUsage;
    }

    public String getPU() {
        return PU;
    }

    public void setPU(String PU) {
        this.PU = PU;
    }

    public BigInteger getSignature() {
        return signature;
    }

    public void setSignature(BigInteger signature) {
        this.signature = signature;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPublicKeyAlgorithm() {
        return publicKeyAlgorithm;
    }

    public void setPublicKeyAlgorithm(String publicKeyAlgorithm) {
        this.publicKeyAlgorithm = publicKeyAlgorithm;
    }
}
