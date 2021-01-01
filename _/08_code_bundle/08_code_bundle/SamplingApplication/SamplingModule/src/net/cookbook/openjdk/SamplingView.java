package net.cookbook.openjdk;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent;
import java.awt.Graphics;
import javax.swing.*;
import org.openide.util.Utilities;

public class SamplingView extends DataSourceView {
    private DataViewComponent dvc;
    private SamplingGraphPanel panel;
    public static final String IMAGE_PATH = "net/cookbook/openjdk/icon.png";

    public SamplingView(Application application) {
        super(application,"Sampling Application", new ImageIcon(Utilities.loadImage(IMAGE_PATH, true)).getImage(), 60, false);
    }

    protected DataViewComponent createComponent() {
        //Data area for master view:
        JEditorPane generalDataArea = new JEditorPane();
        generalDataArea.setBorder(BorderFactory.createEmptyBorder(14, 8, 14, 8));

        panel = new SamplingGraphPanel((Application)getDataSource());
        
        DataViewComponent.MasterView masterView = new DataViewComponent.MasterView("Sampling Overview", null, generalDataArea);
        DataViewComponent.MasterViewConfiguration masterConfiguration = new DataViewComponent.MasterViewConfiguration(false);
        dvc = new DataViewComponent(masterView, masterConfiguration);

        //Add detail views to the component:
        dvc.addDetailsView(new DataViewComponent.DetailsView(
                "Sampling Graph", null, 10, panel, null), DataViewComponent.TOP_LEFT);
        
        panel.start();

        return dvc;

    }

    @Override
    protected void removed() {
        super.removed();
        panel.stop();
    }
}
