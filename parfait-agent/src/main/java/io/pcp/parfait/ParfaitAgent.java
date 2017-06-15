package io.pcp.parfait;

import io.pcp.parfait.MonitorableRegistry;
import io.pcp.parfait.MonitoringViewProperties;
import io.pcp.parfait.dxm.PcpMmvWriter;
import io.pcp.parfait.DynamicMonitoringView;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.ConnectException;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.measure.Unit;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParfaitAgent {
    private static final Logger logger = Logger.getLogger(ParfaitAgent.class);
    private static MonitorableRegistry registry = MonitorableRegistry.DEFAULT_REGISTRY;

    // find the root cause of an exception, for nested BeansException case
    public static Throwable getCause(Throwable e) {
        Throwable cause = null; 
        Throwable result = e;
        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }

    // extract properties from arguments, properties files, or intuition
    public static void setupProperties(String propertyAndValue, String separator) {
        String[] tokens = propertyAndValue.split(separator, 2);
        if (tokens.length == 2) {
            String name = MonitoringViewProperties.PARFAIT + "." + tokens[0];
            String value = tokens[1];
            System.setProperty(name, value);
        }
    }

    public static void startLocal() {
    	Specification spec[] = null;
        DynamicMonitoringView view;
        PcpMmvWriter pcpMmvWriter;
		try {
            parseJSON(spec);
               for (int i = 0; i < spec.length; i++) {
            	   Monitorable<?> monitorable = spec[i].createMonitorable();
            	   registry.register(monitorable);
            }
            view = new DynamicMonitoringView((MonitoringView) registry);
            view.start();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static void setupPreMainArguments(String arguments) {
        for (String propertyAndValue: arguments.split(",")) {
            setupProperties(propertyAndValue, ":");
        }
    }

    public static void premain(String arguments, Instrumentation instruments) {
        MonitoringViewProperties.setupProperties();
        if (arguments != null) {
            setupPreMainArguments(arguments);
        }
        String name = System.getProperty(MonitoringViewProperties.PARFAIT_NAME);
        logger.info(String.format("Starting Parfait agent [%s]", name));
        startLocal();
    }

    public static void startProxy(String jmx) {
        DynamicMonitoringView view = null;
        
        try {
            view.start();
            Thread.currentThread().join();    // pause the main proxy thread
        } catch (Exception e) {
            String m = "Stopping Parfait proxy";  // pretty-print some errors
            if (getCause(e) instanceof ConnectException) {
                logger.error(String.format("%s, cannot connect to %s", m, jmx));
            } else if (e instanceof /*BeansException*/ Exception) {
                logger.error(String.format("%s, cannot setup beans", m), e);
            } else if (e instanceof InterruptedException) {
                logger.error(String.format("%s, interrupted", m));
            } else {
                logger.error(m, e);
            }
        }
    }

    public static void setupMainArguments(String[] arguments) {
        for (String propertyAndValue: arguments) {
            if (propertyAndValue.startsWith("-"))
                propertyAndValue = propertyAndValue.substring(1);
            setupProperties(propertyAndValue, "=");
        }
    }
    
	public static void parseJSON(Specification spec[]) throws MalformedObjectNameException
    {
        ObjectMapper mapper = new ObjectMapper();
        int counter = 0;
        try {
            JsonNode root = mapper.readTree(new File("/etc/pcp/parfait/jvm.config"));
            JsonNode metrics = root.path("metrics");
            for (JsonNode node : metrics) {
                String name = node.path("name").asText();
                String description = node.path("description").asText();
                String semantics = node.path("semantics").asText();
                Unit<?> units = (Unit<?>) node.path("units");
                ObjectName mBeanName = new ObjectName(node.path("mBeanName").asText());
                String mBeanAttributeName = node.path("mBeanAttributeName").asText();
                String mBeanCompositeDataItem = node.path("mBeanCompositeDataItem").asText();
                spec[counter] = new Specification();
                spec[counter].setName(name);
                spec[counter].setDescription(description);
                spec[counter].setUnits(units);
                if (semantics.equalsIgnoreCase("constant"))
                	spec[counter].setSemantics(ValueSemantics.CONSTANT);
                else if (semantics.equalsIgnoreCase("counter"))
                	spec[counter].setSemantics(ValueSemantics.FREE_RUNNING);
                else
                	spec[counter].setSemantics(ValueSemantics.MONOTONICALLY_INCREASING);
                spec[counter].setmBeanName(mBeanName);
                spec[counter].setmBeanAttributeName(mBeanAttributeName);
                spec[counter].setmBeanCompositeDataItem(mBeanCompositeDataItem);
                counter++;
            }
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] arguments) {
        MonitoringViewProperties.setupProperties();
        setupMainArguments(arguments);
        String name = System.getProperty(MonitoringViewProperties.PARFAIT_NAME);
        String c = System.getProperty(MonitoringViewProperties.PARFAIT_CONNECT);
        logger.info(String.format("Starting Parfait proxy [%s %s]", name, c));
        startProxy(c);
    }
}
