package no.acntech.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "acntech.storage")
public class StorageProperties {

    @NotBlank
    private String fileName;
    @NotBlank
    private String uploadPath;
    @NotNull
    private Boolean overwriteExistingFiles;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public Boolean getOverwriteExistingFiles() {
        return overwriteExistingFiles;
    }

    public void setOverwriteExistingFiles(Boolean overwriteExistingFiles) {
        this.overwriteExistingFiles = overwriteExistingFiles;
    }
}
