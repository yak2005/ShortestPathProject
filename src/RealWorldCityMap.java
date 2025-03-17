import java.io.*;
import java.util.*;

class CityMap {
    private Map<String, List<Edge>> graph=new HashMap<>();
    public Map<String, List<Edge>> getGraph(){
        return  graph;
    }

    public CityMap() {
        this.graph = new HashMap<>();
    }

    public void addEdge(String source, String destination, int distance) {
        graph.putIfAbsent(source, new ArrayList<>());
        graph.putIfAbsent(destination, new ArrayList<>());
        graph.get(source).add(new Edge(destination, distance));
        graph.get(destination).add(new Edge(source, distance)); // For undirected graph
    }

    public void loadGraphFromCSV(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        br.readLine(); // Skip header
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            String source = parts[0];
            String destination = parts[1];
            int distance = Integer.parseInt(parts[2]);
            addEdge(source, destination, distance);
        }
        br.close();
    }

    public void displayGraph() {
        for (String node : graph.keySet()) {
            System.out.println(node + " -> " + graph.get(node));
        }
    }

    public List<String> dijkstra(String start, String end) {
        // Priority queue to select the node with the smallest distance
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();

        // Initialize distances
        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(start, 0);

        pq.add(new Node(start, 0));

        while (!pq.isEmpty()) {
            Node currentNode = pq.poll();
            String current = currentNode.name;

            if (visited.contains(current)) continue;
            visited.add(current);

            for (Edge edge : graph.get(current)) {
                String neighbor = edge.destination;
                int newDist = distances.get(current) + edge.distance;

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    pq.add(new Node(neighbor, newDist));
                }
            }
        }

        // Build the shortest path
        List<String> path = new ArrayList<>();
        for (String at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        // Print shortest path details
        if (distances.get(end) == Integer.MAX_VALUE) {
            System.out.println("No path found from " + start + " to " + end);
        } else {
            System.out.println("Shortest path from " + start + " to " + end + ": " + path);
            System.out.println("Total distance: " + distances.get(end));
        }

        return path;
    }

    static class Edge {
        String destination;
        int distance;

        Edge(String destination, int distance) {
            this.destination = destination;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "(" + destination + ", " + distance + ")";
        }
    }

    static class Node {
        String name;
        int distance;

        Node(String name, int distance) {
            this.name = name;
            this.distance = distance;
        }
    }
}

public class RealWorldCityMap {
    public static void main(String[] args) {
    try {
        CityMap cityMap = new CityMap();

        // Load data from CSV
        String filePath = "data/mukka_city_map.csv"; // Update if necessary
        cityMap.loadGraphFromCSV(filePath);

        // Display the graph
        System.out.println("Graph Representation:");
        cityMap.displayGraph();

        // User Input for Shortest Path
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the source location:");
        String source = scanner.nextLine();
        System.out.println("Enter the destination location:");
        String destination = scanner.nextLine();

        // Validate locations
        if (!cityMap.getGraph().containsKey(source) || !cityMap.getGraph().containsKey(destination)) {
            System.out.println("Error: One or both locations do not exist in the graph.");
            return;
        }

        // Find the shortest path
        cityMap.dijkstra(source, destination);

    } catch (FileNotFoundException e) {
        System.out.println("File not found! Please check the file path.");
    } catch (IOException e) {
        System.out.println("Error reading the CSV file: " + e.getMessage());
    } catch (NullPointerException e) {
        System.out.println("Error: Missing or invalid data. Please check your input and CSV file.");
    }
}
}