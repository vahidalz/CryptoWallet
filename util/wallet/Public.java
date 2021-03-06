package util.wallet;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import resources.Config;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPublicKey;

import static crypto.Hash.blake256;
import static crypto.Hash.sha256;
import static util.Base58.decode;
import static util.Base58.encode;
import static util.Hex.fromHex;
import static util.Hex.getHex;

public class Public implements Config{
    private static X9ECParameters x9Params = CustomNamedCurves.getByName(curve);

    public static String publicKeyToAddress(PublicKey publicKey) {


        String temp = publicKeyToString(publicKey);

        String hash = blake256(sha256(temp));

        temp = NETWORK_ID + hash;
        temp = temp + sha256(sha256(temp));

        return encode(fromHex(temp));
    }

    public static String publicKeyToString(PublicKey publicKey) {
        Security.addProvider(new BouncyCastleProvider());
        String tempX = getHex(((ECPublicKey)publicKey).getW().getAffineX().toByteArray());
        String tempY = getHex(((ECPublicKey)publicKey).getW().getAffineY().toByteArray());

        if (tempX.startsWith("00")) {
            tempX = tempX.substring(2);
        }

        if (tempY.startsWith("00")) {
            tempY = tempY.substring(2);
        }

        String temp = tempX + tempY;
        temp = "0x04" + temp;

        return encode(fromHex(temp));
    }

    public static String publicKeyToHexString(PublicKey publicKey) {
        Security.addProvider(new BouncyCastleProvider());
        String tempX = getHex(((ECPublicKey)publicKey).getW().getAffineX().toByteArray());
        String tempY = getHex(((ECPublicKey)publicKey).getW().getAffineY().toByteArray());

        if (tempX.startsWith("00")) {
            tempX = tempX.substring(2);
        }

        if (tempY.startsWith("00")) {
            tempY = tempY.substring(2);
        }

        String temp = tempX + tempY;
        temp = "0x04" + temp;

        return temp;
    }

    public static PublicKey privateKeyToPublicKey(PrivateKey privateKey) {
        Security.addProvider(new BouncyCastleProvider());

        KeyFactory keyFactory;
        try{
            keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        }
        catch (Exception e) {
            System.out.println("The key generator algorithm was not found.");
            throw new RuntimeException(e);
        }

        ECParameterSpec ecSpec = new ECParameterSpec(x9Params.getCurve(), x9Params.getG(), x9Params.getN(), x9Params.getH(), x9Params.getSeed());

        ECPoint Q = ecSpec.getG().multiply(((org.bouncycastle.jce.interfaces.ECPrivateKey) privateKey).getD());

        ECPublicKeySpec pubSpec = new ECPublicKeySpec(Q, ecSpec);
        PublicKey publicKey;
        try{
            publicKey = keyFactory.generatePublic(pubSpec);
        }
        catch(Exception e) {
            System.out.println("The keyspec on the imported wallet is invalid.");
            System.out.println("Failed to derive public key from private key.");
            throw new RuntimeException(e);
        }
        return publicKey;
    }

    public static PublicKey stringToPublicKey(String pubKeyString) {
        pubKeyString = getHex(decode(pubKeyString));
        if(pubKeyString.startsWith("0x")) {
            pubKeyString= pubKeyString.substring(2);
        }

        byte[] encoded = fromHex(pubKeyString);

        ECNamedCurveParameterSpec params = org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec(curve);

        KeyFactory fact;
        try {
            fact = KeyFactory.getInstance("ECDSA","BC");
        }
        catch (Exception e) {
            System.out.println("The key generator algorithm was not found.");
            System.out.println("Failed to convert string to public key.");
            throw new RuntimeException(e);
        }

        java.security.spec.EllipticCurve ellipticCurve = EC5Util.convertCurve(x9Params.getCurve(), x9Params.getSeed());
        java.security.spec.ECPoint point= ECPointUtil.decodePoint(ellipticCurve, encoded);
        java.security.spec.ECParameterSpec params2=EC5Util.convertSpec(ellipticCurve, params);
        java.security.spec.ECPublicKeySpec keySpec = new java.security.spec.ECPublicKeySpec(point,params2);

        PublicKey publicKey;
        try {
            publicKey = fact.generatePublic(keySpec);
        }
        catch (Exception e) {
            System.out.println("The keyspec on the public key string is invalid.");
            System.out.println("Failed to convert string to public key.");
            throw new RuntimeException(e);
        }

        return publicKey;
    }

    public static PublicKey hexStringToPublicKey(String pubKeyString) {
        if(pubKeyString.startsWith("0x")) {
            pubKeyString= pubKeyString.substring(2);
        }

        byte[] encoded = fromHex(pubKeyString);

        ECNamedCurveParameterSpec params = org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec(curve);

        KeyFactory fact;
        try {
            fact = KeyFactory.getInstance("ECDSA","BC");
        }
        catch (Exception e) {
            System.out.println("The key generator algorithm was not found.");
            System.out.println("Failed to convert string to public key.");
            throw new RuntimeException(e);
        }

        java.security.spec.EllipticCurve ellipticCurve = EC5Util.convertCurve(x9Params.getCurve(), x9Params.getSeed());
        java.security.spec.ECPoint point= ECPointUtil.decodePoint(ellipticCurve, encoded);
        java.security.spec.ECParameterSpec params2=EC5Util.convertSpec(ellipticCurve, params);
        java.security.spec.ECPublicKeySpec keySpec = new java.security.spec.ECPublicKeySpec(point,params2);

        PublicKey publicKey;
        try {
            publicKey = fact.generatePublic(keySpec);
        }
        catch (Exception e) {
            System.out.println("The keyspec on the public key string is invalid.");
            System.out.println("Failed to convert string to public key.");
            throw new RuntimeException(e);
        }

        return publicKey;
    }
}
