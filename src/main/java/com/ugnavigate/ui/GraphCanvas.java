package com.ugnavigate.ui;

import com.ugnavigate.models.GraphNode;
import com.ugnavigate.models.Landmark;

import javax.swing.*;
        import java.awt.*;
        import java.awt.geom.Ellipse2D;
import java.util.List;

/**
 * Paints landmarks (scaled from lat/lon), labels, and overlays routes.
 */
public class GraphCanvas extends JPanel {

    private List<Landmark> allLandmarks;
    private List<GraphNode> shortestPath;     // solid
    private List<GraphNode> alternativePath;  // dashed

    // lat/lon bounds for scaling
    private double minLat, maxLat, minLon, maxLon;

    public GraphCanvas() {
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    public void setLandmarks(List<Landmark> landmarks) {
        this.allLandmarks = landmarks;
        computeBounds();
        revalidate();
        repaint();
    }

    public void setPaths(List<GraphNode> shortest, List<GraphNode> alternative) {
        this.shortestPath = shortest;
        this.alternativePath = alternative;
    }

    private void computeBounds() {
        if (allLandmarks == null || allLandmarks.isEmpty()) return;
        minLat = allLandmarks.stream().mapToDouble(Landmark::getLat).min().orElse(0);
        maxLat = allLandmarks.stream().mapToDouble(Landmark::getLat).max().orElse(1);
        minLon = allLandmarks.stream().mapToDouble(Landmark::getLon).min().orElse(0);
        maxLon = allLandmarks.stream().mapToDouble(Landmark::getLon).max().orElse(1);

        System.out.println("Min lat: " + minLat + " Max lat: " + maxLat);
        System.out.println("Min lon: "+ minLon + " Max lon " + maxLon);

        // small padding to avoid zero division
//        if (Math.abs(maxLat - minLat) < 1e-9) { maxLat += 1e-6; minLat -= 1e-6; }
//        if (Math.abs(maxLon - minLon) < 1e-9) { maxLon += 1e-6; minLon -= 1e-6; }
    }

    // lat/lon -> x/y (with margin and Y flip so north is up)
    private Point toCanvas(double lat, double lon, int width, int height) {
        int margin = 40;
        double nx = (lon - minLon) / (maxLon - minLon);
        double ny = (lat - minLat) / (maxLat - minLat);
//        int x = margin + (int)Math.round(nx * (width  - 2*margin));
//        int y = margin + (int)Math.round((1 - ny) * (height - 2*margin));
        // Convert longitude → X
        int x = (int) (( (lon - minLon) / (maxLon - minLon) ) * (width - 100) + 50);

// Convert latitude → Y
        int y = (int) (( (maxLat - lat) / (maxLat - minLat) ) * (height - 100) + 50);

        return new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (allLandmarks == null || allLandmarks.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1) draw all landmarks (nodes) with labels
        g2.setFont(getFont().deriveFont(12f));
        for (Landmark lm : allLandmarks) {
            Point p = toCanvas(lm.getLat(), lm.getLon(), w, h);
            drawNodeWithLabel(g2, p, lm.getName());
        }

        // 2) draw paths (after nodes so they’re on top)
        if (alternativePath != null && alternativePath.size() >= 2) {
            drawPath(g2, alternativePath, w, h, /*dashed=*/true);
        }
        if (shortestPath != null && shortestPath.size() >= 2) {
            drawPath(g2, shortestPath, w, h, /*dashed=*/false);
        }

        g2.dispose();
    }

    private void drawNodeWithLabel(Graphics2D g2, Point p, String name) {
        int r = 6;
        Shape dot = new Ellipse2D.Double(p.x - r, p.y - r, 2*r, 2*r);

        // node
        g2.setColor(new Color(60, 60, 60));
        g2.fill(dot);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(dot);

//        // label
        g2.setColor(new Color(25, 25, 25));
        int labelOffsetX = 10;
        int labelOffsetY = -8;
        g2.drawString(name, p.x + labelOffsetX, p.y + labelOffsetY);
    }

    private void drawPath(Graphics2D g2, List<GraphNode> path, int w, int h, boolean dashed) {
        Stroke prev = g2.getStroke();
        if (dashed) {
            float[] dash = {8f, 8f};
            g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, dash, 0f));
            g2.setColor(new Color(46, 125, 50)); // green-ish
        } else {
            g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(25, 118, 210)); // blue-ish
        }

        Point prevP = toCanvas(path.get(0).getLandmark().getLat(), path.get(0).getLandmark().getLon(), w, h);
        for (int i = 1; i < path.size(); i++) {
            Landmark cur = path.get(i).getLandmark();
            Point curP = toCanvas(cur.getLat(), cur.getLon(), w, h);
            g2.drawLine(prevP.x, prevP.y, curP.x, curP.y);
            prevP = curP;
        }

        g2.setStroke(prev);
    }
}
