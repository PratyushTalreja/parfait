package io.pcp.parfait;

/**
 * General base exception to be thrown on JMX errors.
 */
@SuppressWarnings("serial")
public class JmxException extends RuntimeException {
    /**
     * Constructor for JmxException.
     * @param msg the detail message
     */
    public JmxException(String msg) {
        super(msg);
    }

    /**
     * Constructor for JmxException.
     * @param msg the detail message
     * @param cause the root cause (raw JMX API exception)
     */
    public JmxException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
