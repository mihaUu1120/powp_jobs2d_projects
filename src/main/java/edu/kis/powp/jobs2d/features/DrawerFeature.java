package edu.kis.powp.jobs2d.features;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.events.SelectClearPanelOptionListener;
import edu.kis.legacy.drawer.panel.DrawPanelController;

public class DrawerFeature implements IFeature {

    private static DrawPanelController drawerController;

    public DrawerFeature() {
    }

    @Override
    public void setup(Application application) {
        setupDrawerPlugin(application);
    }

    private static void setupDrawerPlugin(Application application) {
        SelectClearPanelOptionListener selectClearPanelOptionListener = new SelectClearPanelOptionListener();

        drawerController = new DrawPanelController();
        application.addComponentMenu(DrawPanelController.class, "Draw Panel", 0);
        application.addComponentMenuElement(DrawPanelController.class, "Clear Panel", selectClearPanelOptionListener);

        drawerController.initialize(application.getFreePanel());
    }

    public static DrawPanelController getDrawerController() {
        return drawerController;
    }

    @Override
    public String getName() {
        return "Drawer";
    }
}
