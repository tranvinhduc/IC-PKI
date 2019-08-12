package utilities;

import certificate.Extraction;

import javax.json.JsonObject;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.spec.ECPoint;
import java.util.HashMap;
import java.util.Map;

public class Param {

    public static class Curve{
        public static final BigInteger ONE = new BigInteger("1");
        public static BigInteger TWO = new BigInteger("2");
        public static BigInteger THREE = new BigInteger("3");
        public static BigInteger FOUR = new BigInteger("4");
        public static final BigInteger P = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFF", 16);
        public static final BigInteger N = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFF99DEF836146BC9B1B4D22831", 16);
        public static BigInteger A = new BigInteger("fffffffffffffffffffffffffffffffefffffffffffffffc", 16);
        public static BigInteger B = new BigInteger("64210519E59C80E70FA7E9AB72243049FEB8DEECC146B9B1", 16);
    }

    public static class CAPublicKey {
        public static BigInteger xqCA = null; //new BigInteger("3ee163db2993fa2d09fbebe82d21b4cb362c8aae6a86c56b",16);
        public static BigInteger yqCA = null; //new BigInteger("ee1e2302412c10ceaf2b737f826cc56f7b63a895542e8077",16);
        public static ECPoint qCA = null; //new ECPoint( xqCA,yqCA);

        public static void init() throws Exception {
            File CApub = new File(Paths.get(System.getProperty("user.dir"), "CApub.json").toString());
            if (!CApub.isFile()){
                System.out.println("Cannot find CApub.json file!");
                xqCA = new BigInteger("3ee163db2993fa2d09fbebe82d21b4cb362c8aae6a86c56b",16);
                yqCA = new BigInteger("ee1e2302412c10ceaf2b737f826cc56f7b63a895542e8077",16);
            }
            else {
                JsonObject CApubObj = JsonUtil.readJsonFromFile(CApub);
                xqCA = new BigInteger(CApubObj.getString("X_QCA"), 16);
                yqCA = new BigInteger(CApubObj.getString("Y_QCA"), 16);
            }
            qCA = new ECPoint(xqCA, yqCA);
        }

    }
    public static void init() throws Exception{
        CAPublicKey.init();
    }


}
