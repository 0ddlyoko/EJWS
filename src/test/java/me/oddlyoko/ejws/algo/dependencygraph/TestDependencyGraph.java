package me.oddlyoko.ejws.algo.dependencygraph;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestDependencyGraph {

    @Test
    @DisplayName("Test Dependency Graph")
    public void testDependencyGraph() {
        Map<String, String[]> dependencies = new HashMap<>();
        dependencies.put("A", new String[] { "B" });
        dependencies.put("B", new String[] { "C" });
        dependencies.put("C", new String[] { "D" });
        dependencies.put("D", new String[] { "E" });
        dependencies.put("E", new String[] { });
        List<String> result = DependencyGraph.getOrderedGraph(dependencies);
        assertEquals(5, result.size());
        assertArrayEquals(new String[] { "E", "D", "C", "B", "A" }, result.toArray(String[]::new));
    }

    @Test
    @DisplayName("Test Dependency Graph 2")
    public void testDependencyGraph2() {
        Map<String, String[]> dependencies = new HashMap<>();
        dependencies.put("A", new String[] { "B", "D" });
        dependencies.put("B", new String[] { "C" });
        dependencies.put("C", new String[] { "E" });
        dependencies.put("D", new String[] { "C" });
        dependencies.put("E", new String[] { });
        List<String> result = DependencyGraph.getOrderedGraph(dependencies);
        assertEquals(5, result.size());
        assertArrayEquals(new String[] { "E", "C", "B", "D", "A" }, result.toArray(String[]::new));
    }

    @Test
    @DisplayName("Test Recursive Dependence")
    public void testRecursiveDependence() {
        Map<String, String[]> dependencies = new HashMap<>();
        dependencies.put("A", new String[] { "B", "D" });
        dependencies.put("B", new String[] { "C" });
        dependencies.put("C", new String[] { "E" });
        dependencies.put("D", new String[] { "D" });
        dependencies.put("E", new String[] { });
        assertThrows(IllegalStateException.class, () -> DependencyGraph.getOrderedGraph(dependencies));
    }

    @Test
    @DisplayName("Test Dependencies with one without dependence")
    public void testDependenceWithoutDependence() {
        Map<String, String[]> dependencies = new HashMap<>();
        dependencies.put("A", new String[] { "B", "D" });
        dependencies.put("B", new String[] { "C" });
        dependencies.put("G", new String[] { });
        dependencies.put("C", new String[] { "E" });
        dependencies.put("D", new String[] { });
        dependencies.put("E", new String[] { });
        dependencies.put("F", new String[] { });
        List<String> result = DependencyGraph.getOrderedGraph(dependencies);
        assertEquals(7, result.size());
        assertArrayEquals(new String[] { "E", "C", "B", "D", "A", "F", "G" }, result.toArray(String[]::new));
    }
}
