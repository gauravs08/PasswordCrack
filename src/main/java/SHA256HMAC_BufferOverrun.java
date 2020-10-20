import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.xml.bind.DatatypeConverter;

public class SHA256HMAC_BufferOverrun {
    public static void main(String[] args) {
        try {
            //String key = "12345678912345678912";
            //String message = "ClientCmd|202233|aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa12345678912345678912";
            String key = "This is a dummy key!";
            System.out.println("KEY:"+key);
            String command = "aaa............................";
            System.out.println("command:"+command);
            String message = "ClientCmd|131511|"+command+key;
            System.out.println("Msg:"+message);
            //ClientCmd|202233|aaa ...........................This is a dummy key!
            Mac hasher = Mac.getInstance("HmacSHA256");
            hasher.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));

            byte[] hash = hasher.doFinal(message.getBytes());
            //System.out.println("HASH:"+hash);
            // to lowercase hexits
            String hexBin = DatatypeConverter.printHexBinary(hash);
            System.out.println("hexBinary :" +hexBin.toLowerCase());
            // to base64

            String base64Binary = DatatypeConverter.printBase64Binary(hash);
            System.out.println("base64Binary: "+base64Binary+"\n");
            System.out.println("echo \"131511;"+command+key+";"+hexBin.toLowerCase()+"\" | ncat device1.vikaa.fi 35293");
            telnetCode(command,key,hexBin.toLowerCase());
        }
        catch (NoSuchAlgorithmException e) {}
        catch (InvalidKeyException e) {}
    }

    public static void telnetCode(String cmd, String key, String hexBin) {
        Socket pingSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            pingSocket = new Socket("device1.vikaa.fi", 35293);
            out = new PrintWriter(pingSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("IOException" + e.getMessage());
        }
        //System.out.println("echo \"202233;" + cmd + oneCharShortKey + ";" + hexBin.toLowerCase() + "\" | ncat device1.vikaa.fi 35291");
        out.println("131511;" + cmd + key + ";" + hexBin.toLowerCase());

        try {
            String readLines = in.readLine();
            if (!readLines.equals("Invalid HMAC. Command not accepted.")) {
                System.out.println(readLines);
                System.out.println(" -----lastchar:" );
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
}