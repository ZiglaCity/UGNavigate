package com.ugnavigate.ui;

import com.ugnavigate.models.GraphNode;
import com.ugnavigate.models.Landmark;
import com.ugnavigate.algorithms.dijkstra.Dijkstra;
import com.ugnavigate.models.Graph;
import com.ugnavigate.models.Neighbor;
import com.ugnavigate.utils.GraphUtils;
import com.ugnavigate.utils.LandmarkLoader;
// import com.ugnavigate.algorithms.alternative.AlternativePathFinder;
// import com.ugnavigate.models.Route;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main UI frame: left controls + right canvas.
 * Wire your Dijkstra + Alternative path at the two TODOs below.
 */
public class UGNavigateUI extends JFrame {

    private final JTextField startField = new JTextField();
    private final JTextField destField  = new JTextField();
    private final JButton findBtn       = new JButton("Find Route");

    private final JTextArea resultArea  = new JTextArea(6, 30);
    private final AutocompletePopup startPopup = new AutocompletePopup();
    private final AutocompletePopup destPopup  = new AutocompletePopup();

    private final GraphCanvas canvas    = new GraphCanvas();

    // source of truth for suggestions (names only)
    private final List<String> landmarkNames;

    // map name -> Landmark object (lat/lon used for scaling & pins)
    private final Map<String, Landmark> landmarksByName;
    public Graph graph;
    public UGNavigateUI(List<Landmark> allLandmarks) {
        super("UGNavigate — Campus Path Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        String filePath = "data/adjacency_list.json";
        Map<String, List<Neighbor>> adjacencyList = GraphUtils.loadGraph(filePath);
//        System.out.println("=== UG Navigate Graph ===");
//        for (Map.Entry<String, List<Neighbor>> entry : adjacencyList.entrySet()) {
//            System.out.println(entry.getKey() + " ->");
//            for (Neighbor neighbor : entry.getValue()) {
//                System.out.println("    " + neighbor);
//            }
//        }

        filePath = "data/landmarks.json";
        Map<String, Landmark> landmarks = LandmarkLoader.loadLandmarks(filePath);
        System.out.println(landmarks);

        this.graph = new Graph(landmarks, adjacencyList);

        // prepare landmark lookup
        this.landmarkNames = allLandmarks.stream()
                .map(Landmark::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
        this.landmarksByName = new HashMap<>();
        for (Landmark lm : allLandmarks) {
            landmarksByName.put(lm.getName(), lm);
            System.out.println(lm.getLat() + " " + lm.getLon());
        }

        // canvas receives full landmark list to compute lat/lon bounds & layout
        canvas.setLandmarks(allLandmarks);

        // left controls
        JPanel left = buildControlsPanel();
        add(left, BorderLayout.WEST);

        // right canvas
        add(new JScrollPane(canvas), BorderLayout.CENTER);

        // suggestions
        hookAutocomplete(startField, startPopup);
        hookAutocomplete(destField, destPopup);

        // action
        findBtn.addActionListener(e -> onFindRoute());

        // a bit of polish
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        setSize(1100, 720);
        setLocationRelativeTo(null);
    }

    private JPanel buildControlsPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(360, 720));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 12, 8, 12);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;

        JLabel title = new JLabel("UGNavigate");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        c.gridy = 0; panel.add(title, c);

        c.gridy = 1; panel.add(new JLabel("Start location"), c);
        c.gridy = 2; panel.add(startField, c);

        c.gridy = 3; panel.add(new JLabel("Destination"), c);
        c.gridy = 4; panel.add(destField, c);

        c.gridy = 5; panel.add(findBtn, c);

        c.gridy = 6;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        panel.add(new JScrollPane(resultArea), c);

