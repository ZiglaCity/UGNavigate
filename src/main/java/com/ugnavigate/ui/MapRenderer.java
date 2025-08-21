package com.ugnavigate.ui;

import com.ugnavigate.algorithms.dijkstra.Dijkstra;
import com.ugnavigate.models.Graph;
import com.ugnavigate.models.GraphNode;
import com.ugnavigate.models.Landmark;
import com.ugnavigate.ui.models.RouteRequest;
import com.ugnavigate.ui.services.RoutingService;
import com.ugnavigate.ui.services.MockRoutingService;
import com.ugnavigate.utils.LandmarkLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JavaFX-based map renderer with enhanced styling and interactivity.
 * Plots landmarks by lat/lon coordinates and highlights paths with smooth graphics.
 */
public class MapRenderer extends Application {

    private static Graph currentGraph;
    private static List<String> currentPath;
    private static String currentTitle = "UGNavigate Map";
    private static boolean launched = false;
    // UI resources
    private Image mapImage;
    private final double imageDisplayWidth = 800;

    // landmarks loaded from resources
    private List<Landmark> landmarksList;

    // interaction state
    private Landmark startLandmark = null;
    private Landmark endLandmark = null;
    private Canvas overlayCanvas;
    private RoutingService routingService = new MockRoutingService();
    // UI controls for distance calculation
    private Button calcButton;
    private Label distanceLabel;

    public static void render(Graph graph, List<String> path) {
        render(graph, path, "Route Visualization");
    }

    private Landmark createSyntheticLandmarkFromCanvas(double canvasX, double canvasY) {
        Landmark lm = new Landmark();
        lm.setName("UserPoint(" + (int)canvasX + "," + (int)canvasY + ")");
        // map canvas coords back to image coords proportionally
    double imageWidth = (mapImage != null) ? mapImage.getWidth() : imageDisplayWidth;
    double imageHeight = (mapImage != null) ? mapImage.getHeight() : (overlayCanvas != null ? overlayCanvas.getHeight() : imageDisplayWidth);
    double canvasW = overlayCanvas != null ? overlayCanvas.getWidth() : imageWidth;
    double canvasH = overlayCanvas != null ? overlayCanvas.getHeight() : imageHeight;
    double scaleX = imageWidth > 0 ? imageWidth / canvasW : 1.0;
    double scaleY = imageHeight > 0 ? imageHeight / canvasH : 1.0;
        double imgX = canvasX * scaleX;
        double imgY = canvasY * scaleY;
        lm.setX(imgX);
        lm.setY(imgY);
        // also set lon/lat fields for fallback mapping consistency
        lm.setLon(imgX);
        lm.setLat(imgY);
        return lm;
    }

