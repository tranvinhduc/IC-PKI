package certificate;

import utilities.JsonUtil;

import javax.json.*;
import java.io.*;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.Signature;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static utilities.JsonUtil.readJsonFromFile;

public class VerifyCertificate {
    //verify Certificate
    public static boolean verifyCertificate(File certFile, File crlFile) throws Exception {
        if (!certFile.isFile() || !crlFile.isFile()){
            System.out.println("Check your import file path");
            return false;
        }
        if (!verifyCertSignature(certFile)){
            System.out.println("Certificate invalid - signature incorrect.");
            return false;
        }
        if (!verifyCertificateRevocation(certFile,crlFile)){
            return false;
        }
        if (!verifiyCertDate(certFile)) {
            System.out.println("Certificate expired");
            return false;
        }
        System.out.println("The certificate satisfies all condition:");
        System.out.println("    -certificate Signature valid");
        System.out.println("    -certificate Date valid");
        return true;
    }

    /*verify signature of certificate
    input: certificate file
    return true if signature valid */
    public static boolean verifyCertSignature(File certFile) throws Exception{
        PublicKey caPublicKey = JsonUtil.getCAPublicKey();
        JsonObject certJsonObject = readJsonFromFile(certFile);
        String tbsCertStr = JsonUtil.getTBSCertificate(certJsonObject);
        String signatureHashAlgorithm = certJsonObject.getString("Hash");
        BigInteger signature = new BigInteger(certJsonObject.getString("Signature"), 16); //signature in hex value
        Signature sign;
        sign = Signature.getInstance(signatureHashAlgorithm, "SunEC");;
        sign.initVerify(caPublicKey);
        sign.update(tbsCertStr.getBytes());
        boolean result = sign.verify(signature.toByteArray());
        return result;
    }

    //if date valid, return true
    public static boolean verifiyCertDate(File certFile) throws Exception{
        JsonObject certJsonObject = readJsonFromFile(certFile);
        BigInteger validFrom = new BigInteger(certJsonObject.getString("ValidFrom"),16);
        BigInteger validDuration = new BigInteger(certJsonObject.getString("ValidDuration"),16);
//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat ft = new SimpleDateFormat ("E MMMM dd hh:mm:ss zzz yyyy");
        long now = System.currentTimeMillis() / 1000;
        if (validFrom.longValue() < now && now < (validFrom.longValue() + validDuration.longValue()) ){
           return true;
        }
        return false;
    }

    /*check revoked cert
    input: certFile was check signature, crlFile.
    output: if cert was revoked, return false  */
    public static boolean verifyCertificateRevocation(File certFile, File crlFile) throws Exception{
        //check signature of crl
        if (!verifyCRLSignature(crlFile)){
            System.out.println("CRL signature invalid");
            return false; //invalid crl signature
        }
        JsonObject certJsonObject = readJsonFromFile(certFile);
        BigInteger serial = new BigInteger(certJsonObject.getString("SerialNumber"), 16);
        if (!checkCRSerial(crlFile,serial)){
            System.out.println("Certificate was revoked");
            return false; //cert was revoked
        }
        return true;
    }

    /*check if serial was revoked
    input: serial of certificate, crlFile
    if cert in crl ->return false, else return true */
    public static boolean checkCRSerial(File crlFile, BigInteger serial) throws Exception{
        CRL crl = readCRLFromFile(crlFile);
        List<CertificateRevocation> list = crl.getList();
        int len = list.size();
        for (int i = 0; i < len; i++) {
            if (list.get(i).getSerial().equals(serial)){
                return false;
            }
        }
        return true;
    }

    /*check signature of CRL file
    input: crlFile
    output: signature valid: return true, else return false */
    public static boolean verifyCRLSignature(File crlFile) throws Exception{
        PublicKey caPublicKey = JsonUtil.getCAPublicKey();
        //check signature of crl
            CRL crl = readCRLFromFile(crlFile);
        List<CertificateRevocation> list = crl.getList();
        JsonArrayBuilder arrayBuilders = Json.createArrayBuilder();
        for (int i =0; i < list.size(); i++){
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("Serial",list.get(i).getSerialHexString()).
                    add("Time", list.get(i).getTime().toString()).
                    add("Reason", list.get(i).getReason());
            arrayBuilders.add(objectBuilder);
        }
        JsonArray root = arrayBuilders.build();
        System.out.println("**************************");
        String conttent = crl.getCurve() + crl.getHash() + root.toString();
        System.out.println("conttent = " + conttent);
            //content to sign
        String signatureHashAlgorithm = crl.getHash();
        BigInteger signature = crl.getSignature();
        System.out.println("signature = " + signature);
        Signature sign;
        sign = Signature.getInstance(signatureHashAlgorithm, "SunEC");;
        sign.initVerify(caPublicKey);
        sign.update(conttent.getBytes());
        boolean result = sign.verify(signature.toByteArray());
        System.out.println("**************************");
        return result;
    }


    //read crl by JsonReader
    public static CRL readCRLFromFile(File file) throws Exception {
        List<CertificateRevocation> list = new ArrayList<CertificateRevocation>();
//        File file = new File(Param.CRL_PATH);
        if (!file.exists()) {
            file.createNewFile();
            //write scheam to CLR file
        }
        InputStream is = new FileInputStream(file);
        JsonReader jsonReader = Json.createReader(is);
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        is.close();
        CRL crl = new CRL();
        crl.setCurve(jsonObject.getString("Curve"));
        crl.setHash(jsonObject.getString("Hash"));
        crl.setSignature(new BigInteger( jsonObject.getString("Signature"), 16));
        JsonArray jsonArray = jsonObject.getJsonArray("crl");
//        System.out.println("content hash = " + crl.getSignatureAlgorithm() + crl.getSignatureHashAlgorithm() + jsonArray.toString());
        DateFormat format = new SimpleDateFormat("E MMMM dd hh:mm:ss zzz yyyy");
        for (JsonValue jsonValue : jsonArray){
            CertificateRevocation cr = new CertificateRevocation();
            cr.setSerial(new BigInteger(jsonValue.asJsonObject().getString("Serial"),16));
            cr.setTime(format.parse(jsonValue.asJsonObject().getString("Time")));
            cr.setReason(jsonValue.asJsonObject().getString("Reason"));
            list.add(cr);
        }
        crl.setList(list);
        return crl;
    }
}
