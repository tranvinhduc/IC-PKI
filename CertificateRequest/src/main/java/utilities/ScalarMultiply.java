package utilities;

import java.math.BigInteger;
import java.security.spec.ECPoint;

public class ScalarMultiply {

    public static ECPoint scalmult(ECPoint P, BigInteger kin) {

        ECPoint R = ECPoint.POINT_INFINITY, S = P;
        BigInteger k = kin.mod(Param.Curve.P);
        int length = k.bitLength();
        byte[] binarray = new byte[length];
        for (int i = 0; i <= length - 1; i++) {
            binarray[i] = k.mod(Param.Curve.TWO).byteValue();
            k = k.divide(Param.Curve.TWO);
        }

        for (int i = length - 1; i >= 0; i--) {
            R = doublePoint(R);
            if (binarray[i] == 1) {
                R = addPoint(R, S);
            }
        }
        return R;
    }

    public static ECPoint addPoint(ECPoint r, ECPoint s) {

        if (r.equals(s)) {
            return doublePoint(r);
        } else if (r.equals(ECPoint.POINT_INFINITY)) {
            return s;
        } else if (s.equals(ECPoint.POINT_INFINITY)) {
            return r;
        }
        BigInteger slope = (r.getAffineY().subtract(s.getAffineY())).multiply(r.getAffineX().subtract(s.getAffineX()).modInverse(Param.Curve.P)).mod(Param.Curve.P);
        BigInteger Xout = (slope.modPow(Param.Curve.TWO, Param.Curve.P).subtract(r.getAffineX())).subtract(s.getAffineX()).mod(Param.Curve.P);
        BigInteger Yout = s.getAffineY().negate().mod(Param.Curve.P);// gia tri -xS mod p
        Yout = Yout.add(slope.multiply(s.getAffineX().subtract(Xout))).mod(Param.Curve.P);
        ECPoint out = new ECPoint(Xout, Yout);
        return out;
    }

    public static ECPoint doublePoint(ECPoint r) {
        if (r.equals(ECPoint.POINT_INFINITY)) {
            return r;
        }
        BigInteger slope = (r.getAffineX().pow(2)).multiply(new BigInteger("3"));
        slope = slope.add(Param.Curve.A);
        slope = slope.multiply((r.getAffineY().multiply(Param.Curve.TWO)).modInverse(Param.Curve.P));
        BigInteger Xout = slope.pow(2).subtract(r.getAffineX().multiply(Param.Curve.TWO)).mod(Param.Curve.P);
        BigInteger Yout = (r.getAffineY().negate()).add(slope.multiply(r.getAffineX().subtract(Xout))).mod(Param.Curve.P);
        ECPoint out = new ECPoint(Xout, Yout);
        return out;
    }

    //getPU from String
    public static ECPoint getPUFromString(String PU) {
        String bit = PU.substring(0,1);
        int sign = Integer.parseInt(bit);
        String x = PU.substring(1);
        BigInteger X = new BigInteger(x,16);
        BigInteger right = X.modPow(Param.Curve.THREE, Param.Curve.P).add(X.multiply(Param.Curve.A)).add(Param.Curve.B).mod(Param.Curve.P);
        BigInteger Y = right.modPow(((Param.Curve.P.add(Param.Curve.ONE)).divide(Param.Curve.FOUR)),Param.Curve.P);
        //check y
        if (Y.mod(Param.Curve.TWO).intValue() != sign) {
            Y = Y.negate().mod(Param.Curve.P);
        }
        ECPoint pU = new ECPoint(X,Y);
        return pU;
    }

    public static String setStringFromECPoint(ECPoint pU){
        System.out.println("pU.X = " + pU.getAffineX().toString(16));
        System.out.println("pU.Y = " + pU.getAffineY().toString(16));
        String PU;
        BigInteger sign = pU.getAffineY().mod(Param.Curve.TWO);
        PU = sign.toString(16);
        PU += pU.getAffineX().toString(16);
        System.out.println("PU = " + PU);
        return PU;
    }
}
