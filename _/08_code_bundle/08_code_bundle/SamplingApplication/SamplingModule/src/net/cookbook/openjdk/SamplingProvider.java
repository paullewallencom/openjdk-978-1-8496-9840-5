package net.cookbook.openjdk;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.ui.*;
import com.sun.tools.visualvm.tools.jmx.*;
import javax.management.*;
import org.openide.util.Exceptions;

public class SamplingProvider extends DataSourceViewProvider<Application> {
    private static DataSourceViewProvider instance = new SamplingProvider();

    @Override
    public boolean supportsViewFor(Application application) {
        boolean result = false;
        JmxModel jmx = JmxModelFactory.getJmxModelFor(application);
        if (jmx != null && jmx.getConnectionState() == JmxModel.ConnectionState.CONNECTED) {
            MBeanServerConnection mbsc = jmx.getMBeanServerConnection();
            if (mbsc != null) {
                try {
                    mbsc.getObjectInstance(new ObjectName("org.openjdk.cookbook:type=SleepProbe"));
                    result = true; // no exception - bean found
                }catch (InstanceNotFoundException e) {
                    // bean not found, ignore
                } catch (Exception e1) {
                    Exceptions.printStackTrace(e1);
                }
            }
        }
        return result;
    }

    @Override
    protected DataSourceView createView(Application application) {
        return new SamplingView(application);
    }

    static void initialize() {
        DataSourceViewsManager.sharedInstance().addViewProvider(instance, Application.class);
    }

    static void unregister() {
        DataSourceViewsManager.sharedInstance().removeViewProvider(instance);
    }  
}
