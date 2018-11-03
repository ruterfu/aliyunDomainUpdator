package com.ruterfu;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * PackageName Ruter Utils
 * User ruter
 * Author Ruter
 * Time 10:26 21/09/2017
 * Ruter的工具包
 */
public class Tools {

    private static MessageDigest md5Digest;
    private static DecimalFormat df = new DecimalFormat("0.00");
    private static SimpleDateFormat sdf = null;
    public static boolean isNull(String text) {
        return text == null || text.length() == 0 || text.trim().length() == 0;
    }
    public static boolean isNotNull(String... text) {
        for(int i = 0; i < text.length; i++) {
            if(text[i] == null || text[i].length() == 0) {
                return false;
            }
        }
        return true;
    }
    public static String[] split(String str, String separator) {
        if(str == null) return null;
        if(!str.contains(separator)) return new String[]{str};
        StringTokenizer st = new StringTokenizer(str, separator);
        String[] s = new String[st.countTokens()];
        int T = 0;
        while(st.hasMoreTokens()) {
            if(T < s.length) {
                s[T++] = st.nextToken().trim();
            }
        }
        return s;
    }

    public static String getTime(Long timeStamp) {
        if(timeStamp == null) {
            return null;
        }
        if(sdf == null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return sdf.format(timeStamp);
    }
    public static String url(String src) {
        try {
            return URLEncoder.encode(src, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return src;
    }

    public static String httpGet(String url, boolean curl) throws IOException {
        BufferedReader rd = null ;
        StringBuilder sb = new StringBuilder ();
        HttpURLConnection httpConn = getHttpConnection(url, curl);

        try {
            httpConn.connect();
            int code = httpConn.getResponseCode();
            if(code != 200) {
                rd  = new BufferedReader( new InputStreamReader(httpConn.getErrorStream(), "UTF-8"));
            } else {
                rd  = new BufferedReader( new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            }
            String line;
            while ((line = rd.readLine()) != null ) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { if(rd != null){ rd.close(); }  } catch (Exception ignored) { }
            try { if(httpConn != null){ httpConn.disconnect(); }  } catch (Exception ignored) { }
        }
        return null;
    }
    private static HttpURLConnection getHttpConnection(String accessUrl, boolean curl) throws IOException {
        URL url = new URL(accessUrl);
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setUseCaches(false);

        httpConn.setRequestProperty("Accept", "application/json");
        if(curl) {
            httpConn.setRequestProperty("User-Agent", "curl/7.29.0");
        }

        return httpConn;
    }

    /**
     * 将传入的arguments转成一定的格式返回
     * @param args args
     * @return
     */
    static Map<String,String> parseArguments(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        if(args != null && args.length >= 3) {
            for(String arg : args) {
                if(arg.startsWith("--access=")) {
                    arguments.put("access", arg.substring(9));
                } else if(arg.startsWith("--secret=")) {
                    arguments.put("secret", arg.substring(9));
                } else if(arg.startsWith("--domain=")) {
                    String[] splitDomain = Tools.split(arg.substring(9), ".");
                    // 它应该是 xx.aa.com或者是xx.yy.aa.com, aa.com是顶级域名, 而xx则二级域名
                    if(splitDomain.length >= 3) {
                        int len = splitDomain.length;
                        StringBuilder rr = new StringBuilder();
                        String topDomain = splitDomain[len - 2] + "." + splitDomain[len - 1];
                        for(int i = 0; i < len - 2; i++) {
                            rr.append(splitDomain[i]);
                            if(i < len - 3) {
                                rr.append(".");
                            }
                        }
                        arguments.put("topDomain", topDomain);
                        arguments.put("rr", rr.toString());
                    }
                }
            }
        }
        return arguments;
    }

    static void printSystemOut(String msg) {
        System.out.println("Thread " + Thread.currentThread().getId() + " - " + getTime(System.currentTimeMillis()) + " : " + msg + (msg.lastIndexOf(".") == msg.length() - 1 ? "" : "."));
    }

}