        return panel;
    }

    private void hookAutocomplete(JTextField field, AutocompletePopup popup) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { show(); }
            @Override public void removeUpdate(DocumentEvent e) { show(); }
            @Override public void changedUpdate(DocumentEvent e) { show(); }
            private void show() {
                String q = field.getText().trim();
                if (q.isEmpty()) {
                    popup.hide(field);
                    return;
                }
                List<String> matches = fuzzyPrefix(landmarkNames, q, 12);
                System.out.println("Matches...: " + matches);
                if (matches.isEmpty()) {
                    popup.hide(field);
                } else {
                    popup.show(field, matches, chosen -> {
                        field.setText(chosen);
                        popup.hide(field);
                    System.out.println("Tried to show popup: " + matches);
                    });
                }
            }
        });
        System.out.println("Fields: " + field);
        field.addFocusListener(popup.getFocusHider(field));
    }

    // simple, fast, case-insensitive prefix+contains matcher
    private static List<String> fuzzyPrefix(List<String> items, String query, int limit) {
        final String q = query.toLowerCase(Locale.ROOT);
        List<String> starts = items.stream()
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(q))
                .limit(limit)
                .collect(Collectors.toList());
        if (starts.size() < limit) {
            List<String> contains = items.stream()
                    .filter(s -> !starts.contains(s))
                    .filter(s -> s.toLowerCase(Locale.ROOT).contains(q))
                    .limit(limit - starts.size())
                    .collect(Collectors.toList());
            starts.addAll(contains);
        }
        return starts;
    }

    private void onFindRoute() {
        String startName = startField.getText().trim();
        String destName  = destField.getText().trim();

        // validate inputs
        Landmark start = landmarksByName.get(startName);
        Landmark dest  = landmarksByName.get(destName);

        if (start == null || dest == null) {
            StringBuilder sb = new StringBuilder("Invalid input:\n");
            if (start == null) sb.append(" • Start not recognized. Try selecting from suggestions.\n");
            if (dest  == null) sb.append(" • Destination not recognized. Try selecting from suggestions.\n");
            JOptionPane.showMessageDialog(this, sb.toString(), "Invalid location", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // === TODO: call your real shortest path ===
            // Dijkstra d = new Dijkstra(graph.getNode(start), graph.getNode(dest));
            // d.solveUsingDijkstra();
            // List<GraphNode> shortestNodes = d.getShortestPath();
            // double distMeters = d.getShortestDistance();
            // double etaMinutes = TimeUtils.estimateMinutes(distMeters, trafficWeight);

            // For UI wiring demo, convert via landmark names:
            List<GraphNode> shortestPath = requestShortestPath(startName, destName);

            System.out.println("Shortest path in finding the route: " + shortestPath);

            // === TODO: call your real alternative path ===
            List<GraphNode> alternativePath = requestAlternativePath(startName, destName);

            // update canvas overlays
            canvas.setPaths(shortestPath, alternativePath);

            // update result panel
            StringBuilder out = new StringBuilder();
            out.append("Shortest path:\n  ")
                    .append(shortestPath.stream().map(GraphNode::getName).collect(Collectors.joining(" → ")))
                    .append("\n");

            if (alternativePath != null && !alternativePath.isEmpty()) {
                out.append("\nAlternative path:\n  ")
                        .append(alternativePath.stream().map(GraphNode::getName).collect(Collectors.joining(" → ")))
                        .append("\n");
            }

            // If you have distance/time ready here, append:
            // out.append(String.format("\nDistance: %.0f m   ETA: %.1f min\n", distMeters, etaMinutes));

            resultArea.setText(out.toString());
            canvas.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to compute route.\n" + ex.getMessage(),
                    "Routing error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== MOCK HOOKS (replace with your Dijkstra / alternative calls) =====
    private List<GraphNode> requestShortestPath(String startName, String destName) {
        // replace with: Dijkstra -> List<GraphNode> -> map to List<Landmark>
        // For now: straight-line 2-point list just to demonstrate UI wiring
        System.out.println("Start name gotten : " + startName);
        System.out.println("Destination name gotten: " + destName);
//        GraphNode start = new GraphNode(startName);
//        GraphNode destination = new GraphNode(destName);

        String filePath = "data/adjacency_list.json";
        Map<String, List<Neighbor>> adjacencyList = GraphUtils.loadGraph(filePath);

        filePath = "data/landmarks.json";
        Map<String, Landmark> landmarks = LandmarkLoader.loadLandmarks(filePath);
        System.out.println(landmarks);

        Graph graph = new Graph(landmarks, adjacencyList);

        GraphNode start = graph.getNode(startName);
        GraphNode destination = graph.getNode(destName);

        if(start == null || destination == null){
            System.out.println("One of the nodes invalid");
        }
        System.out.println(start.getName());
        System.out.println(destination.getName());

        if (!graph.getAllNodes().contains(start) || !graph.getAllNodes().contains(destination)) {
            throw new IllegalArgumentException("Invalid path provided! One or both nodes are not in the graph.");
        }

        Dijkstra dj = new Dijkstra(start, destination);
        dj.solve();

        System.out.println("shortest distance: " + dj.getShortestDistance());
        System.out.println(dj.getShortestPath());
        System.out.println("Path summary: " + dj.getPathSummary());
        return dj.getShortestPath();
//        return Arrays.asList(landmarksByName.get(startName), landmarksByName.get(destName));
    }
    private List<GraphNode> requestAlternativePath(String startName, String destName) {
        // replace with: AlternativePathFinder edge-removal trick
        return Collections.emptyList();
    }
    // ======================================================================

    // entry-point convenience for testing UI
    public static void launchWithLandmarks(List<Landmark> allLandmarks) {
        SwingUtilities.invokeLater(() -> {
            UGNavigateUI ui = new UGNavigateUI(allLandmarks);
            ui.setVisible(true);
        });
    }
}
