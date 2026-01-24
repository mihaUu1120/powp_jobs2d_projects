package edu.kis.powp.jobs2d.features;

import edu.kis.powp.appbase.Application;

import javax.swing.*;
import java.awt.*;

/**
 * Feature for managing view transformations (zoom and pan).
 * This feature wraps the drawing panel to apply transformations.
 * 
 * Panning: Hold CTRL + Right Mouse Button and drag
 */
public class ViewFeature {

    private static ViewTransformPanel controlPanel;
    private static JPanel transformedPanel;
    private static Application app;

    /**
     * Setup View Feature Plugin and add to application.
     * Must be called Before DrawerFeature and CanvasFeature.
     * 
     * @param application Application context.
     */
    public static void setupViewPlugin(Application application) {
        app = application;
        JPanel freePanel = app.getFreePanel();
        
        // Create a custom panel that will be transformed
        transformedPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                if (controlPanel == null) {
                    super.paint(g);
                    return;
                }
                
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Apply transformations
                double scale = controlPanel.getScale();
                double tx = controlPanel.getTranslateX();
                double ty = controlPanel.getTranslateY();
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                g2d.translate(centerX + tx, centerY + ty);
                g2d.scale(scale, scale);
                g2d.translate(-centerX, -centerY);
                
                // Draw large white background in transformed space behind everything
                g2d.setColor(Color.WHITE);
                g2d.fillRect(-10000, -10000, 20000, 20000);
                
                // Paint everything with transformation
                super.paint(g2d);
                g2d.dispose();
            }
        };
        transformedPanel.setOpaque(true);
        transformedPanel.setBackground(Color.WHITE);
        transformedPanel.setLayout(new OverlayLayout(transformedPanel));
        
        // Replace free panel content with our transformed panel
        freePanel.removeAll();
        freePanel.setLayout(new BorderLayout());
        freePanel.add(transformedPanel, BorderLayout.CENTER);
        
        // Create transparent control panel for mouse input
        controlPanel = new ViewTransformPanel();
        transformedPanel.add(controlPanel);
        
        // Add View menu
        app.addComponentMenu(ViewFeature.class, "View");
    }

    /**
     * Get the panel where drawing components should be added.
     * 
     * @return transformed panel.
     */
    public static JPanel getDrawingPanel() {
        return transformedPanel;
    }
    
    /**
     * Add a mouse listener that will receive left button events on the control panel.
     * This is used for drawing operations.
     */
    public static void addMouseListenerToControlPanel(java.awt.event.MouseListener listener) {
        if (controlPanel != null) {
            controlPanel.addDelegatedMouseListener(listener);
        }
    }

    /**
     * Zoom in the view.
     */
    public static void zoomIn() {
        if (controlPanel != null) {
            controlPanel.zoomIn();
        }
    }

    /**
     * Zoom out the view.
     */
    public static void zoomOut() {
        if (controlPanel != null) {
            controlPanel.zoomOut();
        }
    }

    /**
     * Reset the view to default.
     */
    public static void resetView() {
        if (controlPanel != null) {
            controlPanel.resetView();
        }
    }
}

