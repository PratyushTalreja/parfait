package io.pcp.parfait;

import io.pcp.parfait.MonitoringViewProperties;
import io.pcp.parfait.dxm.PcpMmvWriter;
import io.pcp.parfait.jmx.MonitoredMBeanAttributeFactory;
import io.pcp.parfait.DynamicMonitoringView;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.ConnectException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParfaitAgent {
    private static final Logger logger = Logger.getLogger(ParfaitAgent.class);

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
        PcpMmvWriter pMmvWriter;
        MonitorableRegistry mRegistry = new MonitorableRegistry();
        MonitoredMBeanAttributeFactory<?> mMBeanAttributeFactory = null;
        try {
        	parseJSON(spec);
        	for (int i = 0; i < spec.length; i++)
        	{
        		mMBeanAttributeFactory = new MonitoredMBeanAttributeFactory<>(spec[i].getName(), spec[i].getDescription(), spec[i].getmBeanName(), spec[i].getmBeanAttributeName(), spec[i].getmBeanCompositeDataItem());
		        mMBeanAttributeFactory.setMonitorableRegistry(mRegistry);
        	}
        	view = new DynamicMonitoringView((MonitoringView) mMBeanAttributeFactory);
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
    
	public static void parseJSON(Specification spec[])
    {
    	ObjectMapper mapper = new ObjectMapper();
    	int counter = 0;
		try {
			JsonNode root = mapper.readTree(new File("/home/pratyush/Desktop/jvm.config"));
			JsonNode metrics = root.path("metrics");
			for (JsonNode node : metrics) {
				String name = node.path("name").asText();
				String description = node.path("description").asText();
				String units = node.path("units").asText();
				String mBeanName = node.path("mBeanName").asText();
				String mBeanAttributeName = node.path("mBeanAttributeName").asText();
				String mBeanCompositeDataItem = node.path("mBeanCompositeDataItem").asText();
				spec[counter] = new Specification();
				spec[counter].setName(name);
				spec[counter].setDescription(description);
				spec[counter].setUnits(units);
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
