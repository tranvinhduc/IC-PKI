import certificate.Extraction;
import certificate.Reception;
import certificate.Request;
import certificate.VerifyCertificate;
import utilities.JsonUtil;
import utilities.Param;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class certVerify {
    public static void main(String[] args) throws Exception{
        Param.CAPublicKey.init();
        File certFile, rFile, requestFile, crlFile;
        // -help
        if(args.length == 1 && args[0].equals("-help")){
            System.out.println("-request: make random k and rU");
            System.out.println("-extract: generate qU of cert");
            System.out.println("        -certificateFile: generate qU of cert");
            System.out.println("-recept certificateFilePath rFilePath requestFilePath: recept, get keypair of client.");
            System.out.println("        -certificateFilePath: path to certificate file");
            System.out.println("        -rFilePath: path to r file");
            System.out.println("        -requestFilePath: path to request file");
            System.out.println("-verify certificateFilePath crlFilePath");
            System.out.println("        -certificateFilePath: path to certificate file");
            System.out.println("        -crlFilePath: path to crl file");
        }
        // -request
        else if (args.length == 1 && args[0].equals("-request") ){
            Request.requestCetificate();
            System.out.println("Get xrU and yrU in Request.json file to add form certificate register.");
        }
        // -extract
        else if (args.length == 2 && args[0].equals("-extract")) {
            certFile = new File(args[1]);
            if (!certFile.isFile()){
                System.out.println("Check your file path");
            }
            else {
                Extraction.extract(certFile);
            }
        }
        // -verify
        else if (args.length == 3 && args[0].equals("-verify")) {
            certFile = new File(args[1]);
            crlFile = new File(args[2]);
            if (!certFile.isFile() || !crlFile.isFile()){
                System.out.println("Check your file path");
            }
            else {
                VerifyCertificate.verifyCertificate(certFile, crlFile);
            }
        }
        // -reception
        else if (args.length == 4 && args[0].equals("-recept")){
            certFile = new File(args[1]);
            rFile = new File(args[2]);
            requestFile = new File(args[3]);
            if (!certFile.isFile() || !rFile.isFile() || !requestFile.isFile()){
                System.out.println("Check your file path");
            }
            else {
                Reception.recept(certFile,rFile,requestFile);
            }
        }
        else {
            System.out.println("Use -help to more detail.");
        }
    }
}
