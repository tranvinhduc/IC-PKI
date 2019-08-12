package certificate;

import utilities.JsonUtil;
import utilities.ScalarMultiply;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;

public class Request {
    public static void requestCetificate() throws Exception{
        //root point
        KeyPairGenerator kpg;
        kpg = KeyPairGenerator.getInstance("EC", "SunEC");
        ECGenParameterSpec ecsp;
        ecsp = new ECGenParameterSpec("secp192r1");
        kpg.initialize(ecsp);
        ECParameterSpec p = ((ECPublicKey) kpg.generateKeyPair().getPublic()).getParams();
        ECPoint G = p.getGenerator();

        //generate random kU
        BigInteger kU = JsonUtil.getRandomBigInteger();
        ECPoint rU = ScalarMultiply.scalmult(G, kU);        //rU - value to send CA

        //write kU and xrU, yrU into Request.json file.
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonObject jsonObject;
        File output;
        builder.add("kU",kU.toString(16)).
                add("xRU", rU.getAffineX().toString(16)).
                add("yRU", rU.getAffineY().toString(16));
        jsonObject = builder.build();
        String currentDir = System.getProperty("user.dir");
        output = new File(Paths.get(currentDir, "Request.json").toString());
        JsonUtil.writeJsonToFile(jsonObject, output);
        System.out.println("xrU = " + rU.getAffineX().toString(16));
        System.out.println("yrU = " + rU.getAffineY().toString(16));
    }
}
