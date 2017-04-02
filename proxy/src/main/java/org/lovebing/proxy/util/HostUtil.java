package org.lovebing.proxy.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author lovebing Created on Apr 2, 2017
 */
public class HostUtil {

    public static String getHostAddress() throws UnknownHostException, SocketException {
        Enumeration<NetworkInterface> netInterfaces;
        netInterfaces = NetworkInterface.getNetworkInterfaces();
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> ips = ni.getInetAddresses();
            while (ips.hasMoreElements()) {
                InetAddress ip = ips.nextElement();
                if (ip.isSiteLocalAddress()) {
                    return ip.getHostAddress();
                }
            }
        }
        return InetAddress.getLocalHost().getHostAddress();
    }
}
