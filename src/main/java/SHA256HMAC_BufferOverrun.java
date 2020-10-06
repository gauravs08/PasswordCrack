import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
            String message = "ClientCmd|202233|"+command+key;
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
            System.out.println("echo \"202233;"+command+key+";"+hexBin.toLowerCase()+"\" | ncat device1.vikaa.fi 35291");
        }
        catch (NoSuchAlgorithmException e) {}
        catch (InvalidKeyException e) {}
    }
}