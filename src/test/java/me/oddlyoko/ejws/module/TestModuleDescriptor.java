package me.oddlyoko.ejws.module;

import me.oddlyoko.ejws.EJWS;
import me.oddlyoko.ejws.base.exceptions.InvalidModuleDescriptorException;
import me.oddlyoko.ejws.base.exceptions.ModuleLoadException;
import me.oddlyoko.ejws.util.Version;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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
    public void testModuleDescriptorGetter() throws InvalidModuleDescriptorException {
        ModuleDescriptor moduleDescriptor = ModuleDescriptor.builder("test 1", Version.V1_0)
                .description("Test Module 1")
                .title("Test Module 1")
                .authors("0ddlyoko")
                .bugs("https://github.com/0ddlyoko/EJWS/issues")
                .license("MIT")
                .licenseUrl("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE")
                .url("https://github.com/0ddlyoko/EJWS")
                .build();

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
        assertThrows(InvalidModuleDescriptorException.class, () ->
                ModuleDescriptor.builder(null, Version.V1_0)
                        .description("Test Module 1")
                        .title("Test Module 1")
                        .authors("0ddlyoko")
                        .bugs("https://github.com/0ddlyoko/EJWS/issues")
                        .license("MIT")
                        .licenseUrl("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE")
                        .url("https://github.com/0ddlyoko/EJWS")
                        .build());
    }

    @Test
    @DisplayName("Test validate no version")
    public void testValidateNoVersion() {
        assertThrows(InvalidModuleDescriptorException.class, () ->
                ModuleDescriptor.builder("test 1", null)
                        .description("Test Module 1")
                        .title("Test Module 1")
                        .authors("0ddlyoko")
                        .bugs("https://github.com/0ddlyoko/EJWS/issues")
                        .license("MIT")
                        .licenseUrl("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE")
                        .url("https://github.com/0ddlyoko/EJWS")
                        .build());
    }

    @Test
    @DisplayName("Test validate default description")
    public void testValidateDefaultDescription() throws InvalidModuleDescriptorException {
        assertEquals("Test 1 - Description not found", ModuleDescriptor.builder("test 1", Version.V1_0)
                .title("Test Module 1")
                .authors("0ddlyoko")
                .bugs("https://github.com/0ddlyoko/EJWS/issues")
                .license("MIT")
                .licenseUrl("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE")
                .url("https://github.com/0ddlyoko/EJWS")
                .build().getDescription());
    }

    @Test
    @DisplayName("Test validate default title")
    public void testValidateDefaultTitle() throws InvalidModuleDescriptorException {
        assertEquals("Test 1", ModuleDescriptor.builder("test 1", Version.V1_0)
                .description("Test Module 1")
                .authors("0ddlyoko")
                .bugs("https://github.com/0ddlyoko/EJWS/issues")
                .license("MIT")
                .licenseUrl("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE")
                .url("https://github.com/0ddlyoko/EJWS")
                .build().getTitle());
    }

    @Test
    @DisplayName("Test validate default authors")
    public void testValidateDefaultAuthors() throws InvalidModuleDescriptorException {
        assertEquals(0, ModuleDescriptor.builder("test 1", Version.V1_0)
                .title("Test Module 1")
                .description("Test Module 1")
                .bugs("https://github.com/0ddlyoko/EJWS/issues")
                .license("MIT")
                .licenseUrl("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE")
                .url("https://github.com/0ddlyoko/EJWS")
                .build().getAuthors().length);
    }

    @Test
    @DisplayName("Test validate default dependencies")
    public void testValidateDefaultDependencies() throws InvalidModuleDescriptorException {
        assertEquals(0, ModuleDescriptor.builder("test 1", Version.V1_0)
                .title("Test Module 1")
                .description("Test Module 1")
                .authors("0ddlyoko")
                .bugs("https://github.com/0ddlyoko/EJWS/issues")
                .license("MIT")
                .licenseUrl("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE")
                .url("https://github.com/0ddlyoko/EJWS")
                .build().getDependencies().length);
    }

    @Test
    @DisplayName("Test validate default license")
    public void testValidateDefaultLicense() throws InvalidModuleDescriptorException {
        assertEquals("Unknown", ModuleDescriptor.builder("test 1", Version.V1_0)
                .title("Test Module 1")
                .description("Test Module 1")
                .authors("0ddlyoko")
                .bugs("https://github.com/0ddlyoko/EJWS/issues")
                .licenseUrl("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE")
                .url("https://github.com/0ddlyoko/EJWS")
                .build().getLicense());
    }

    @Test
    @DisplayName("Test validate default url")
    public void testValidateDefaultUrl() throws InvalidModuleDescriptorException {
        assertEquals("https://github.com", ModuleDescriptor.builder("test 1", Version.V1_0)
                .description("Test Module 1")
                .title("Test Module 1")
                .authors("0ddlyoko")
                .bugs("https://github.com/0ddlyoko/EJWS/issues")
                .license("MIT")
                .licenseUrl("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE")
                .build().getUrl());
    }

    @Test
    @DisplayName("Test validate default bugs url")
    public void testValidateDefaultBugsUrl() throws InvalidModuleDescriptorException {
        assertEquals("https://github.com/0ddlyoko/EJWS/issues", ModuleDescriptor.builder("test 1", Version.V1_0)
                .description("Test Module 1")
                .title("Test Module 1")
                .authors("0ddlyoko")
                .license("MIT")
                .licenseUrl("https://github.com/0ddlyoko/EJWS/blob/master/LICENSE")
                .url("https://github.com/0ddlyoko/EJWS")
                .build().getBugs());
    }

    @Test
    @DisplayName("Test validate default license url")
    public void testValidateDefaultLicenseUrl() throws InvalidModuleDescriptorException {
        assertEquals("https://github.com/0ddlyoko/EJWS/LICENSE", ModuleDescriptor.builder("test 1", Version.V1_0)
                .description("Test Module 1")
                .title("Test Module 1")
                .authors("0ddlyoko")
                .bugs("https://github.com/0ddlyoko/EJWS/issues")
                .license("MIT")
                .url("https://github.com/0ddlyoko/EJWS")
                .build().getLicenseUrl());
    }
}
