package cvs.com.asyncttaskvsreact.connection;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author IgorSteblii on 15.10.16.
 */
class RequestHeader {

    enum ContentType {

        X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded");

        private final String type;

        ContentType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    enum RequestType {

        POST,
        GET

    }

    enum SupportedCharset {

        UTF_8(StandardCharsets.UTF_8);

        private final static String CHARSET_PREFIX = "charset=";

        private final Charset charset;

        SupportedCharset(Charset charset) {
            this.charset = charset;
        }

        public String getCharset() {
            return CHARSET_PREFIX + charset.displayName();
        }

    }

}
