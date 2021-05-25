package me.oddlyoko.ejws.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.module.ModuleFinder;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import me.oddlyoko.ejws.base.exceptions.InvalidModuleDescriptorException;
import me.oddlyoko.ejws.module.ModuleDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestModuleHelper {

    @Test
    @DisplayName("Test ofDirectory")
    public void testOfDirectory() throws URISyntaxException, IOException {
        ModuleFinder moduleFinder = ModuleHelper.ofDirectory(new File(Thread.currentThread().getContextClassLoader().getResource("modules/").toURI()));
        assertNotNull(moduleFinder);
        // Should find 2 modules
        assertEquals(2, moduleFinder.findAll().size());
    }

    @Test
    @DisplayName("Test ofDirectory on file")
    public void testOfDirectoryOnFile() {
        assertThrows(IllegalArgumentException.class, () -> ModuleHelper.ofDirectory(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test1-1.0.jar").toURI())));
    }

    @Test
    @DisplayName("Test ofFile")
    public void testOfFile() throws URISyntaxException {
        assertTrue(ModuleHelper.ofFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test1-1.0.jar").toURI())).find("me.oddlyoko.test1").isPresent());
        assertTrue(ModuleHelper.ofFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test2-1.0.jar").toURI())).find("me.oddlyoko.test2").isPresent());
        assertTrue(ModuleHelper.ofFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules2/Test3-no-provider-1.0.jar").toURI())).find("me.oddlyoko.test3").isPresent());
    }

    @Test
    @DisplayName("Test ofFile on directory")
    public void testOfFileOnDirectory() {
        assertThrows(IllegalArgumentException.class, () -> ModuleHelper.ofFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/").toURI())));
    }

    @Test
    @DisplayName("Test getModulesName")
    public void testGetModulesName() throws URISyntaxException, IOException {
        ModuleFinder moduleFinder = ModuleHelper.ofDirectory(new File(Thread.currentThread().getContextClassLoader().getResource("modules/").toURI()));
        assertEquals(Set.of("me.oddlyoko.test1", "me.oddlyoko.test2"), ModuleHelper.getModulesName(moduleFinder));
    }

    @Test
    @DisplayName("Test getModulesNameFromDirectory")
    public void testGetModulesNameFromDirectory() throws URISyntaxException, IOException {
        assertEquals(Set.of("me.oddlyoko.test1", "me.oddlyoko.test2"), ModuleHelper.getModulesNameFromDirectory(new File(Thread.currentThread().getContextClassLoader().getResource("modules/").toURI())));
    }

    @Test
    @DisplayName("Test getModuleNameFromFile")
    public void testGetModuleNameFromFile() throws URISyntaxException {
        Optional<String> test1 = ModuleHelper.getModuleNameFromFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test1-1.0.jar").toURI()));
        assertTrue(test1.isPresent());
        assertEquals("me.oddlyoko.test1", test1.get());
        Optional<String> test2 = ModuleHelper.getModuleNameFromFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test2-1.0.jar").toURI()));
        assertTrue(test2.isPresent());
        assertEquals("me.oddlyoko.test2", test2.get());
        Optional<String> test3 = ModuleHelper.getModuleNameFromFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules2/Test3-no-provider-1.0.jar").toURI()));
        assertTrue(test3.isPresent());
        assertEquals("me.oddlyoko.test3", test3.get());
    }

    @Test
    @DisplayName("Test getModuleLayer")
    public void testGetModuleLayer() throws URISyntaxException, IOException {
        ModuleFinder moduleFinder = ModuleHelper.ofDirectory(new File(Thread.currentThread().getContextClassLoader().getResource("modules/").toURI()));
        ModuleLayer moduleLayer = ModuleHelper.getModuleLayer(moduleFinder);
        assertNotNull(moduleLayer);
        assertEquals(2, moduleLayer.modules().size());
        assertTrue(moduleLayer.findModule("me.oddlyoko.test1").isPresent());
        assertTrue(moduleLayer.findModule("me.oddlyoko.test2").isPresent());
        assertTrue(moduleLayer.findModule("me.oddlyoko.test3").isEmpty());
    }

    @Test
    @DisplayName("Test getModuleLayerFromFile")
    public void testGetModuleLayerFromFile() throws URISyntaxException {
        ModuleLayer moduleLayer = ModuleHelper.getModuleLayerFromFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test1-1.0.jar").toURI()));
        assertNotNull(moduleLayer);
        assertEquals(1, moduleLayer.modules().size());
        assertTrue(moduleLayer.findModule("me.oddlyoko.test1").isPresent());
    }

    @Test
    @DisplayName("Test getModuleLayerFromDirectory")
    public void testGetModuleLayerFromDirectory() throws URISyntaxException, IOException {
        ModuleLayer moduleLayer = ModuleHelper.getModuleLayerFromDirectory(new File(Thread.currentThread().getContextClassLoader().getResource("modules/").toURI()));
        assertNotNull(moduleLayer);
        assertEquals(2, moduleLayer.modules().size());
        assertTrue(moduleLayer.findModule("me.oddlyoko.test1").isPresent());
        assertTrue(moduleLayer.findModule("me.oddlyoko.test2").isPresent());
        assertTrue(moduleLayer.findModule("me.oddlyoko.test3").isEmpty());
    }

    @Test
    @DisplayName("Test getModuleDescriptorFromJarFile")
    public void testGetModuleDescriptorFromJarFile() throws URISyntaxException, InvalidModuleDescriptorException {
        ModuleDescriptor moduleDescriptor = ModuleHelper.getModuleDescriptorFromJarFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test1-1.0.jar").toURI()));
        assertNotNull(moduleDescriptor);
        assertEquals("test 1", moduleDescriptor.getName());
        assertEquals("Test Module 1", moduleDescriptor.getDescription());
        assertEquals(Version.V1_0, moduleDescriptor.getMinimumCoreVersion());
        assertEquals(Version.V1_0, moduleDescriptor.getMaximumCoreVersion());
        assertEquals("Test Module 1", moduleDescriptor.getTitle());
        assertEquals(Version.V1_0, moduleDescriptor.getVersion());
        assertArrayEquals(new String[] { "0ddlyoko" }, moduleDescriptor.getAuthors());
        assertArrayEquals(new String[0], moduleDescriptor.getDependencies());
        assertEquals("https://github.com/0ddlyoko/EJWS/issues", moduleDescriptor.getBugs());
        assertEquals("MIT", moduleDescriptor.getLicense());
        assertEquals("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE", moduleDescriptor.getLicenseUrl());
        assertEquals("https://0ddlyoko.be", moduleDescriptor.getUrl());
    }

    @Test
    @DisplayName("Test getModuleDescriptorFromJarFile invalid file")
    public void testGetModuleDescriptorFromJarFileInvalidFile() {
        assertThrows(InvalidModuleDescriptorException.class, () -> ModuleHelper.getModuleDescriptorFromJarFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test1-1.0.jar").toURI()).getParentFile()));
        assertDoesNotThrow(() -> ModuleHelper.getModuleDescriptorFromJarFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test1-1.0.jar").toURI())));
        assertThrows(InvalidModuleDescriptorException.class, () -> ModuleHelper.getModuleDescriptorFromJarFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test1-1.0.jar").toURI()), "invalid_file.yml"));
        assertThrows(InvalidModuleDescriptorException.class, () -> ModuleHelper.getModuleDescriptorFromJarFile(new File(Thread.currentThread().getContextClassLoader().getResource("empty.txt").toURI())));
    }

    @Test
    @DisplayName("Test getModuleDescriptorFromReader")
    public void testGetModuleDescriptorFromReader() throws URISyntaxException, InvalidModuleDescriptorException, IOException {
        try (JarFile jar = new JarFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test1-1.0.jar").toURI()))) {
            JarEntry entry = jar.getJarEntry("module.json");
            try (Reader reader = new InputStreamReader(jar.getInputStream(entry))) {
                ModuleDescriptor moduleDescriptor = ModuleHelper.getModuleDescriptorFromReader(reader);
                assertNotNull(moduleDescriptor);
                assertEquals("test 1", moduleDescriptor.getName());
                assertEquals("Test Module 1", moduleDescriptor.getDescription());
                assertEquals(Version.V1_0, moduleDescriptor.getMinimumCoreVersion());
                assertEquals(Version.V1_0, moduleDescriptor.getMaximumCoreVersion());
                assertEquals("Test Module 1", moduleDescriptor.getTitle());
                assertEquals(Version.V1_0, moduleDescriptor.getVersion());
                assertArrayEquals(new String[] { "0ddlyoko" }, moduleDescriptor.getAuthors());
                assertArrayEquals(new String[0], moduleDescriptor.getDependencies());
                assertEquals("https://github.com/0ddlyoko/EJWS/issues", moduleDescriptor.getBugs());
                assertEquals("MIT", moduleDescriptor.getLicense());
                assertEquals("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE", moduleDescriptor.getLicenseUrl());
                assertEquals("https://0ddlyoko.be", moduleDescriptor.getUrl());
            }
        }
    }

    @Test
    @DisplayName("Test getModuleDescriptorFromReader invalid reader")
    public void testGetModuleDescriptorFromReaderInvalidReader() throws URISyntaxException, IOException, InvalidModuleDescriptorException {
        try (JarFile jar = new JarFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test1-1.0.jar").toURI()))) {
            JarEntry entry = jar.getJarEntry("module-info.class");
            try (Reader reader = new InputStreamReader(jar.getInputStream(entry))) {
                assertThrows(InvalidModuleDescriptorException.class, () -> ModuleHelper.getModuleDescriptorFromReader(reader));
            }
        }
    }

    @Test
    @DisplayName("Test getModuleDescriptorFromJson")
    public void testGetModuleDescriptorFromJson() throws InvalidModuleDescriptorException {
        String json = "{\n" +
                "  \"name\": \"test 1\",\n" +
                "  \"description\": \"Test Module 1\",\n" +
                "  \"minimumCoreVersion\": \"1.0\",\n" +
                "  \"maximumCoreVersion\": \"1.0\",\n" +
                "  \"title\": \"Test Module 1\",\n" +
                "  \"version\": 1.0,\n" +
                "  \"authors\": [\"0ddlyoko\"],\n" +
                "  \"dependencies\": [],\n" +
                "  \"bugs\": \"https://github.com/0ddlyoko/EJWS/issues\",\n" +
                "  \"license\": \"MIT\",\n" +
                "  \"licenseUrl\": \"https://github.com/0ddlyoko/EJWS/blob/master/LICENSE\",\n" +
                "  \"url\": \"https://0ddlyoko.be\"\n" +
                "}";
        ModuleDescriptor moduleDescriptor = ModuleHelper.getModuleDescriptorFromJson(json);
        assertNotNull(moduleDescriptor);
        assertEquals("test 1", moduleDescriptor.getName());
        assertEquals("Test Module 1", moduleDescriptor.getDescription());
        assertEquals(Version.V1_0, moduleDescriptor.getMinimumCoreVersion());
        assertEquals(Version.V1_0, moduleDescriptor.getMaximumCoreVersion());
        assertEquals("Test Module 1", moduleDescriptor.getTitle());
        assertEquals(Version.V1_0, moduleDescriptor.getVersion());
        assertArrayEquals(new String[] { "0ddlyoko" }, moduleDescriptor.getAuthors());
        assertArrayEquals(new String[0], moduleDescriptor.getDependencies());
        assertEquals("https://github.com/0ddlyoko/EJWS/issues", moduleDescriptor.getBugs());
        assertEquals("MIT", moduleDescriptor.getLicense());
        assertEquals("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE", moduleDescriptor.getLicenseUrl());
        assertEquals("https://0ddlyoko.be", moduleDescriptor.getUrl());
    }

    @Test
    @DisplayName("Test getModuleDescriptorFromJson invalid json")
    public void testGetModuleDescriptorFromJsonInvalidJson() {
        String json = "lol";
        assertThrows(InvalidModuleDescriptorException.class, () -> ModuleHelper.getModuleDescriptorFromJson(json));
    }
}
