# UG Navigate
Optimal Routing Solution for the University of Ghana Campus

##  Project Overview
UG Navigate is a Java-based routing solution designed to help students and visitors find the best path from one landmark to another on the University of Ghana campus. The system considers both shortest distance and optimal arrival time, while also allowing users to filter routes based on landmarks (e.g., "Bank", "Library").

##  Objectives
- Find the best route from location A → B.
- Consider shortest distance and travel time.
- Provide multiple route options sorted by distance/time.
- Enable searching by landmarks/tags (e.g., routes via a bank).
- Enhance efficiency using advanced algorithmic strategies.

## Clean Architecture
- **Presentation Layer**: User interface (JavaFX/Console).
- **Application Layer**: Services (RouteFinder, LandmarkService).
- **Domain Layer**: Core graph + algorithms.
- **Infrastructure Layer**: Data loading, storage, distance calculation.

##  Algorithms Used
- **Routing**: Dijkstra, A* (real-time), Floyd-Warshall (precomputation).
- **Sorting**: MergeSort (routes by distance/time).
- **Searching**: Binary Search (routes), Linear Search (tags).
- **Optimizations**: Greedy approach, Dynamic Programming caching.
- **Extensions**: CPM, Vogel Approximation (time-based analysis).

##  How It Works
1. Load campus map as a graph (nodes = landmarks, edges = paths).
2. User inputs start + destination (+ optional tags).
3. System applies shortest path algorithm.
4. Sorts and filters routes by distance/time/landmark tags.
5. Displays 3 best routes with highlighted landmarks.

##  Future Extensions
- Integration with Google Maps API for live traffic.
- Mobile app with real-time navigation.
- Machine learning-based traffic prediction.

## Team
DCIT 204 – Group Project, University of Ghana
