package io.pcp.parfait;

/**
 * Exception thrown when we cannot locate an instance of an {@code MBeanServer}
 */
@SuppressWarnings("serial")
public class MBeanServerException extends JmxException {
    /**
     * Create a new {@code MBeanServerException} with the given error message.
     * @param msg the error message
     */
    public MBeanServerException(String msg) {
        super(msg);
    }

    /**
     * Create a new {@code MBeanServerException} with the
     * given error message and root cause.
     * @param msg the error message
     * @param cause the root cause
     */
    public MBeanServerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}