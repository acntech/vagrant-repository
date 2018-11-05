package no.acntech.common.model;

import java.time.ZonedDateTime;

public class ApiError {

    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private int status;
        private String error;
        private String message;
        private String path;

        private Builder() {
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public ApiError build() {
            ApiError apiError = new ApiError();
            apiError.timestamp = ZonedDateTime.now();
            apiError.error = this.error;
            apiError.status = this.status;
            apiError.message = this.message;
            apiError.path = this.path;
            return apiError;
        }
    }
}
