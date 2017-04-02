package org.lovebing.proxy.config;

import net.lightbody.bmp.mitm.CertificateAndKey;
import net.lightbody.bmp.mitm.CertificateInfo;
import net.lightbody.bmp.mitm.RootCertificateGenerator;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import net.lightbody.bmp.mitm.tools.DefaultSecurityProviderTool;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @author lovebing Created on Apr 22, 2017
 */
@Component
@ConfigurationProperties(prefix = "proxyServer")
public class ProxyServerConfig {
    private Integer port;
    private String rootCertificatePath;
    private String privateKeyPath;
    private String rootCertificateAndKeyPath;
    private String passwordForPrivateKey;
    private String keyStoreType;
    private String privateKeyAlias;

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public void setRootCertificateAndKeyPath(String rootCertificateAndKeyPath) {
        this.rootCertificateAndKeyPath = rootCertificateAndKeyPath;
    }

    public void setRootCertificatePath(String rootCertificatePath) {
        this.rootCertificatePath = rootCertificatePath;
    }

    public void setPasswordForPrivateKey(String passwordForPrivateKey) {
        this.passwordForPrivateKey = passwordForPrivateKey;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public void setPrivateKeyAlias(String privateKeyAlias) {
        this.privateKeyAlias = privateKeyAlias;
    }

    public Integer getPort() {
        return port;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public String getRootCertificateAndKeyPath() {
        return rootCertificateAndKeyPath;
    }

    public String getRootCertificatePath() {
        return rootCertificatePath;
    }

    public String getPasswordForPrivateKey() {
        return passwordForPrivateKey;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public String getPrivateKeyAlias() {
        return privateKeyAlias;
    }

    @Bean
    ImpersonatingMitmManager createImpersonatingMitmManager() {
        RootCertificateGenerator rootCertificateGenerator = RootCertificateGenerator.builder().build();
        rootCertificateGenerator.saveRootCertificateAsPemFile(new File(getRootCertificatePath()));
        rootCertificateGenerator.savePrivateKeyAsPemFile(new File(getPrivateKeyPath()), getPasswordForPrivateKey());
        rootCertificateGenerator.saveRootCertificateAndKey(getKeyStoreType(), new File(getRootCertificateAndKeyPath()), getPrivateKeyAlias(), getPasswordForPrivateKey());

        return ImpersonatingMitmManager.builder()
                .rootCertificateSource(rootCertificateGenerator)
                .build();
    }
}

