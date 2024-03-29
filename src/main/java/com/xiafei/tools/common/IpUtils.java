package com.xiafei.tools.common;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * <P>Description: IP地址工具. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/2/5</P>
 * <P>UPDATE DATE: 2018/2/5</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class IpUtils {

    private static volatile String REAL_IP = null;
    private static volatile Integer SERVER_PORT = null;
    private static final char FILE_SEPARATOR = File.separatorChar;
    private static volatile String NOTIFY_URL_PREFIX;

    private IpUtils() {
        try {
            NOTIFY_URL_PREFIX = "http://" + IpUtils.getRealIp() + ":" + IpUtils.getServerPort();
        } catch (Throwable e) {
            log.warn("加载本机Ip端口号失败", e);
        }
    }

    /**
     * 获取本机url，例：http://127.0.0.1:8080
     *
     * @return 本机url
     */
    public static String getLocalUrl() {
        if (NOTIFY_URL_PREFIX == null) {
            synchronized (IpUtils.class) {
                if (NOTIFY_URL_PREFIX == null) {
                    try {
                        NOTIFY_URL_PREFIX = "http://" + IpUtils.getRealIp() + ":" + IpUtils.getServerPort();
                    } catch (Throwable e) {
                        log.warn("加载本机Ip端口号失败", e);
                    }
                }
            }
        }
        return NOTIFY_URL_PREFIX;
    }

    public static String getReqIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            System.out.println("WL-Proxy-Client-IP  ip=" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            System.out.println("HTTP_CLIENT_IP  ip=" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            System.out.println("X-Real-IP  ip=" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            System.out.println("HTTP_X_FORWARDED_FOR  ip=" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("0:0:0:0:0:0:0:1")) {
                ip = "127.0.0.1";
            }
        }
        if (ip != null && ip.length() > 0) {
            ip = ip.split(",")[0];
        }
        return ip;
    }


    /**
     * 获取本机外网IP.
     * 内网Ip可以使用InetAddress.getLocalHost().getHostAddress();
     *
     * @return 本机外网IP
     * @throws SocketException
     */
    private static String getRealIp() throws SocketException {
        if (REAL_IP != null) {
            return REAL_IP;
        }
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP

        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        boolean finded = false;// 是否找到外网IP
        while (netInterfaces.hasMoreElements() && !finded) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                ip = address.nextElement();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {// 外网IP
                    netip = ip.getHostAddress();
                    finded = true;
                    break;
                } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                        && !ip.getHostAddress().contains(":")) {// 内网IP
                    localip = ip.getHostAddress();
                }
            }
        }

        if (netip != null && !"".equals(netip)) {
            REAL_IP = netip;
            return netip;
        } else {
            return localip;
        }
    }

    /**
     * 获取当前程序tomcat容器端口号.
     *
     * @return 端口号
     */
    private static Integer getServerPort() {
        if (SERVER_PORT != null) {
            return SERVER_PORT;
        }
        File serverXml = new File(System.getProperty("catalina.home") + FILE_SEPARATOR + "conf" + FILE_SEPARATOR + "server.xml");
        Integer port;
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true); // never forget this!
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(serverXml);
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile
                    ("/Server/Service[@name='Catalina']/Connector[count(@scheme)=0]/@port[1]");
            String result = (String) expr.evaluate(doc, XPathConstants.STRING);
            port = result != null && result.length() > 0 ? Integer.valueOf(result) : null;
        } catch (Exception e) {
            port = null;
        }
        return port;
    }


}
