package no.acntech.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProviderForm {

    @NotNull
    private ProviderType name;
    @NotNull
    private Algorithm checksumType;
    @NotBlank
    @Size(max = 200)
    private String checksum;
    @Size(max = 200)
    private String url;

    public ProviderForm() {
    }

    public ProviderForm(ProviderType name, Algorithm checksumType, String checksum, String url) {
        this.name = name;
        this.checksumType = checksumType;
        this.checksum = checksum;
        this.url = url;
    }

    public ProviderType getName() {
        return name;
    }

    public void setName(ProviderType name) {
        this.name = name;
    }

    public Algorithm getChecksumType() {
        return checksumType;
    }

    public void setChecksumType(Algorithm checksumType) {
        this.checksumType = checksumType;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
