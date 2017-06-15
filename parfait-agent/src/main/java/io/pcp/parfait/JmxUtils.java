package io.pcp.parfait;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.apache.log4j.Logger;

/**
 * A convenience mechanism for locating an MBeanServer.
 */
public abstract class JmxUtils {

    /**
     * Attempt to find a locally running <code>MBeanServer</code>. Fails if no
     * <code>MBeanServer</code> can be found.  If multiple servers are found,
     * simply returns the first one from the list.
     * @param agent the agent identifier of the MBeanServer to retrieve.
     * If this parameter is <code>null</code>, all registered MBeanServers are
     * considered.
     * @return the <code>MBeanServer</code> if any are found
     * @throws io.pcp.parfait.MBeanServerException
     * if no <code>MBeanServer</code> could be found
     * @see javax.management.MBeanServerFactory#findMBeanServer(String)
     */
    public static MBeanServer locateMBeanServer(String agent) throws MBeanServerException {
        List servers = MBeanServerFactory.findMBeanServer(agent);

        MBeanServer server = null;
        if (servers != null && servers.size() > 0) {
            server = (MBeanServer) servers.get(0);
        }

        if (server == null && agent == null) {
            // Attempt to load the PlatformMBeanServer.
            try {
                server = ManagementFactory.getPlatformMBeanServer();
            }
            catch (SecurityException ex) {
                throw new MBeanServerException("No MBeanServer found, " +
                        "and cannot obtain the Java platform MBeanServer", ex);
            }
        }

        if (server == null) {
            throw new MBeanServerException(
                    "Unable to locate an MBeanServer instance" +
                    (agent != null ? " with agent id [" + agent + "]" : ""));
        }

        return server;
    }

    public static MBeanServer locateMBeanServer() throws MBeanServerException {
        return locateMBeanServer(null);
    }
}
