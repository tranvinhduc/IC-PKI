package utilities;

import java.io.*;

public class FileUtil {

    public static String readWholeFile(String path) throws Exception {
        String content = "";
        File f = new File(path);
        FileInputStream fis = new FileInputStream(f);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        while (line != null) {
            content += line;
            line = br.readLine();
        }

        br.close();
        isr.close();
        fis.close();
        return content;
    }

    public static void writeFile(String content, String path) throws Exception {
        // System.out.println("Preparing to write " + content);
        File f = new File(path);
        FileOutputStream fos = new FileOutputStream(f);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
        osw.close();
        fos.close();
    }
}

    