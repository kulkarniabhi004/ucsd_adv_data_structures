/**
 * @author UCSD MOOC development team 
 * 
 * A class which reprsents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
package roadgraph;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import geography.GeographicPoint;
import util.GraphLoader;
import week3example.MazeNode;

/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which represents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
public class MapGraph {
	//the graph, associates a physical location with a MapNode
	private HashMap<GeographicPoint, MapNode> geoMap;
	private HashSet<MapEdge> edgeList;
	
	
	/** 
	 * Create a new empty MapGraph 
	 */
	public MapGraph()
	{
		geoMap = new HashMap<GeographicPoint, MapNode>();
		edgeList = new HashSet<MapEdge>();
	}
	
	/**
	 * Get the number of vertices (road intersections) in the graph
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices()
	{
		return geoMap.size();
	}
	
	/**
	 * Return the intersections, which are the vertices in this graph.
	 * @return The vertices in this graph as GeographicPoints
	 */
	public Set<GeographicPoint> getVertices()
	{
		return geoMap.keySet();
	}
	
	/**
	 * Get the number of road segments in the graph
	 * @return The number of edges in the graph.
	 */
	public int getNumEdges()
	{
		return edgeList.size();
	}

	
	
	/** Add a node corresponding to an intersection at a Geographic Point
	 * If the location is already in the graph or null, this method does 
	 * not change the graph.
	 * @param location  The location of the intersection
	 * @return true if a node was added, false if it was not (the node
	 * was already in the graph, or the parameter is null).
	 */
	public boolean addVertex(GeographicPoint location)
	{		
		if (!geoMap.containsKey(location)) {
			MapNode vertex = new MapNode(location);
			geoMap.put(location, vertex);
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a directed edge to the graph from pt1 to pt2.  
	 * Precondition: Both GeographicPoints have already been added to the graph
	 * @param from The starting point of the edge
	 * @param to The ending point of the edge
	 * @param roadName The name of the road
	 * @param roadType The type of the road
	 * @param length The length of the road, in km
	 * @throws IllegalArgumentException If the points have not already been
	 *   added as nodes to the graph, if any of the arguments is null,
	 *   or if the length is less than 0.
	 */
	public void addEdge(GeographicPoint from, GeographicPoint to, String roadName,
			String roadType, double length) throws IllegalArgumentException {
		MapNode fromNode = geoMap.get(from);
		MapNode toNode = geoMap.get(to);

		if (length <= 0 || fromNode == null || toNode == null) {
			throw new IllegalArgumentException();
		}
		MapEdge edge = new MapEdge(fromNode, toNode, roadName, roadType, length);
		edgeList.add(edge);
		fromNode.addEdge(edge);
	}
	
	public void printVertices() {
		System.out.println("There are " + getNumVertices() + " vertices: \n");
		for (GeographicPoint point : geoMap.keySet()) {
			System.out.println(geoMap.get(point));
		}
		System.out.println();
	}

	public void printEdges()
	{
		System.out.println("There are " + getNumEdges() + " edges: \n");
		for (MapEdge e : edgeList) {
			System.out.println(e);
		}
		System.out.println();
	}
	

	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return bfs(start, goal, temp);
	}
	
	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, 
			 					     GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		if (start == null || goal == null) {
			throw new IllegalArgumentException();
		}
		
		MapNode startNode = geoMap.get(start);
		MapNode goalNode = geoMap.get(goal);
		
		if (startNode == null || goalNode == null) {
			System.out.println("One of the points is not on the map");
			return null;
		}
		
		HashSet<MapNode> visited = new HashSet<MapNode>();
		Queue<MapNode> queue = new LinkedList<MapNode>();
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		
		boolean found = false;
		queue.add(startNode);
		
		while (!queue.isEmpty()) {
			MapNode currentNode = queue.remove();
			nodeSearched.accept(currentNode.getLocation());
			if (currentNode == goalNode) {
				found = true;
				break;
			}
			List<MapNode> neighbors = currentNode.getNeighbors();
			for (MapNode nextNode : neighbors) {
				if (!visited.contains(nextNode)) {
					visited.add(nextNode);
					parentMap.put(nextNode, currentNode);
					queue.add(nextNode);
				}
			}
		}
		
		if (!found) {
			System.out.println("No path exists");
			return new ArrayList<GeographicPoint>();
		}
		
		
		// Hook for visualization.  See writeup.
		//nodeSearched.accept(next.getLocation());

		return constructPath(startNode, goalNode, parentMap);
	}
	
	private static List<GeographicPoint> constructPath(MapNode start, MapNode goal, HashMap<MapNode, MapNode> parentMap) {
		LinkedList<GeographicPoint> path = new LinkedList<GeographicPoint>();
		MapNode curr = goal;
		while (curr != start) {
			path.addFirst(curr.getLocation());
			curr = parentMap.get(curr);
		}
		path.addFirst(start.getLocation());
		return path;
	}
	

	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
        Consumer<GeographicPoint> temp = (x) -> {};
        return dijkstra(start, goal, temp);
	}
	
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, 
										  GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		if (start == null || goal == null) {
			throw new IllegalArgumentException();
		}
		
		MapNode startNode = geoMap.get(start);
		MapNode goalNode = geoMap.get(goal);
		
		if (startNode == null || goalNode == null) {
			System.out.println("One of the points is not on the map");
			return null;
		}
		
		int nodesVisited = 0;
		
		PriorityQueue<MapNode> pQueue = new PriorityQueue<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		
		setAllDistances();
		
		boolean found = false;
		startNode.setDistance(0);
		pQueue.add(startNode);
		
		while(!pQueue.isEmpty()) {
			MapNode currentNode = pQueue.remove();
			nodesVisited++;
			nodeSearched.accept(currentNode.getLocation());
			if (!visited.contains(currentNode)) {
				if (currentNode == goalNode) {
					found = true;
					break;
				}
				for (MapEdge edge : currentNode.getEdgeList()) {
					MapNode nextNode = edge.getEnd();
					if (!visited.contains(nextNode)) {
						double newDistance = currentNode.getDistance() + edge.getLength();
						if (newDistance < nextNode.getDistance()) {
							nextNode.setDistance(newDistance);
							nextNode.setPredictedDistance(newDistance);	
							pQueue.add(nextNode);
							parentMap.put(nextNode, currentNode);
						}
					}
				}
			}
		}
		
		if (!found) {
			System.out.println("No path exists");
			return new ArrayList<GeographicPoint>();
		}

		// Hook for visualization.  See writeup.
		//nodeSearched.accept(next.getLocation());
		
		System.out.println(nodesVisited);
		
		return constructPath(startNode, goalNode, parentMap);
	}

	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return aStarSearch(start, goal, temp);
	}
	
	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, 
											 GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		if (start == null || goal == null) {
			throw new IllegalArgumentException();
		}
		
		MapNode startNode = geoMap.get(start);
		MapNode goalNode = geoMap.get(goal);
		
		if (startNode == null || goalNode == null) {
			System.out.println("One of the points is not on the map");
			return null;
		}
		
		int nodesVisited = 0;
		
		PriorityQueue<MapNode> pQueue = new PriorityQueue<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		
		setAllDistances();
		
		boolean found = false;
		startNode.setDistance(0);
		pQueue.add(startNode);
		
		while(!pQueue.isEmpty()) {
			MapNode currentNode = pQueue.remove();
			nodesVisited++;
			nodeSearched.accept(currentNode.getLocation());
			if (!visited.contains(currentNode)) {
				if (currentNode == goalNode) {
					found = true;
					break;
				}
				for (MapEdge edge : currentNode.getEdgeList()) {
					MapNode nextNode = edge.getEnd();
					if (!visited.contains(nextNode)) {
						double newDistance = nextNode.straightDistanceTo(goalNode) + currentNode.getDistance() + edge.getLength();
						if (newDistance < nextNode.getDistance()) {
							nextNode.setDistance(newDistance - nextNode.straightDistanceTo(goalNode));
							nextNode.setPredictedDistance(newDistance);
							pQueue.add(nextNode);
							parentMap.put(nextNode, currentNode);
						}
					}
				}
			}
		}
		
		if (!found) {
			System.out.println("No path exists");
			return new ArrayList<GeographicPoint>();
		}

		// Hook for visualization.  See writeup.
		//nodeSearched.accept(next.getLocation());
		
		System.out.println(nodesVisited);
		
		return constructPath(startNode, goalNode, parentMap);
	}

	
	
	public void setAllDistances() {
		for (MapNode node : geoMap.values()) {
			node.setDistance(Integer.MAX_VALUE);
			node.setDistance(Integer.MAX_VALUE);
		}
	}

	
	
	public static void main(String[] args)
	{
		System.out.print("Making a new map...");
		MapGraph firstMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", firstMap);
		System.out.println("DONE.");
		firstMap.printVertices();
		firstMap.printEdges();
		GeographicPoint start = new GeographicPoint(1.0, 1.0);
		GeographicPoint end = new GeographicPoint(8.0, -1.0);
		List<GeographicPoint> path = firstMap.bfs(start, end);
		for (GeographicPoint point : path) {
			System.out.println(point);
		}
		
		// You can use this method for testing.  
		
		
		/* Here are some test cases you should try before you attempt 
		 * the Week 3 End of Week Quiz, EVEN IF you score 100% on the 
		 * programming assignment.
		 */
		
		MapGraph simpleTestMap = new MapGraph();
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", simpleTestMap);
		
		GeographicPoint testStart = new GeographicPoint(1.0, 1.0);
		GeographicPoint testEnd = new GeographicPoint(8.0, -1.0);
		
		System.out.println("Test 1 using simpletest: Dijkstra should be 9 and AStar should be 5");
		List<GeographicPoint> testroute = simpleTestMap.dijkstra(testStart,testEnd);
		List<GeographicPoint> testroute2 = simpleTestMap.aStarSearch(testStart,testEnd);
		
		
		MapGraph testMap = new MapGraph();
		GraphLoader.loadRoadMap("data/maps/utc.map", testMap);
		
		// A very simple test using real data
		testStart = new GeographicPoint(32.869423, -117.220917);
		testEnd = new GeographicPoint(32.869255, -117.216927);
		System.out.println("Test 2 using utc: Dijkstra should be 13 and AStar should be 5");
		testroute = testMap.dijkstra(testStart,testEnd);
		testroute2 = testMap.aStarSearch(testStart,testEnd);
		
		
		// A slightly more complex test using real data
		testStart = new GeographicPoint(32.8674388, -117.2190213);
		testEnd = new GeographicPoint(32.8697828, -117.2244506);
		System.out.println("Test 3 using utc: Dijkstra should be 37 and AStar should be 10");
		testroute = testMap.dijkstra(testStart,testEnd);
		testroute2 = testMap.aStarSearch(testStart,testEnd);
		
		
		
		/* Use this code in Week 3 End of Week Quiz */
		/*MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");

		GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
		GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);
		
		
		List<GeographicPoint> route = theMap.dijkstra(start,end);
		List<GeographicPoint> route2 = theMap.aStarSearch(start,end);

		*/
		
	}
	
}