# UGNavigate – UI Developer Guide

This document is specifically for **frontend/UI developers** working on the **UGNavigate desktop app**.
It explains how to connect the GUI layer to the core navigation algorithms without worrying about the backend internals.

---

## Responsibilities of the UI

* Provide **input fields** where users type **Start Location** and **Destination**.
* Show **typing suggestions** (autocomplete) to prevent invalid locations.
* Display the **computed shortest path** in a **list form** (step by step) and optionally **render on a campus map**.
* Show **alerts/errors** when an invalid location is entered.
* Display **distance/time** results after pathfinding.

⚠️ The UI **does not handle algorithms, data parsing, or sorting** — these are handled in the `algorithms/` and `data/` packages.

---

## How to Use Core Functions

### 1. Import Relevant Classes

All you need to work with:

```java
import ugnavigate.algorithms.dijkstra.Dijkstra;
import ugnavigate.models.Landmark;
import ugnavigate.models.Route;
import ugnavigate.utils.GraphUtils;
```

---

### 2. Validate User Input

We provide a helper method to check whether a typed location exists in our campus dataset.

```java
// Returns true if valid, false if not
boolean isValid = GraphUtils.verifyLocation("Balme Library");
```

In UI:

* If `false`, show `"Invalid location. Please select from suggestions."`.

---

### 3. Generate Suggestions While Typing

On **keypress**, query all campus landmarks:

```java
List<String> suggestions = GraphUtils.getSuggestions("Bal");  
// Returns: ["Balme Library", "Balme Cafe", "Balme Annex"]
```

The UI just needs to **display this dropdown** below the input.

---

### 4. Compute Shortest Path

Once the user selects valid start and destination:

```java
Route route = Dijkstra.findShortestPath("Balme Library", "N Block");
```

The `Route` object contains:

```java
route.getPath();     // ["Balme Library", "JQB", "NNB", "N Block"]
route.getDistance(); // e.g., 1.2 km
route.getTime();     // e.g., 10 minutes (if traffic weights applied)
```

---

### 5. Render Results in UI

* **Textual View:**
  Show each landmark in order → `"Balme Library → JQB → NNB → N Block"`.
* **Map View:**
  Pass `route.getPath()` to `MapRenderer` (already in `ui/` package).

Example:

```java
MapRenderer.render(route.getPath());
```

---

## Error Handling

1. If either `start` or `destination` is invalid → throw:

```java
throw new RuntimeException("Invalid path provided!");
```

UI should **catch this and show a red error banner**.

2. If no path exists (very rare in campus map), display:

> `"No available route found."`

---

##  Suggested UI Layout

* **Top section:** Two text fields (Start, Destination) with suggestion dropdowns.
* **Middle section:** Buttons → `"Find Path"`
* **Bottom section:**

    * Left: Route text list (step by step).
    * Right: Map visualization (using `MapRenderer`).

---

##  Next Steps for UI Dev

* Focus only on **inputs, autocomplete, error alerts, and displaying output**.
* Assume backend algorithms and data parsing **already work**.
* Just call methods, catch errors, and show results.
