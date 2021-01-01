package net.cookbook.openjdk;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.tools.jmx.*;
import java.awt.*;
import java.util.LinkedList;
import javax.management.*;
import javax.swing.JPanel;
import org.openide.util.Exceptions;

public class SamplingGraphPanel extends JPanel implements Runnable {
    private static final int MAX_DATA_POINTS = 20;
    private static final int MAX_VALUE = 110;
    private static final int GAP = 30;
   
    private final LinkedList<Long> samples = new LinkedList<Long>();
   
    private final Application application;
    private Thread refreshThread;

    public SamplingGraphPanel(Application application) {
        this.application = application;
        this.setBackground(Color.black);
    }

    @Override
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        
      Graphics2D g2 = (Graphics2D)gr;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      final double xScale = ((double) getWidth() - 2 * GAP) / (samples.size() - 1);
      final double yScale = ((double) getHeight() - 2 * GAP) / (MAX_VALUE - 1);

      Stroke oldStroke = g2.getStroke();
      g2.setColor(Color.green);
      g2.setStroke(new BasicStroke(3f));
      for (int i = 0; i < samples.size()-1; ++i) {
         final int x1 = (int) (i * xScale + GAP);
         final int y1 = (int) ((MAX_VALUE - samples.get(i)) * yScale + GAP);
         final int x2 = (int) ((i+1) * xScale + GAP);
         final int y2 = (int) ((MAX_VALUE - samples.get(i+1)) * yScale + GAP);
         g2.drawLine(x1, y1, x2, y2);         
      }
    }
    
    public void start() {
       refreshThread = new Thread(this);
       refreshThread.start();
    }
    
    public void stop() {
        if ( refreshThread != null ) {
            refreshThread.interrupt();
            refreshThread = null;
        }
    }

    @Override
    public void run() {
        JmxModel jmx = JmxModelFactory.getJmxModelFor(application);
        MBeanServerConnection mbsc = null;
        if (jmx != null && jmx.getConnectionState() == JmxModel.ConnectionState.CONNECTED) {
            mbsc = jmx.getMBeanServerConnection();
        }
        
        try {
            while ( mbsc != null && !Thread.currentThread().isInterrupted() ) {
                if ( samples.size() == MAX_DATA_POINTS ) {
                    samples.remove();
                }
                Long val = (Long)mbsc.getAttribute(new ObjectName("org.openjdk.cookbook:type=SleepProbe"), "ActualSleepTime");
                samples.add(val);
                repaint();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }
}
