/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.takamaka.takamaka.core.wallet.examples.support;

import io.takamaka.wallet.utils.TkmTextUtils;
import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author admin
 */
@Slf4j
public class ProjectHelper {
    
    public static final String doPost(String passedUrl, String key, String param) throws MalformedURLException, ProtocolException, IOException {
        String r = null;
        URL url = new URL(passedUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        if (!TkmTextUtils.isNullOrBlank(key) && !TkmTextUtils.isNullOrBlank(param)) {
            String data = key + "=" + param;

            byte[] out = data.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);
        }

        int status = http.getResponseCode();

        switch (status) {
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                r = sb.toString();
                break;
            default:
                return null;
        }

        http.disconnect();

        return r;
    }
    
    public static String doPost(String uri, Map<String, String> parameters) throws MalformedURLException, IOException {

        URL url = new URL(uri);
        //F.b("Uri : " + uri);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST"); // PUT is another valid option
        http.setDoOutput(true);

        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
            //F.b("Key: " + entry.getKey() + " - Value: " + entry.getValue());
        }
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        //F.b(sj.toString());

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        } catch (Exception e) {
            log.error("Error sending data", e);
        }
        
        Reader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
        
        
        CharArrayWriter caw = new CharArrayWriter(100000);
        
        //String ret = "";
        for (int c; (c = in.read()) >= 0;) {
            caw.append((char) c);
        }
        return caw.toString();

    }
    
    
    
}