    public static void render(Graph graph, List<String> path, String title) {
        if (graph == null) {
            System.out.println("Graph required for MapRenderer");
            return;
        }
        
        currentGraph = graph;
        currentPath = path;
        currentTitle = title;
        
        if (!launched) {
            launched = true;
            // Launch JavaFX application in a separate thread
            new Thread(() -> {
                try {
                    Application.launch(MapRenderer.class);
                } catch (IllegalStateException e) {
                    System.err.println("JavaFX runtime already initialized. Creating new window...");
                    Platform.runLater(() -> {
                        try {
                            new MapRenderer().start(new Stage());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }).start();
        } else {
            // If already launched, just create a new window
            Platform.runLater(() -> {
                try {
                    new MapRenderer().start(new Stage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle(currentTitle);
        
        BorderPane root = new BorderPane();
        
        // Create info panel
        VBox infoPanel = createInfoPanel();
        root.setTop(infoPanel);
        
        // Load map image from resources and set up ImageView with fallbacks
        try {
            InputStream is = MapRenderer.class.getResourceAsStream("/images/UG_map.png");
            if (is == null) {
                // try without leading slash
                is = MapRenderer.class.getResourceAsStream("images/UG_map.png");
            }
            if (is == null) {
                // try loading directly from project resources (useful when running from IDE)
                java.io.File f = new java.io.File("src/main/resources/images/UG_map.png");
                if (f.exists()) {
                    is = new java.io.FileInputStream(f);
                }
            }
            if (is != null) mapImage = new Image(is);
        } catch (Exception e) {
            System.err.println("Failed to load map image: " + e.getMessage());
        }

        ImageView imageView = new ImageView();
        if (mapImage != null) {
            imageView.setImage(mapImage);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(imageDisplayWidth);
        } else {
            // fallback: empty ImageView with preferred width
            imageView.setFitWidth(imageDisplayWidth);
        }

    // Overlay canvas for drawing pins and routes. We'll adjust size once image layout is known.
    double initialH = (mapImage != null) ? (mapImage.getHeight() * (imageDisplayWidth / Math.max(mapImage.getWidth(), 1))) : 600;
    overlayCanvas = new Canvas(imageDisplayWidth, initialH);

        // Load landmarks from resources (returns Map<String, Landmark>)
        try {
            var lmMap = LandmarkLoader.loadLandmarks("data/landmarks.json");
            landmarksList = lmMap.values().stream().collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Could not load landmarks.json: " + e.getMessage());
            landmarksList = List.of();
        }

        // handle mouse clicks on overlay canvas
        overlayCanvas.setOnMouseClicked(ev -> {
            if (ev.getButton() != MouseButton.PRIMARY) return;
            double clickX = ev.getX();
            double clickY = ev.getY();

            if (landmarksList == null) {
                landmarksList = List.of();
            }

            // Try to snap to a nearby landmark; if none within threshold, create a synthetic landmark at the clicked position
            Optional<Landmark> nearest = findNearestLandmark(clickX, clickY, landmarksList, overlayCanvas.getWidth(), overlayCanvas.getHeight());
            Landmark chosen;
            if (nearest.isPresent()) {
                chosen = nearest.get();
            } else {
                // create a synthetic landmark representing the user's clicked point
                chosen = createSyntheticLandmarkFromCanvas(clickX, clickY);
            }

            if (startLandmark == null) {
                startLandmark = chosen;
                drawPinsAndRoute();
                return;
            }

            if (endLandmark == null) {
                endLandmark = chosen;
                drawPinsAndRoute();
                // Integration placeholder: create RouteRequest and call routingService
                List<String> lmIds = List.of(startLandmark.getName(), endLandmark.getName());
                RouteRequest req = new RouteRequest(startLandmark.getName(), endLandmark.getName(), lmIds, "shortest");
                routingService.calculateRoutes(req);
                return;
            }

            // third click -> reset
            startLandmark = null;
            endLandmark = null;
            clearOverlay();
        });

    // Stack the imageView and canvas in a Pane
    javafx.scene.layout.StackPane stack = new javafx.scene.layout.StackPane(imageView, overlayCanvas);
    root.setCenter(stack);
        
        // Ensure canvas resizes to imageView's displayed size after layout
        imageView.boundsInParentProperty().addListener((obs, oldB, newB) -> {
            double newW = newB.getWidth();
            double newH = newB.getHeight();
            overlayCanvas.setWidth(newW);
            overlayCanvas.setHeight(newH);
            try { drawMap(overlayCanvas.getGraphicsContext2D(), currentGraph, currentPath); } catch (Exception ignored) {}
        });

        Scene scene = new Scene(root, 900, 700);
        // draw initial map with landmarks
        Platform.runLater(() -> {
            try {
                GraphicsContext gc2 = overlayCanvas.getGraphicsContext2D();
                drawMap(gc2, currentGraph, currentPath);
            } catch (Exception ignored) {}
        });
        
        stage.setScene(scene);
        stage.show();
    }

    private void clearOverlay() {
        if (overlayCanvas == null) return;
        GraphicsContext gc = overlayCanvas.getGraphicsContext2D();
    gc.clearRect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());
    // redraw base map landmarks so blue pins remain visible
    try { drawMap(gc, currentGraph, currentPath); } catch (Exception ignored) {}
    }

    private void drawPinsAndRoute() {
        if (overlayCanvas == null) return;
        GraphicsContext gc = overlayCanvas.getGraphicsContext2D();
    // redraw base map first so base blue pins are present
    try { drawMap(gc, currentGraph, currentPath); } catch (Exception ignored) {}

    // draw start pin (red)
        if (startLandmark != null) {
            // coordinates in landmark file are lat/lon or pixel x/y; mapping function will handle either.
            var startPos = mapLatLonToCanvas(startLandmark);
            gc.setFill(Color.RED);
            gc.fillOval(startPos[0]-6, startPos[1]-6, 12, 12);
            gc.setFill(Color.BLACK);
            gc.fillText("Start: " + (startLandmark.getName() != null ? startLandmark.getName() : startLandmark.getId()), startPos[0]+8, startPos[1]-8);
        }

        // draw end pin (green)
        if (endLandmark != null) {
            var endPos = mapLatLonToCanvas(endLandmark);
            gc.setFill(Color.LIMEGREEN);
            gc.fillOval(endPos[0]-6, endPos[1]-6, 12, 12);
            gc.setFill(Color.BLACK);
            gc.fillText("End: " + (endLandmark.getName() != null ? endLandmark.getName() : endLandmark.getId()), endPos[0]+8, endPos[1]-8);
        }

        // draw line if both set
        if (startLandmark != null && endLandmark != null) {
            var s = mapLatLonToCanvas(startLandmark);
            var e = mapLatLonToCanvas(endLandmark);
            gc.setStroke(Color.GREEN);
            gc.setLineWidth(3);
            gc.strokeLine(s[0], s[1], e[0], e[1]);
        }
    }

    /**
     * Map a Landmark's lat/lon to canvas coordinates using graph bounds if available.
     * Falls back to mapping using landmark lat/lon relative to min/max of loaded landmarks.
     */
    private double[] mapLatLonToCanvas(Landmark lm) {
        double w = overlayCanvas.getWidth();
        double h = overlayCanvas.getHeight();

        // If explicit pixel coordinates are available on the Landmark, map them proportionally to the canvas size
        if (lm.getX() != 0 || lm.getY() != 0) {
            // landmarks.json x/y are in image pixel coordinates for the source image width used in development.
            // We need to scale them to the current displayed canvas width/height.
            double imageWidth = (mapImage != null) ? mapImage.getWidth() : imageDisplayWidth;
            double imageHeight = (mapImage != null) ? mapImage.getHeight() : (h);
            double scaleX = w / imageWidth;
            double scaleY = h / imageHeight;
            double x = lm.getX() * scaleX;
            double y = lm.getY() * scaleY;
            return new double[]{x, y};
        }

        if (currentGraph != null) {
            double minLat = Double.POSITIVE_INFINITY, maxLat = Double.NEGATIVE_INFINITY,
                   minLon = Double.POSITIVE_INFINITY, maxLon = Double.NEGATIVE_INFINITY;
            for (GraphNode n : currentGraph.getAllNodes()) {
                Landmark l = n.getLandmark();
                if (l == null) continue;
                minLat = Math.min(minLat, l.getLat());
                maxLat = Math.max(maxLat, l.getLat());
                minLon = Math.min(minLon, l.getLon());
                maxLon = Math.max(maxLon, l.getLon());
            }
            if (minLat == Double.POSITIVE_INFINITY) { minLat = 0; maxLat = 1; minLon = 0; maxLon = 1; }

            double lonRange = maxLon - minLon + 1e-9;
            double latRange = maxLat - minLat + 1e-9;
            double padding = 20;

            double x = ((lm.getLon() - minLon) / lonRange) * (w - 2*padding) + padding;
            double y = (1 - (lm.getLat() - minLat) / latRange) * (h - 2*padding) + padding;
            return new double[]{x, y};
        }

        // fallback: direct proportional mapping using landmarksList
        double minLat = landmarksList.stream().mapToDouble(Landmark::getLat).min().orElse(0);
        double maxLat = landmarksList.stream().mapToDouble(Landmark::getLat).max().orElse(1);
        double minLon = landmarksList.stream().mapToDouble(Landmark::getLon).min().orElse(0);
        double maxLon = landmarksList.stream().mapToDouble(Landmark::getLon).max().orElse(1);
        double lonRange = maxLon - minLon + 1e-9;
        double latRange = maxLat - minLat + 1e-9;
        double padding = 20;
        double x = ((lm.getLon() - minLon) / lonRange) * (w - 2*padding) + padding;
        double y = (1 - (lm.getLat() - minLat) / latRange) * (h - 2*padding) + padding;
        return new double[]{x, y};
    }

    /**
     * Find nearest landmark to the clicked point in canvas coordinates.
     */
    private Optional<Landmark> findNearestLandmark(double clickX, double clickY, List<Landmark> landmarks, double canvasW, double canvasH) {
        if (landmarks == null || landmarks.isEmpty()) return Optional.empty();
        Landmark best = null;
        double bestDist = Double.POSITIVE_INFINITY;
        for (Landmark lm : landmarks) {
            double[] pos = mapLatLonToCanvas(lm);
            double dx = pos[0] - clickX;
            double dy = pos[1] - clickY;
            double d = Math.hypot(dx, dy);
            if (d < bestDist) {
                bestDist = d;
                best = lm;
            }
        }
        // simple threshold: only snap if within 40 pixels
        if (best != null && bestDist <= 40) return Optional.of(best);
        return Optional.empty();
    }

    private VBox createInfoPanel() {
        VBox infoPanel = new VBox(10);
        infoPanel.setPadding(new Insets(15));
        infoPanel.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
        
        Label titleLabel = new Label(currentTitle);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#495057"));
        
        // Add calculate controls
        calcButton = new Button("Calculate Distance");
        distanceLabel = new Label("Distance: N/A");
        calcButton.setOnAction(ae -> {
            if (startLandmark == null || endLandmark == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please select both start and end points first.");
                alert.showAndWait();
                return;
            }
            // find nearest graph nodes to the selected start/end positions
            double[] sPos = mapLatLonToCanvas(startLandmark);
            double[] ePos = mapLatLonToCanvas(endLandmark);
            GraphNode sNode = findNearestGraphNode(sPos[0], sPos[1]);
            GraphNode eNode = findNearestGraphNode(ePos[0], ePos[1]);
            if (sNode == null || eNode == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not map selected points to graph nodes.");
                alert.showAndWait();
                return;
            }
            try {
                Dijkstra dj = new Dijkstra(sNode, eNode);
                dj.solve();
                List<GraphNode> pathNodes = dj.getShortestPath();
                if (pathNodes == null || pathNodes.isEmpty()) {
                    distanceLabel.setText("No path found");
                    // draw direct line as fallback
                    GraphicsContext gc = overlayCanvas.getGraphicsContext2D();
                    gc.setStroke(Color.GREEN);
                    gc.setLineWidth(3);
                    gc.strokeLine(sPos[0], sPos[1], ePos[0], ePos[1]);
                } else {
                    // convert to ids for drawMap highlighting
                    List<String> ids = pathNodes.stream().map(GraphNode::getId).collect(Collectors.toList());
                    // redraw map with highlighted path
                    try { drawMap(overlayCanvas.getGraphicsContext2D(), currentGraph, ids); } catch (Exception ignored) {}
                    distanceLabel.setText(String.format("Distance: %.1f (graph units)", dj.getShortestDistance()));
                }
            } catch (Throwable t) {
                // Defensive fallback: if Dijkstra or graph wiring fails, draw direct line
                System.err.println("Dijkstra failed: " + t.getMessage());
                GraphicsContext gc = overlayCanvas.getGraphicsContext2D();
                gc.setStroke(Color.GREEN);
                gc.setLineWidth(3);
                gc.strokeLine(sPos[0], sPos[1], ePos[0], ePos[1]);
                distanceLabel.setText("Distance: (direct) " + String.format("%.1f", Math.hypot(sPos[0]-ePos[0], sPos[1]-ePos[1])));
            }
        });

        if (currentPath != null && !currentPath.isEmpty()) {
            Label pathInfo = new Label("Path: " + String.join(" → ", currentPath));
            pathInfo.setFont(Font.font("Arial", 12));
            pathInfo.setTextFill(Color.web("#6c757d"));
            pathInfo.setWrapText(true);
            infoPanel.getChildren().addAll(titleLabel, pathInfo, calcButton, distanceLabel);
        } else {
            Label noPathLabel = new Label("Graph visualization (no path highlighted)");
            noPathLabel.setFont(Font.font("Arial", 12));
            noPathLabel.setTextFill(Color.web("#6c757d"));
            infoPanel.getChildren().addAll(titleLabel, noPathLabel, calcButton, distanceLabel);
        }
        
        return infoPanel;
    }

    private void drawMap(GraphicsContext gc, Graph graph, List<String> path) {
        if (graph == null) return;
        
        // Calculate bounds
        double minLat = Double.POSITIVE_INFINITY, maxLat = Double.NEGATIVE_INFINITY,
               minLon = Double.POSITIVE_INFINITY, maxLon = Double.NEGATIVE_INFINITY;
        
        for (GraphNode n : graph.getAllNodes()) {
            Landmark lm = n.getLandmark();
            if (lm != null) {
                minLat = Math.min(minLat, lm.getLat());
                maxLat = Math.max(maxLat, lm.getLat());
                minLon = Math.min(minLon, lm.getLon());
                maxLon = Math.max(maxLon, lm.getLon());
            }
        }
        
        if (minLat == Double.POSITIVE_INFINITY) {
            minLat = 0; maxLat = 1; minLon = 0; maxLon = 1;
        }
        
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        
    // Clear overlay canvas
    gc.clearRect(0, 0, width, height);
    // Ensure the base map image is painted onto the canvas so overlays are always visible
    if (mapImage != null) {
        try {
            gc.drawImage(mapImage, 0, 0, width, height);
        } catch (Exception ignored) {
            // protective: if drawImage fails, proceed with overlays only
        }
    }
        
        // Helper functions for coordinate transformation
        
        // Draw edges first (underneath nodes) using canvas-mapped coordinates
        gc.setStroke(Color.web("#b0bec5"));
        gc.setLineWidth(1.5);
        for (GraphNode n : graph.getAllNodes()) {
            Landmark lm = n.getLandmark();
            if (lm == null) continue;
            double[] p1 = mapLatLonToCanvas(lm);
            for (var e : n.getNeighbors()) {
                Landmark to = e.getTo().getLandmark();
                if (to == null) continue;
                double[] p2 = mapLatLonToCanvas(to);
                gc.strokeLine(p1[0], p1[1], p2[0], p2[1]);
            }
        }
        
        // Draw path highlighting (use the unified canvas mapping helper so scaling is correct)
        if (path != null && path.size() > 1) {
            gc.setStroke(Color.web("#ff5722"));
            gc.setLineWidth(4);
            
            for (int i = 0; i < path.size() - 1; i++) {
                GraphNode a = graph.getNode(path.get(i));
                GraphNode b = graph.getNode(path.get(i + 1));
                if (a == null || b == null) continue;
                Landmark la = a.getLandmark();
                Landmark lb = b.getLandmark();
                if (la == null || lb == null) continue;
                double[] p1 = mapLatLonToCanvas(la);
                double[] p2 = mapLatLonToCanvas(lb);
                gc.strokeLine(p1[0], p1[1], p2[0], p2[1]);
            }
        }
        
        // Draw nodes
        for (GraphNode n : graph.getAllNodes()) {
            Landmark lm = n.getLandmark();
            if (lm == null) continue;
            
            double[] xy = mapLatLonToCanvas(lm);
            double x = xy[0];
            double y = xy[1];
            
            // Always draw a base blue pin for the landmark
            gc.setFill(Color.web("#2196f3"));
            gc.fillOval(x - 6, y - 6, 12, 12);
            gc.setFill(Color.WHITE);
            gc.fillOval(x - 3, y - 3, 6, 6);

            // If node is part of the highlighted path, overlay a distinct ring/marker
            boolean inPath = path != null && path.contains(n.getId());
            if (inPath) {
                gc.setStroke(Color.web("#ff5722"));
                gc.setLineWidth(2);
                gc.strokeOval(x - 10, y - 10, 20, 20);
            }
            
            // Draw node labels for path nodes or important landmarks
            if (inPath || (lm.getName() != null && lm.getName().length() < 20)) {
                gc.setFill(Color.web("#37474f"));
                gc.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
                String label = lm.getName() != null ? lm.getName() : n.getId();
                gc.fillText(label, x + 10, y - 5);
            }
        }
    }

    // Find the nearest graph node to a canvas position (used to map user-chosen points to graph nodes)
    private GraphNode findNearestGraphNode(double canvasX, double canvasY) {
        if (currentGraph == null) return null;
        GraphNode best = null;
        double bestDist = Double.POSITIVE_INFINITY;
        for (GraphNode n : currentGraph.getAllNodes()) {
            Landmark lm = n.getLandmark();
            if (lm == null) continue;
            double[] p = mapLatLonToCanvas(lm);
            double d = Math.hypot(p[0]-canvasX, p[1]-canvasY);
            if (d < bestDist) {
                bestDist = d;
                best = n;
            }
        }
        return best;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
