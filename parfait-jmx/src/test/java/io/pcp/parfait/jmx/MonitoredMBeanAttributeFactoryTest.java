package io.pcp.parfait.jmx;

import junit.framework.TestCase;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import java.io.IOException;
import java.lang.management.ManagementFactory;

public class MonitoredMBeanAttributeFactoryTest extends TestCase {

    public void testCanMonitorCompositeDataItem() throws InstanceNotFoundException,
            IntrospectionException, ReflectionException, AttributeNotFoundException, MBeanException, IOException {
        MonitoredMBeanAttributeFactory<Long> f = new MonitoredMBeanAttributeFactory<Long>("aconex.free.memory",
                "", "java.lang:type=Memory", "HeapMemoryUsage", "max");
        assertEquals(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax(), f
                .getObject().get().longValue());
    }

    public void testCanMonitorStandardAttribute() throws InstanceNotFoundException,
            IntrospectionException, ReflectionException, AttributeNotFoundException, MBeanException, IOException {
        MonitoredMBeanAttributeFactory<Long> f = new MonitoredMBeanAttributeFactory<Long>(
                "aconex.system.startTime", "", "java.lang:type=Runtime", "StartTime", "");
        assertEquals(ManagementFactory.getRuntimeMXBean().getStartTime(), f.getObject().get().longValue());
    }
}
