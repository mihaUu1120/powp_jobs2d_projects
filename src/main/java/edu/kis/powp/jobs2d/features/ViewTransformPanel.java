package edu.kis.powp.jobs2d.features;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseListener;

/**
 * Transparent overlay panel for view control (zoom and pan).
 * Handles mouse input for panning and stores transformation state.
 * Delegates left button and right button clicks to registered listeners with transformed coordinates.
 */
public class ViewTransformPanel extends JPanel {

    private double scale = 1.0;
    private double translateX = 0.0;
    private double translateY = 0.0;
    
    private Point lastMousePosition;
    private boolean isPanning = false;
    
    private static final double ZOOM_FACTOR = 1.2;
    private static final double MIN_SCALE = 0.1;
    private static final double MAX_SCALE = 10.0;
    
    private List<MouseListener> delegatedListeners = new ArrayList<>();

    public ViewTransformPanel() {
        setOpaque(false);
        setupMouseListeners();
    }
    
    /**
     * Add a mouse listener that will receive mouse events with transformed coordinates.
     */
    public void addDelegatedMouseListener(MouseListener listener) {
        delegatedListeners.add(listener);
    }
    
    /**
     * Transform screen coordinates to drawing space coordinates.
     */
    private Point2D transformPoint(Point screenPoint) {
        try {
            Container parent = getParent();
            if (parent == null) return screenPoint;
            
            int centerX = parent.getWidth() / 2;
            int centerY = parent.getHeight() / 2;
            
            AffineTransform transform = new AffineTransform();
            transform.translate(centerX + translateX, centerY + translateY);
            transform.scale(scale, scale);
            transform.translate(-centerX, -centerY);
            
            AffineTransform inverse = transform.createInverse();
            Point2D transformed = new Point2D.Double();
            inverse.transform(screenPoint, transformed);
            
            return transformed;
        } catch (NoninvertibleTransformException e) {
            return screenPoint;
        }
    }
    
    /**
     * Create a transformed mouse event with coordinates in drawing space.
     */
    private MouseEvent transformMouseEvent(MouseEvent e) {
        Point2D transformed = transformPoint(e.getPoint());
        
        return new MouseEvent(
            e.getComponent(),
            e.getID(),
            e.getWhen(),
            e.getModifiersEx(),
            (int)transformed.getX(),
            (int)transformed.getY(),
            e.getClickCount(),
            e.isPopupTrigger(),
            e.getButton()
        );
    }

    /**
     * Setup mouse listeners for panning with right mouse button + CTRL.
     * Left button and right button (without CTRL) events are delegated to registered listeners.
     */
    private void setupMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && e.isControlDown()) {
                    isPanning = true;
                    lastMousePosition = e.getPoint();
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && isPanning) {
                    isPanning = false;
                    lastMousePosition = null;
                    setCursor(Cursor.getDefaultCursor());
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Delegate clicks to registered listeners (unless it was a pan operation)
                if (!isPanning) {
                    MouseEvent transformedEvent = transformMouseEvent(e);
                    for (MouseListener listener : delegatedListeners) {
                        listener.mouseClicked(transformedEvent);
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isPanning && lastMousePosition != null) {
                    Point currentPosition = e.getPoint();
                    double dx = currentPosition.x - lastMousePosition.x;
                    double dy = currentPosition.y - lastMousePosition.y;
                    
                    translateX += dx;
                    translateY += dy;
                    
                    lastMousePosition = currentPosition;
                    
                    // Trigger repaint of parent
                    Container parent = getParent();
                    if (parent != null) {
                        parent.repaint();
                    }
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    /**
     * Zoom in the view.
     */
    public void zoomIn() {
        double newScale = scale * ZOOM_FACTOR;
        if (newScale <= MAX_SCALE) {
            scale = newScale;
            Container parent = getParent();
            if (parent != null) {
                parent.repaint();
            }
        }
    }

    /**
     * Zoom out the view.
     */
    public void zoomOut() {
        double newScale = scale / ZOOM_FACTOR;
        if (newScale >= MIN_SCALE) {
            scale = newScale;
            Container parent = getParent();
            if (parent != null) {
                parent.repaint();
            }
        }
    }

    /**
     * Reset view to default (scale=1.0, no translation).
     */
    public void resetView() {
        scale = 1.0;
        translateX = 0.0;
        translateY = 0.0;
        Container parent = getParent();
        if (parent != null) {
            parent.repaint();
        }
    }

    /**
     * Get current scale.
     * 
     * @return current scale factor.
     */
    public double getScale() {
        return scale;
    }

    /**
     * Get current X translation.
     * 
     * @return current X translation.
     */
    public double getTranslateX() {
        return translateX;
    }

    /**
     * Get current Y translation.
     * 
     * @return current Y translation.
     */
    public double getTranslateY() {
        return translateY;
    }
}

