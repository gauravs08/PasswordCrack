import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.xml.bind.DatatypeConverter;

public class SHA256HMAC_BufferOverrun {
    public static void main(String[] args) {
        try {
            String key = "This is a dummy key!";
            String message = "ClientCmd|202233|aaa";

            Mac hasher = Mac.getInstance("HmacSHA256");
            hasher.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));

            byte[] hash = hasher.doFinal(message.getBytes());

            // to lowercase hexits
            String hexBin = DatatypeConverter.printHexBinary(hash);
            System.out.println("hexBinary :" +hexBin.toLowerCase());

            // to base64
            String base64Binary = DatatypeConverter.printBase64Binary(hash);
            System.out.println("base64Binary: "+base64Binary);
        }
        catch (NoSuchAlgorithmException e) {}
        catch (InvalidKeyException e) {}
    }
}