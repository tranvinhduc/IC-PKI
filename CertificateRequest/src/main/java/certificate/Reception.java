package certificate;

import utilities.JsonUtil;
import utilities.Param;
import utilities.ScalarMultiply;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.security.spec.ECPoint;
import java.util.Base64;

public class Reception {

    public static void recept(File certFile, File rFile, File requestFile) throws Exception{
        File inputFile, outputFile;
        JsonObject certJsonObject, rJsonObject, kJsonObject,keyUJsonObject;
        JsonObjectBuilder builder = Json.createObjectBuilder();

        //root point. public key of CA
        ECPoint G = JsonUtil.getRootPoint();
        PublicKey caPublicKey = JsonUtil.getCAPublicKey();

        // read received certificate and r from CA and k from certRequest
        certJsonObject = JsonUtil.readJsonFromFile(certFile);
        rJsonObject = JsonUtil.readJsonFromFile(rFile);
        kJsonObject = JsonUtil.readJsonFromFile(requestFile);

        BigInteger r = new BigInteger(rJsonObject.getString("r"), 16);

        boolean checkSignature = VerifyCertificate.verifyCertSignature(certFile);
        if (checkSignature == true) {
            System.out.println("Certificate signature and date valid!");
            String EncodeCertU = Base64.getEncoder().encodeToString(JsonUtil.getCertificateString(certJsonObject).getBytes());
            BigInteger kU = new BigInteger(kJsonObject.getString("kU"), 16);
            BigInteger e = JsonUtil.hashN(EncodeCertU);

            System.out.println("e = " + e);
            System.out.println("r = " + r.toString(16));

            BigInteger dU = e.multiply(kU).add(r).mod(Param.Curve.N);            //dU = e*kU + r
            ECPoint qU = Extraction.extract(certFile);
            ECPoint qUcheck = ScalarMultiply.scalmult(G, dU);
            if (qU.getAffineX().equals(qUcheck.getAffineX()) && qU.getAffineY().equals(qUcheck.getAffineY())) {
                System.out.println("KeyPair valid!");
//                builder.add("dU",dU.toString(16)).add("xqU",qU.getAffineX().toString(16)).add("yqU", qU.getAffineY().toString(16));
                builder.add("dU",dU.toString(16));
                keyUJsonObject = builder.build();
                String currendir = System.getProperty("user.dir");
                outputFile = new File(Paths.get(currendir, "PrivateKey.json").toString());
                JsonUtil.writeJsonToFile(keyUJsonObject, outputFile);
                System.out.println("Privatekey was written in privatekey file.");
            }
            else {
                System.out.println("Can not generate client key pair. Maybe r value was changed.");
            }
        }
        else  {
            System.out.println("Signature invalid");
        }
    }
}
