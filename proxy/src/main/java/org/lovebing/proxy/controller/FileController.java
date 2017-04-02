package org.lovebing.proxy.controller;

import org.lovebing.proxy.config.ProxyServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * @author lovebing Created on Apr 2, 2017
 */

@RestController
public class FileController {

    @Autowired
    private ProxyServerConfig proxyServerConfig;

    @GetMapping(value = "/file/download")
    public FileSystemResource download(@Param(value = "path") String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            return null;
        }
        return new FileSystemResource(path);
    }

    @GetMapping(value = "/file/root-certificate.cer")
    public FileSystemResource rootCertificate() {
        return new FileSystemResource(proxyServerConfig.getRootCertificatePath());
    }
}
