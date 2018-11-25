package no.acntech.common.model;

public class ProviderFile {

    private String fileName;
    private long fileSize;
    private ChecksumType checksumType;
    private String checksum;

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public ChecksumType getChecksumType() {
        return checksumType;
    }

    public String getChecksum() {
        return checksum;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String fileName;
        private long fileSize;
        private ChecksumType checksumType;
        private String checksum;

        private Builder() {
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder fileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder checksumType(ChecksumType checksumType) {
            this.checksumType = checksumType;
            return this;
        }

        public Builder checksum(String checksum) {
            this.checksum = checksum;
            return this;
        }

        public ProviderFile build() {
            ProviderFile providerFile = new ProviderFile();
            providerFile.fileName = this.fileName;
            providerFile.checksumType = this.checksumType;
            providerFile.fileSize = this.fileSize;
            providerFile.checksum = this.checksum;
            return providerFile;
        }
    }
}
