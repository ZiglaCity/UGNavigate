UGNavigate – UI Developer Guide (Final)

This document is for frontend/UI developers building the UGNavigate desktop app (JavaFX).
It explains how the UI connects to the algorithms provided by the backend team and outlines implementation details for a smooth workflow.

🎯 Responsibilities of the UI

Provide input fields for Start Location and Destination.

Allow two ways to select locations:

Dropdown/autocomplete (predefined list of landmarks).

Tap-to-pin on the campus map (red pin for Start, red pin for End).

Optional landmark filters (checkbox list of banks, libraries, halls, etc.).

Display multiple route results (min 3):

Distance (km)

Estimated time (min)

Route path (landmarks sequence)

Render routes visually on a static UG campus map (with JavaFX overlays).

Highlight the “best route” but allow user to click/view alternatives.

Show alerts/errors when invalid inputs are provided.

⚠️ The UI does not implement algorithms — only calls the services provided by the algorithm team.

🛠️ Project Structure (UI Side)
/ui
  /controllers
    RouteSearchController.java   // Input screen
    RouteResultsController.java  // Results screen
  /services
    RoutingService.java          // Interface for algorithm calls
    MockRoutingService.java      // Temporary dummy implementation
  /models
    RouteRequest.java
    RouteResult.java
  /views
    route_search.fxml
    route_results.fxml
  /utils
    MapRenderer.java             // Handles drawing on map

🔗 Connecting to Core Algorithms

The algorithm team exposes pathfinding methods (e.g., Dijkstra.findShortestPath).
UI developers should not directly call algorithm classes — instead use RoutingService.

Example Interface
public interface RoutingService {
    List<RouteResult> calculateRoutes(RouteRequest request);
}

RouteRequest
public class RouteRequest {
    private String start;
    private String destination;
    private List<String> landmarks;
    private String criteria; // "distance" | "time"
}

RouteResult
public class RouteResult {
    private int routeId;
    private List<String> path;
    private double distanceKm;
    private double estimatedTimeMin;
    private List<String> landmarks;
}

📥 User Input Flow
1. Dropdown / Autocomplete

Use GraphUtils.getSuggestions("Bal") from backend utils to show matching landmarks.

Prevents invalid inputs.

2. Tap-to-Pin on Map

User clicks/taps static UG map.

First tap → Start location (red pin).

Second tap → Destination (red pin).

Store coordinates → Map back to nearest landmark node (algorithm team will supply a helper).

3. Criteria Selection

Radio buttons: Shortest Distance / Shortest Time.

Checkbox list for landmarks (e.g., “Pass by Bank”).

⚡ Calling the Algorithm (via Service)

When the user clicks “Calculate Route”:

RouteRequest req = new RouteRequest(
    "Balme Library",
    "N Block",
    Arrays.asList("Bank"),
    "time"
);

List<RouteResult> results = routingService.calculateRoutes(req);


UI must then:

Show all results in a side panel list.

Highlight best route automatically.

Allow user to click another route to re-render on the map.

🎨 Rendering in JavaFX

Map: ImageView for static UG map.

Routes: Drawn using Canvas overlays.

Pins: Small red circles for Start/End.

Animation (optional): Polyline drawn with Timeline.

MapRenderer.renderRoute(results.get(0).getPath());

🚨 Error Handling

Invalid Location

Backend throws:

throw new RuntimeException("Invalid location");


UI → Show red banner: "Invalid location. Please select from suggestions."

No Route Found

Backend returns empty list.

UI → Show: "No available route found."

💡 Suggested Layout
Scene 1 – Route Search

Top: Start + Destination input (textfields with autocomplete OR tap-to-pin on map).

Side: Landmark checkboxes, criteria selection.

Bottom: “Calculate Route” button.

Scene 2 – Results

Left: Map with drawn route(s).

Right: Route options list (distance, time, landmarks).

Save/Export (optional).

🧪 Mocking Before Algorithm Team is Ready

Until backend is connected, use MockRoutingService:

public class MockRoutingService implements RoutingService {
    @Override
    public List<RouteResult> calculateRoutes(RouteRequest request) {
        return Arrays.asList(
            new RouteResult(1, Arrays.asList("Balme", "JQB", "NNB", "N Block"), 1.2, 10, Arrays.asList("JQB")),
            new RouteResult(2, Arrays.asList("Balme", "Central Cafeteria", "NNB", "N Block"), 1.5, 12, Arrays.asList("Central Cafeteria"))
        );
    }
}


This ensures UI can be fully tested before algorithms are plugged in.

🌟 Optional Extras (Only if Time Permits)

Traffic delay simulation.

Animated route drawing.

Save route (JSON).

Export route (PDF/image).

Accessibility (zoom controls, colorblind-friendly colors).

✅ Next Steps for UI Devs

Implement RouteSearchController + RouteResultsController.

Wire input fields to RoutingService.

Render map + route list with dummy data.

Replace MockRoutingService with real algorithm service when ready.

Test with sample inputs.