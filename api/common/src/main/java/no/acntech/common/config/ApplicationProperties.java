package no.acntech.common.config;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "acntech")
public class ApplicationProperties {

    @NotNull
    @Valid
    private File file = new File();

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Validated
    public static class File {

        @NotBlank
        private String uploadDir;
        @NotBlank
        private String defaultFileName;
        @NotBlank
        private String rootContextPath;

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

        public String getRootContextPath() {
            return rootContextPath;
        }

        public void setRootContextPath(String rootContextPath) {
            this.rootContextPath = rootContextPath;
        }
    }
}
