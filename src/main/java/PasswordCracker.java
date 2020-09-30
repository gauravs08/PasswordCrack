import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;

public class PasswordCracker {

    private static final String FILE_PATH = "D:/MASTER/MyCourses/Cs-C3130-Information Security/rockyou.txt";
    private static final String PASS_FILE_PATH = "D:/MASTER/MyCourses/Cs-C3130-Information Security/passwordHash.txt";

    public static void main(String[] args) {
        int count = 1;
        //1. read a password from rockyou.
        try {
            LineIterator it = FileUtils.lineIterator(new File(FILE_PATH), "UTF-8");
            try {
                while (it.hasNext()) {
                    String line = it.nextLine();
                    // do something with line
                    String generatedPassHash = processPasswordAndTruncate(line);
                    boolean match = findStringInPasswordFile(generatedPassHash);
                    if (match) {
                        System.out.println("----Password Matched-----");
                        System.out.println("Password: " + line);
                        System.out.println("Password Hash: " + generatedPassHash);
                    }
                    //System.out.println(line);
                    if (count % 1000000 ==0) {
                        //break;
                        System.out.println("Processed password :"+count +" current trying password: "+line);
                    }
                    //System.out.println(line+" : "+count);
                    count++;
                }
            } finally {
                LineIterator.closeQuietly(it);
            }
        } catch (IOException io) {
            System.out.println("IO exception" + io.getMessage());
        }
    }


    protected static String processPasswordAndTruncate(String passwordFormRockYou) {
        String result = null;
        //2. add suffix +password+ prefix
        String prefixPassSuffix = "potPlantSalt" + passwordFormRockYou + "d02817f0a85324c0";
        //String prefixPassSuffix = "potPlantSalt" + passwordFormRockYou + "1439cd2ae28b9c6a";
        //tring prefixPassSuffix = "potPlantSalt" + passwordFormRockYou + "515e22d5ecda3eb7";
        //String prefixPassSuffix = "potPlantSalt"+passwordFormRockYou ;
        //System.out.println("prefixPassSuffix:" + prefixPassSuffix);
        //3. create hash
        String sha256hex = DigestUtils.sha256Hex(prefixPassSuffix);
        //System.out.println("sha256hex: " + sha256hex);
        //4. truncate first 32 bits
        result = StringUtils.left(sha256hex, 32);
        return result;

    }

    protected static Boolean findStringInPasswordFile(String passwordToFind) throws IOException {
        Boolean match = FileUtils.readFileToString(new File(PASS_FILE_PATH)).contains(passwordToFind);
        if (match) System.out.println("password matched:" + passwordToFind);
        return match;
    }
}
