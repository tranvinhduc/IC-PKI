package certificate;

import utilities.JsonUtil;
import utilities.Param;
import utilities.ScalarMultiply;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.spec.ECPoint;
import java.util.Base64;

import static utilities.JsonUtil.readJsonFromFile;

public class Extraction {

//    input: cert file and public key of CA, output: public key of certificate.
    public static ECPoint extract(File certificateFile) throws Exception{
        JsonObject certJsonObject;
        JsonObjectBuilder builder = Json.createObjectBuilder();

        //root point. public key of CA
        ECPoint G = JsonUtil.getRootPoint();
        PublicKey caPublicKey = JsonUtil.getCAPublicKey();

        // read received certificate
        certJsonObject = readJsonFromFile(certificateFile);

        boolean checkSignature = VerifyCertificate.verifyCertSignature(certificateFile);
//        boolean checkDate = VerifyCertificate.verifiyCertDate(new File(CertificateFile));
        if (checkSignature == true) {
            System.out.println("Certificate signature valid!");
            String EncodeCertU = Base64.getEncoder().encodeToString(JsonUtil.getCertificateString(certJsonObject).getBytes());
            BigInteger e = JsonUtil.hashN(EncodeCertU);

//            System.out.println("EncodeCertU = " + EncodeCertU);
//            System.out.println("e = " + e);

            String PU = certJsonObject.getString("PU");
            ECPoint pU = ScalarMultiply.getPUFromString(PU);
            ECPoint qU = ScalarMultiply.addPoint(ScalarMultiply.scalmult(pU, e), Param.CAPublicKey.qCA); //qU = e*pU + qCA
            System.out.println("Public key of U: x = " + qU.getAffineX().toString(16));
            System.out.println("                 y = " + qU.getAffineY().toString(16));
            return qU;
        }
        else  {
            System.out.println("Signature invalid");
        }
        return null;
    }
}
