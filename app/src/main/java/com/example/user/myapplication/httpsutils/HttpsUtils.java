package com.example.user.myapplication.httpsutils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by user on 2016/9/22.
 */
public class HttpsUtils {
    /**
     * * https://itconnect.uw.edu/security/securing-computer/install/installing-the-uw-services-ca-on-ie7-ie8-ie9-windows-vista-and-windows-7-2/
     * 通过以下命令可以打印出来
     */
    public static final String TEST_STRING_CRT = "-----BEGIN CERTIFICATE-----\n" +
            "MIIEBzCCA3CgAwIBAgIBADANBgkqhkiG9w0BAQQFADCBlDELMAkGA1UEBhMCVVMx\n" +
            "CzAJBgNVBAgTAldBMSEwHwYDVQQKExhVbml2ZXJzaXR5IG9mIFdhc2hpbmd0b24x\n" +
            "FDASBgNVBAsTC1VXIFNlcnZpY2VzMRcwFQYDVQQDEw5VVyBTZXJ2aWNlcyBDQTEm\n" +
            "MCQGCSqGSIb3DQEJARYXaGVscEBjYWMud2FzaGluZ3Rvbi5lZHUwHhcNMDMwMjI1\n" +
            "MTgyNTA5WhcNMzAwOTAzMTgyNTA5WjCBlDELMAkGA1UEBhMCVVMxCzAJBgNVBAgT\n" +
            "AldBMSEwHwYDVQQKExhVbml2ZXJzaXR5IG9mIFdhc2hpbmd0b24xFDASBgNVBAsT\n" +
            "C1VXIFNlcnZpY2VzMRcwFQYDVQQDEw5VVyBTZXJ2aWNlcyBDQTEmMCQGCSqGSIb3\n" +
            "DQEJARYXaGVscEBjYWMud2FzaGluZ3Rvbi5lZHUwgZ8wDQYJKoZIhvcNAQEBBQAD\n" +
            "gY0AMIGJAoGBALwCo6h4T44m+7ve+BrnEqflqBISFaZTXyJTjIVQ39ZWhE0B3Laf\n" +
            "bbZYju0imlQLG+MEVAtNDdiYICcBcKsapr2dxOi31Nv0moCkOj7iQueMVU4E1Tgh\n" +
            "YIR2I8hqixFCQIP/CMtSDail/POzFzzdVxI1pv2wRc5cL6zNwV25gbn3AgMBAAGj\n" +
            "ggFlMIIBYTAdBgNVHQ4EFgQUVdfBM8b6k/gnPcsgS/VajliXfXQwgcEGA1UdIwSB\n" +
            "uTCBtoAUVdfBM8b6k/gnPcsgS/VajliXfXShgZqkgZcwgZQxCzAJBgNVBAYTAlVT\n" +
            "MQswCQYDVQQIEwJXQTEhMB8GA1UEChMYVW5pdmVyc2l0eSBvZiBXYXNoaW5ndG9u\n" +
            "MRQwEgYDVQQLEwtVVyBTZXJ2aWNlczEXMBUGA1UEAxMOVVcgU2VydmljZXMgQ0Ex\n" +
            "JjAkBgkqhkiG9w0BCQEWF2hlbHBAY2FjLndhc2hpbmd0b24uZWR1ggEAMAwGA1Ud\n" +
            "EwQFMAMBAf8wKwYDVR0RBCQwIoYgaHR0cDovL2NlcnRzLmNhYy53YXNoaW5ndG9u\n" +
            "LmVkdS8wQQYDVR0fBDowODA2oDSgMoYwaHR0cDovL2NlcnRzLmNhYy53YXNoaW5n\n" +
            "dG9uLmVkdS9VV1NlcnZpY2VzQ0EuY3JsMA0GCSqGSIb3DQEBBAUAA4GBAIn0PNmI\n" +
            "JjT9bM5d++BtQ5UpccUBI9XVh1sCX/NdxPDZ0pPCw7HOOwILumpulT9hGZm9Rd+W\n" +
            "4GnNDAMV40wes8REptvOZObBBrjaaphDe1D/MwnrQythmoNKc33bFg9RotHrIfT4\n" +
            "EskaIXSx0PywbyfIR1wWxMpr8gbCjAEUHNF/\n" +
            "-----END CERTIFICATE-----";
    public static final String TEST_URL = "https://certs.cac.washington.edu/CAtest/";
    public static final String TEST_ASSET_FILENAME = "load-der.crt";


