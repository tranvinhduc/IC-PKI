package utilities;

import certificate.Certificate;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Random;

public class JsonUtil {
    public static ECPoint getRootPoint() throws Exception{
        KeyPairGenerator kpg;
        kpg = KeyPairGenerator.getInstance("EC", "SunEC");
        ECGenParameterSpec ecsp;
        ecsp = new ECGenParameterSpec("secp192r1");
        kpg.initialize(ecsp);
        ECParameterSpec Parameters = ((ECPublicKey) kpg.generateKeyPair().getPublic()).getParams();
        ECPoint G = Parameters.getGenerator();
        return G;
    }

    public static PublicKey getCAPublicKey() throws Exception{
        KeyPairGenerator kpg;
        kpg = KeyPairGenerator.getInstance("EC", "SunEC");
        ECGenParameterSpec ecsp;
        ecsp = new ECGenParameterSpec("secp192r1");
        kpg.initialize(ecsp);
        ECParameterSpec Parameters = ((ECPublicKey) kpg.generateKeyPair().getPublic()).getParams();
        ECPublicKeySpec caPublicKeySpec = new ECPublicKeySpec(Param.CAPublicKey.qCA, Parameters);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey caPublicKey = keyFactory.generatePublic(caPublicKeySpec);
        return caPublicKey;
    }


    //hash module N
    public static BigInteger hashN(String string) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(string.getBytes());
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            BigInteger h = new BigInteger(hashtext, 16);
            return h.mod(Param.Curve.N);
        } // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown"
                    + " for incorrect algorithm: " + e);
            return null;
        }
    }

    //function compute random k < n
    public static BigInteger getRandomBigInteger() {
        SecureRandom rand = new SecureRandom();
        BigInteger upperLimit = Param.Curve.N;
        BigInteger result;
        do {
            result = new BigInteger(upperLimit.bitLength(), rand);
        } while (result.compareTo(upperLimit) >= 0);
        return result;
    }

    //get full certificate string
    public static String getCertificateString(JsonObject certJsonObject) {
        String tbsCertStr = getTBSCertificate(certJsonObject);
        String sign = new BigInteger(certJsonObject.getString("Signature"),16).toString(16);
        return tbsCertStr + sign;
    }

    //get to be signed certificate string
    public static String getTBSCertificate(JsonObject certJsonObject) {
        Certificate cert = new Certificate();
        cert.setMesType(certJsonObject.getString("MESType"));
        cert.setSerialNumber(new BigInteger(certJsonObject.getString("SerialNumber"),16));
        cert.setCurve(certJsonObject.getString("Curve"));
        cert.setHash(certJsonObject.getString("Hash"));
        cert.setIssuerID(new BigInteger(certJsonObject.getString("Issuer"),16));
        cert.setValidFrom(new BigInteger(certJsonObject.getString("ValidFrom"),16));
        cert.setValidDuration(new BigInteger(certJsonObject.getString("ValidDuration"),16));
        cert.setSubject(new BigInteger(certJsonObject.getString("Subject"),16));
        cert.setKeyUsage(Integer.parseInt(certJsonObject.getString("Usage")));
        cert.setPU(certJsonObject.getString("PU"));
        cert.setPublicKeyAlgorithm(certJsonObject.getString("PublicKeyAlgorithm"));
        cert.setEmail(certJsonObject.getString("Email"));
        cert.setSignature(new BigInteger(certJsonObject.getString("Signature"),16));
        String tbsCertStr = makeTBSCertificate(cert);
//        System.out.println("tbsCertStr = " + tbsCertStr);
        return tbsCertStr;
    }

    //function to read conent from json file
    public static JsonObject readJsonFromFile(File file) throws FileNotFoundException, IOException {
        InputStream inputStream = new FileInputStream(file);
        JsonReader jsonReader = Json.createReader(inputStream);
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        inputStream.close();
        return jsonObject;
    }

    //function to write content to json file
    public static void writeJsonToFile(JsonObject jsonObject, File file) throws FileNotFoundException, IOException {
        OutputStream os;
        file.getParentFile().mkdirs();
        os = new FileOutputStream(file);
        JsonWriter jsonWriter = Json.createWriter(os);
        jsonWriter.writeObject(jsonObject);
    }

    public static boolean deleteFile(File file){
        if (file.exists()) {
            file.delete();
            return true;
        } else {
            return false;
        }
    }

    //create serial string with 40 charactor
    public static String getSerialString(BigInteger serial){
        String serialStr = serial.toString(16);
        int len = serialStr.length();
        while (len < 16){
            serialStr = '0' + serialStr;
            len += 1;
        }
        return serialStr;
    }

    //generate TBS Certificate to verify signature
    public static String makeTBSCertificate (Certificate cert){
        String tbsCert = cert.getMesType() + getSerialString(cert.getSerialNumber()) + cert.getCurve() +
                cert.getHash() + cert.getIssuerID().toString(16) + cert.getValidFrom().toString(16)+
                cert.getValidDuration().toString(16) + cert.getSubject().toString(16) +
                cert.getKeyUsage() + cert.getPU() + cert.getPublicKeyAlgorithm() + cert.getEmail();
        return tbsCert;
    }
}
