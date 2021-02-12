package me.oddlyoko.ejws.algo.dependencygraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class DependencyGraph {

    private DependencyGraph() {
    }

    public static List<String> getOrderedGraph(Map<String, String[]> dependencies) {
        List<String> result = new ArrayList<>();
        Map<String, Boolean> marked = new HashMap<>();
        LinkedList<String> rest = new LinkedList<>();
        dependencies.keySet().forEach(key -> {
            rest.add(key);
            marked.put(key, false);
        });
        while (!rest.isEmpty()) {
            String currentNode = rest.remove();
            visit(result, marked, dependencies, currentNode);
        }

        return result;
    }

    private static void visit(List<String> result, Map<String, Boolean> marked, Map<String, String[]> dependencies, String currentNode) {
        // Check if the node is marked
        // If the node is marked, that means there is a recursion somewhere
        if (marked.get(currentNode))
            throw new IllegalStateException("Recursive node !");
        if (!result.contains(currentNode)) {
            // Mark this node
            marked.put(currentNode, true);
            // Visit nodes
            for (String dep : dependencies.get(currentNode)) {
                visit(result, marked, dependencies, dep);
            }
            // Unmark this node
            marked.put(currentNode, false);
            // Add to head
            result.add(currentNode);
        }
    }
}