    /**HttpsURLConnection
     * 信任指定的第三方证书
     * 信任指定的第三方证书
     * 官方推荐 添加信任的第三方证书
     * 一般的，给https添加信任的证书
     * 还有一种情况，分两步 ：第一步：添加信任证书(此步必须有)，第二步：添加 验证主机名 。
     * 如果报错:java.io.IOException: Hostname 'example.com' was not verified 请添加验证主机名
     *
     * @param urlPath
     * @param crtInput
     * @param hostNameVerify
     * @return
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     */
    public String httpsConnection(String urlPath, InputStream crtInput, String hostNameVerify) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        // 告诉 URlConnection 使用 上面的sslContext中的SocketFatory
        URL url = new URL(urlPath);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        //重点一：告诉 URlConnection 使用 sslContext中的SocketFatory(必须)
        urlConnection.setSSLSocketFactory(initSSL(crtInput).getSocketFactory());
        //重点二：如果需要验证主机名，告诉UrlConnection 使用上面的 HostNameVerifier
        if (hostNameVerify != null) {
            urlConnection.setHostnameVerifier(setHostNameVerify(hostNameVerify));
        }
        InputStream in = urlConnection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuffer sb = new StringBuffer();
        String line = "";
        if ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        in.close();
        return sb.toString();
    }

    /**
     * 从asserts中取得证书文件 流
     *
     * @param context
     * @param assetsFileName
     * @return
     * @throws IOException
     */
    public InputStream getInputStreamFromAssets(Context context, String assetsFileName) throws IOException {
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
        return context.getAssets().open(assetsFileName);
    }

    public InputStream getInputStreamFromStr(String str) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(str.getBytes("UTF-8"));
    }

    /**
     * 官方推荐 添加信任的第三方证书 HttpsURLConnection
     * 一般的，给https添加信任的证书
     * 还有一种情况，分两步 ：第一步：添加信任证书(此步必须有)，第二步：添加 验证主机名 。
     * 如果报错:java.io.IOException: Hostname 'example.com' was not verified 请添加验证主机名
     *
     * @param caInput
     * @return result
     * @throws CertificateException
     * @throws IOException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private SSLContext initSSL(InputStream caInput) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        //从inputStream流中读取 CAs
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }
        //创建 一个 密钥库 包含我们相信的CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        //创建一个 信任管理者 信任keystore里的CAs。
        String tmfAlgorihtm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorihtm);
        tmf.init(keyStore);
        //创建一个sslcontext 用来使用TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
//        // 告诉 URlConnection 使用 上面的sslContext中的SocketFatory
//        URL url = new URL(urlPath);
//        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
//        urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

        return sslContext;
    }


    /**HttpsURLConnection  请求，信任所有证书
     * @param urlStr
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private void httpsSSLAll(String urlStr) throws IOException, NoSuchAlgorithmException, KeyManagementException {
       URL url = new URL(urlStr);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null,new TrustManager[]{new TrustAllManager()},null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
        httpsURLConnection.setDoInput(true);
        httpsURLConnection.setDoOutput(false);
        httpsURLConnection.connect();
        InputStream in = httpsURLConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
        String line = "";
        StringBuffer sb = new StringBuffer();
        while ((line = reader.readLine())!=null){
            sb.append(line);
        }
        Log.e("HTTPS", "httpsSSLAll: "+sb.toString() );
    }

    /**
     * 如果报错:java.io.IOException: Hostname 'example.com' was not verified 请添加验证主机名
     */
    private HostnameVerifier setHostNameVerify(final String hostNameVerify) {
        //创建一个HostnameVerifier ,固定期望的主机名
        //注意，这与url的hostname 不同
        //他们两个是相对的 eg."example.com" 与 URL url = new URL("https://example.org/");

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify(hostNameVerify, session);
            }
        };
//        //告诉UrlConnection 使用上面的 HostNameVerifier
//        try {
//            URL url = new URL("https://example.org/");
//            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
//            urlConnection.setHostnameVerifier(hostnameVerifier);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return hostnameVerifier;
    }


    private class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
//            return new X509Certificate[0];
            return null;
        }
    }
}
