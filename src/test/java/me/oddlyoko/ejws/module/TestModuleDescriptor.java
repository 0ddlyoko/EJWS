package me.oddlyoko.ejws.module;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import me.oddlyoko.ejws.EJWS;
import me.oddlyoko.ejws.base.exceptions.InvalidModuleDescriptorException;
import me.oddlyoko.ejws.base.exceptions.ModuleLoadException;
import me.oddlyoko.ejws.util.Version;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestModuleDescriptor {

    @BeforeEach
    public void beforeEach() throws ModuleLoadException, IOException {
        // Load EJWS
        EJWS.main(new String[] {});
    }

    @AfterEach
    public void afterEach() {
        EJWS.get().unload();
    }

    @Test
    @DisplayName("Test getters")
    public void testModuleDescriptorGetter() {
        ModuleDescriptor moduleDescriptor = new ModuleDescriptor(
                "test 1",
                "Test Module 1",
                "Test Module 1",
                Version.V1_0,
                new String[] { "0ddlyoko" },
                new String[] { },
                "https://github.com/0ddlyoko/EJWS/issues",
                "MIT",
                "https://github.com/0ddlyoko/EJWS/blob/master/LICENSE",
                "https://github.com/0ddlyoko/EJWS"
        );
        assertEquals("test 1", moduleDescriptor.getName());
        assertEquals("Test Module 1", moduleDescriptor.getDescription());
        assertEquals("Test Module 1", moduleDescriptor.getTitle());
        assertEquals(Version.V1_0, moduleDescriptor.getVersion());
        assertArrayEquals(new String[] { "0ddlyoko" }, moduleDescriptor.getAuthors());
        assertArrayEquals(new String[0], moduleDescriptor.getDependencies());
        assertEquals("https://github.com/0ddlyoko/EJWS/issues", moduleDescriptor.getBugs());
        assertEquals("MIT", moduleDescriptor.getLicense());
        assertEquals("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE", moduleDescriptor.getLicenseUrl());
        assertEquals("https://github.com/0ddlyoko/EJWS", moduleDescriptor.getUrl());
    }

    @Test
    @DisplayName("Test validate no name")
    public void testValidateNoName() {
        ModuleDescriptor descriptor = new ModuleDescriptor(
                null,
                "Test Module 1",
                "Test Module 1",
                Version.V1_0,
                new String[] { "0ddlyoko" },
                new String[] { },
                "https://github.com/0ddlyoko/EJWS/issues",
                "MIT",
                "https://github.com/0ddlyoko/EJWS/blob/master/LICENSE",
                "https://github.com/0ddlyoko/EJWS");
        assertThrows(InvalidModuleDescriptorException.class, descriptor::validate);
    }

    @Test
    @DisplayName("Test validate no version")
    public void testValidateNoVersion() {
        ModuleDescriptor descriptor = new ModuleDescriptor(
                "test 1",
                "Test Module 1",
                "Test Module 1",
                null,
                new String[] { "0ddlyoko" },
                new String[] { },
                "https://github.com/0ddlyoko/EJWS/issues",
                "MIT",
                "https://github.com/0ddlyoko/EJWS/blob/master/LICENSE",
                "https://github.com/0ddlyoko/EJWS");
        assertThrows(InvalidModuleDescriptorException.class, descriptor::validate);
    }

    @Test
    @DisplayName("Test validate default description")
    public void testValidateDefaultDescription() {
        ModuleDescriptor descriptor = new ModuleDescriptor(
                "test 1",
                null,
                "Test Module 1",
                Version.V1_0,
                new String[] { "0ddlyoko" },
                new String[] { },
                "https://github.com/0ddlyoko/EJWS/issues",
                "MIT",
                "https://github.com/0ddlyoko/EJWS/blob/master/LICENSE",
                "https://github.com/0ddlyoko/EJWS");
        assertDoesNotThrow(descriptor::validate);
        assertEquals("Test 1 - Description not found", descriptor.getDescription());
    }

    @Test
    @DisplayName("Test validate default title")
    public void testValidateDefaultTitle() {
        ModuleDescriptor descriptor = new ModuleDescriptor(
                "test 1",
                "Test 1",
                null,
                Version.V1_0,
                new String[] { "0ddlyoko" },
                new String[] { },
                "https://github.com/0ddlyoko/EJWS/issues",
                "MIT",
                "https://github.com/0ddlyoko/EJWS/blob/master/LICENSE",
                "https://github.com/0ddlyoko/EJWS");
        assertDoesNotThrow(descriptor::validate);
        assertEquals("Test 1", descriptor.getTitle());
    }

    @Test
    @DisplayName("Test validate default version")
    public void testValidateDefaultVersion() {
        ModuleDescriptor descriptor = new ModuleDescriptor(
                "test 1",
                "Test 1",
                "Test Module 1",
                null,
                new String[] { "0ddlyoko" },
                new String[] { },
                "https://github.com/0ddlyoko/EJWS/issues",
                "MIT",
                "https://github.com/0ddlyoko/EJWS/blob/master/LICENSE",
                "https://github.com/0ddlyoko/EJWS");
        assertThrows(InvalidModuleDescriptorException.class, descriptor::validate);
    }

    @Test
    @DisplayName("Test validate default authors")
    public void testValidateDefaultAuthors() {
        ModuleDescriptor descriptor = new ModuleDescriptor(
                "test 1",
                "Test 1",
                "Test Module 1",
                Version.V1_0,
                null,
                new String[] { },
                "https://github.com/0ddlyoko/EJWS/issues",
                "MIT",
                "https://github.com/0ddlyoko/EJWS/blob/master/LICENSE",
                "https://github.com/0ddlyoko/EJWS");
        assertDoesNotThrow(descriptor::validate);
        assertNotNull(descriptor.getAuthors());
        assertEquals(0, descriptor.getAuthors().length);
    }

    @Test
    @DisplayName("Test validate default dependencies")
    public void testValidateDefaultDependencies() {
        ModuleDescriptor descriptor = new ModuleDescriptor(
                "test 1",
                "Test 1",
                "Test Module 1",
                Version.V1_0,
                new String[] { },
                null,
                "https://github.com/0ddlyoko/EJWS/issues",
                "MIT",
                "https://github.com/0ddlyoko/EJWS/blob/master/LICENSE",
                "https://github.com/0ddlyoko/EJWS");
        assertDoesNotThrow(descriptor::validate);
        assertNotNull(descriptor.getDependencies());
        assertEquals(0, descriptor.getDependencies().length);
    }

    @Test
    @DisplayName("Test validate default license")
    public void testValidateDefaultLicense() {
        ModuleDescriptor descriptor = new ModuleDescriptor(
                "test 1",
                "Test 1",
                "Test Module 1",
                Version.V1_0,
                new String[] { "0ddlyoko" },
                new String[] { },
                "https://github.com/0ddlyoko/EJWS/issues",
                null,
                "https://github.com/0ddlyoko/EJWS/blob/master/LICENSE",
                "https://github.com/0ddlyoko/EJWS");
        assertDoesNotThrow(descriptor::validate);
        assertEquals("Unknown", descriptor.getLicense());
    }

    @Test
    @DisplayName("Test validate default url")
    public void testValidateDefaultUrl() {
        ModuleDescriptor descriptor = new ModuleDescriptor(
                "test 1",
                "Test 1",
                "Test Module 1",
                Version.V1_0,
                new String[] { "0ddlyoko" },
                new String[] { },
                "https://github.com/0ddlyoko/EJWS/issues",
                "MIT",
                "https://github.com/0ddlyoko/EJWS/blob/master/LICENSE",
                null);
        assertDoesNotThrow(descriptor::validate);
        assertEquals("https://github.com", descriptor.getUrl());
    }

    @Test
    @DisplayName("Test validate default bugs url")
    public void testValidateDefaultBugsUrl() {
        ModuleDescriptor descriptor = new ModuleDescriptor(
                "test 1",
                "Test 1",
                "Test Module 1",
                Version.V1_0,
                new String[] { "0ddlyoko" },
                new String[] { },
                null,
                "MIT",
                "https://github.com/0ddlyoko/EJWS/blob/master/LICENSE",
                "https://github.com/0ddlyoko/EJWS");
        assertDoesNotThrow(descriptor::validate);
        assertEquals("https://github.com/0ddlyoko/EJWS", descriptor.getBugs());
    }

    @Test
    @DisplayName("Test validate default license url")
    public void testValidateDefaultLicenseUrl() {
        ModuleDescriptor descriptor = new ModuleDescriptor(
                "test 1",
                "Test 1",
                "Test Module 1",
                Version.V1_0,
                new String[] { "0ddlyoko" },
                new String[] { },
                "https://github.com/0ddlyoko/EJWS/issues",
                "MIT",
                null,
                "https://github.com/0ddlyoko/EJWS");
        assertDoesNotThrow(descriptor::validate);
        assertEquals("https://github.com/0ddlyoko/EJWS", descriptor.getLicenseUrl());
    }
}
