import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

public class PasswordCrackerMultiThreading {

    private static final String FILE_PATH = "D:/MASTER/MyCourses/Cs-C3130-Information Security/rockyou.txt";
    private static final String FILE_PATH2 = "D:/MASTER/MyCourses/Cs-C3130-Information Security/10k-most-common.txt";
    private static final String PASS_FILE_PATH = "D:/MASTER/MyCourses/Cs-C3130-Information Security/passwordHash.txt";


    public static void main(String[] args) {
        String[] salts = {"1439cd2ae28b9c6a", "bbe2478f77a3fafc", "a76e013d7d4453ef", "85719ea0aa6ea3b0", "2c79890bf25e3728", "e201bde074f36162", "09f703b445d7063d", "91c8a2e840366f7e", "c7ea98c93fd7a813", "dee07c5179854e25", "2e36ae2b1b4fba63", "b76ed7738cdd5854", "605c9358165485e9", "35728e67f561b4ce", "ce81e65fe39c929d", "98d936212f388f1b", "3147c2b274256d5e", "bd73708fa1c5d3cb", "f724e388db0cb0fb", "b19edb882489babb", "805ece80086b7064", "5f4ad39f3a3fc776", "3a3eae42bbf1442e", "76205bef6dfff4bb", "93eedb09e8b9a1c6", "0360cfd87de62421", "d02817f0a85324c0", "d6e52956c3d82aad", "e201bde074f36162", "48d14bf044c4366a", "ae48fd4a7653c527", "e201bde074f36162", "a33882adf068d75d", "43f0c2c749b38cf1", "515e22d5ecda3eb7"};
        int n = 1; // Number of salts threads
        for (int i = 0; i < n; i++) {
            MultiThreadingPassCrackThreading object = new MultiThreadingPassCrackThreading(salts[i]);
            object.setName(salts[i]+" - "+i);
            object.start();
        }
    }
}

class MultiThreadingPassCrackThreading extends Thread {
    private static final String FILE_PATH = "D:/MASTER/MyCourses/Cs-C3130-Information Security/rockyou.txt";
    private static final String FILE_PATH2 = "D:/MASTER/MyCourses/Cs-C3130-Information Security/10k-most-common.txt";
    private static final String PASS_FILE_PATH = "D:/MASTER/MyCourses/Cs-C3130-Information Security/passwordHash.txt";

    String currentSalt;

    MultiThreadingPassCrackThreading(String salt){
    this.currentSalt =salt;
    }


    public void run() {
        try {
            System.out.println("Processing salt Thread:: "+Thread.currentThread().getName());
            passCracker(this.currentSalt);
        } catch ( Exception e){
            System.out.println("Exception in processing password: "+e.getMessage());
        }
    }

    protected static void passCracker(String currentSalt) {
        System.out.println("Current Salt: "+currentSalt);
        //String[] salts ={"1439cd2ae28b9c6a","bbe2478f77a3fafc","a76e013d7d4453ef","85719ea0aa6ea3b0","2c79890bf25e3728","e201bde074f36162","09f703b445d7063d","91c8a2e840366f7e","c7ea98c93fd7a813","dee07c5179854e25","2e36ae2b1b4fba63","b76ed7738cdd5854","605c9358165485e9","35728e67f561b4ce","ce81e65fe39c929d","98d936212f388f1b","3147c2b274256d5e","bd73708fa1c5d3cb","f724e388db0cb0fb","b19edb882489babb","805ece80086b7064","5f4ad39f3a3fc776","3a3eae42bbf1442e","76205bef6dfff4bb","93eedb09e8b9a1c6","0360cfd87de62421","d02817f0a85324c0","d6e52956c3d82aad","e201bde074f36162","48d14bf044c4366a","ae48fd4a7653c527","e201bde074f36162","a33882adf068d75d","43f0c2c749b38cf1","515e22d5ecda3eb7"};
        int saltCount =0;
        int wordCount = 1;
        //1. read a password from rockyou.
        try {
            LineIterator it = FileUtils.lineIterator(new File(FILE_PATH), "UTF-8");
            try {
                //while(saltCount < salts.length) {
                    while (it.hasNext()) {
                        String line = it.nextLine();
                        // do something with line
                        //String generatedPassHash = processPasswordAndTruncate("potPlantSalt", line, salts[saltCount]);
                        String generatedPassHash = processPasswordAndTruncate("potPlantSalt", line, currentSalt);
                        boolean match = findStringInPasswordFile(generatedPassHash);
                        if (match) {
                            System.out.println("----Password Matched-----");
                            System.out.println("Password: " + line);
                            System.out.println("Password Hash: " + generatedPassHash);
                            break;
                        }
                        //System.out.println(line);
                        if (wordCount % 1000000 == 0) {
                            //break;
                            System.out.println("Processed password :" + wordCount + " current trying password: " + line);
                        }
                        //System.out.println(line+" : "+count);
                        wordCount++;
                    }
                    //saltCount++; }

            } finally {
                LineIterator.closeQuietly(it);
            }
        } catch (IOException io) {
            System.out.println("IO exception" + io.getMessage());
        }
    }

    protected static String processPasswordAndTruncate(String suffix, String passwordFormRockYou, String salt) {
        String result = null;
        //2. add suffix +password+ prefix
        String prefixPassSuffix = "potPlantSalt" + passwordFormRockYou + "bbe2478f77a3fafc";
        //String prefixPassSuffix = "potPlantSalt" + passwordFormRockYou + "1439cd2ae28b9c6a";
        //tring prefixPassSuffix = "potPlantSalt" + passwordFormRockYou + "515e22d5ecda3eb7";
        //String prefixPassSuffix = "potPlantSalt"+passwordFormRockYou ;
        //System.out.println("prefixPassSuffix:" + prefixPassSuffix);
        //3. create hash
        //System.out.println("Processing for: "+prefixPassSuffix);
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
