package org.lovebing.proxy.controller;

import io.netty.handler.codec.http.HttpHeaders;
import org.lovebing.proxy.config.ProxyCacheConfig;
import org.lovebing.proxy.config.ProxyServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;

/**
 * @author lovebing Created on Apr 2, 2017
 */

@RestController
public class FileController {

    @Autowired
    private ProxyServerConfig proxyServerConfig;
    @Autowired
    private ProxyCacheConfig proxyCacheConfig;

    @GetMapping(value = "/file/download")
    public FileSystemResource download(@Param(value = "path") String path, HttpServletResponse response) {
        if (!path.startsWith(proxyCacheConfig.getCachePath())) {
            return null;
        }
        if (path.indexOf("..") > -1) {
            return null;
        }
        File file = new File(path);
        if (file.isDirectory()) {
            return null;
        }

        String[] info = path.split("/");
        String name = info[info.length - 1];
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/octet-stream");
        try {
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(name, "UTF-8"));
        }
        catch (Exception e) {
        }
        return new FileSystemResource(path);
    }

    @GetMapping(value = "/file/root-certificate.cer")
    public FileSystemResource rootCertificate(HttpServletResponse response) {
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/octet-stream");
        return new FileSystemResource(proxyServerConfig.getRootCertificatePath());
    }
}
