package no.acntech.common.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "acntech")
public class ApplicationProperties {

    @NotNull
    @Valid
    private Api api = new Api();
    @NotNull
    @Valid
    private Proxy proxy = new Proxy();
    @NotNull
    @Valid
    private File file = new File();

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Validated
    public static class Api {

        @NotBlank
        private String clientContextPath;

        public String getClientContextPath() {
            return clientContextPath;
        }

        public void setClientContextPath(String clientContextPath) {
            this.clientContextPath = clientContextPath;
        }
    }

    @Validated
    public static class Proxy {

        private String scheme = "http";
        private String host;
        private Integer port;

        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public boolean isSet() {
            return StringUtils.isNoneBlank(scheme) && StringUtils.isNoneBlank(host) && port != null;
        }
    }

    @Validated
    public static class File {

        @NotBlank
        private String uploadDir;
        @NotBlank
        private String defaultFileName;

        public String getUploadDir() {
            return uploadDir;
        }

        public void setUploadDir(String uploadDir) {
            this.uploadDir = uploadDir;
        }

        public String getDefaultFileName() {
            return defaultFileName;
        }

        public void setDefaultFileName(String defaultFileName) {
            this.defaultFileName = defaultFileName;
        }
    }
}
