package net.hyze.core.shared.misc.utils;

import com.google.common.collect.Lists;
import net.hyze.core.shared.CoreConstants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Set;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {

    public static String getPage(String urlStr) throws IOException {

        Request request = new Request.Builder()
                .url(new URL(urlStr))
                .build();

        try (Response response = CoreConstants.OKHTTP_CLIENT.newCall(request).execute()) {
            return response.body().string();
        }

    }

    @Deprecated
    public static List<String> getPageCurl(String urlStr) throws IOException {

        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(new String[]{"curl", urlStr});

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        List<String> output = Lists.newArrayList();

        String s;

        while ((s = stdInput.readLine()) != null) {
            output.add(s);
        }

        return output;

    }

    public static void sendPost(URL url, Set<String> params) throws Exception {
        String[] output = params.toArray(new String[params.size()]);
        sendPost(url, output);
    }

    public static void sendPost(URL url, String... params) throws Exception {

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Java");
        con.setRequestProperty("Accept-Charset", "UTF-8");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            for (int i = 0; i < params.length; i++) {
                if (i == 0) {
                    os.write(params[i].getBytes());
                    continue;
                }

                os.write(("&" + params[i]).getBytes());
            }

            os.flush();
        }

        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {

            StringBuilder response;

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            System.out.println(response.toString());
        } else {
            System.out.println("Não foi possível enviar o POST: " + responseCode);
        }
    }

}
