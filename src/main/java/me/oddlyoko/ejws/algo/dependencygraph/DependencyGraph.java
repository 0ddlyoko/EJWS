package me.oddlyoko.ejws.algo.dependencygraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Dependency graph allowing to order a map of dependencies
 * 
 * @see #getOrderedGraph(Map)
 */
public final class DependencyGraph {

    private DependencyGraph() {
    }

    /**
     * Order given map to have an ordered graph<br />
     * Example:
     * <ul>
     *     <li>A: B, C, D</li>
     *     <li>C:, C, D</li>
     *     <li>C: D</li>
     *     <li>D: /</li>
     *     <li>Result: D, C, B, A</li>
     * </ul>
     *
     * @param dependencies The dependency graph
     * @return An ordered graoh
     */
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

    /**
     * Visit a node
     *
     * @param result       The ordered result
     * @param marked       Map of marked node to avoid infinite dependency loop
     * @param dependencies Map for dependencies
     * @param currentNode  The current node
     */
    private static void visit(List<String> result, Map<String, Boolean> marked, Map<String, String[]> dependencies, String currentNode) {
        // Check if the node is marked
        // If the node is marked, that means there is a recursion somewhere
        if (marked.get(currentNode))
            throw new IllegalStateException("Recursive node !");
        if (!result.contains(currentNode)) {
            // Mark this node
            marked.put(currentNode, true);
            // Visit nodes
            for (String dep : dependencies.get(currentNode))
                visit(result, marked, dependencies, dep);
            // Unmark this node
            marked.put(currentNode, false);
            // Add to head
            result.add(currentNode);
        }
    }
}
