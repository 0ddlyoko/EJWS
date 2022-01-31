package me.oddlyoko.ejws.module;

import me.oddlyoko.ejws.base.BaseModule;
import me.oddlyoko.ejws.base.events.ModuleLoadEvent;
import me.oddlyoko.ejws.base.events.ModuleUnloadEvent;
import me.oddlyoko.ejws.base.exceptions.ModuleAlreadyLoadedException;
import me.oddlyoko.ejws.base.exceptions.ModuleLoadException;
import me.oddlyoko.ejws.event.EventHandler;
import me.oddlyoko.ejws.event.Events;
import me.oddlyoko.ejws.util.ModuleHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestModuleManager {

    public ModuleManager moduleManager;

    @BeforeEach
    public void beforeEach() {
        moduleManager = new ModuleManager();
    }

    @AfterEach
    public void afterEach() {
        moduleManager.unloadAllModules();
        moduleManager.unloadModule(ModuleManager.BASE_MODULE_NAME);
    }

    @Test
    @DisplayName("Test loadBaseModule")
    public void testLoadBaseModule() {
        assertDoesNotThrow(() -> moduleManager.loadBaseModule());
        assertEquals(1, moduleManager.getModules().size());
        assertTrue(moduleManager.getTheModule(ModuleManager.BASE_MODULE_NAME).isPresent());
        assertTrue(moduleManager.getTheModule(BaseModule.class).isPresent());
        assertTrue(moduleManager.getModule(ModuleManager.BASE_MODULE_NAME).isPresent());
        assertTrue(moduleManager.getModule(BaseModule.class).isPresent());
        assertEquals(BaseModule.class, moduleManager.getModule(BaseModule.class).get().getClass());
        assertTrue(Events.getHandlerList(ModuleLoadEvent.class).isPresent());
        assertTrue(Events.getHandlerList(ModuleUnloadEvent.class).isPresent());
        // Unload module
        assertDoesNotThrow(() -> moduleManager.unloadModule(ModuleManager.BASE_MODULE_NAME));
        assertTrue(moduleManager.getModules().isEmpty());
        assertTrue(moduleManager.getTheModule(ModuleManager.BASE_MODULE_NAME).isEmpty());
        assertTrue(moduleManager.getTheModule(BaseModule.class).isEmpty());
        assertTrue(moduleManager.getModule(ModuleManager.BASE_MODULE_NAME).isEmpty());
        assertTrue(moduleManager.getModule(BaseModule.class).isEmpty());
        assertTrue(Events.getHandlerList(ModuleLoadEvent.class).isEmpty());
        assertTrue(Events.getHandlerList(ModuleUnloadEvent.class).isEmpty());
        // Call another time
        assertDoesNotThrow(() -> moduleManager.unloadModule(ModuleManager.BASE_MODULE_NAME));
    }

    @Test
    @DisplayName("Test loadAllModules")
    public void testLoadAllModules() throws URISyntaxException, ModuleLoadException {
        // Load base module
        assertDoesNotThrow(() -> moduleManager.loadBaseModule());
        assertDoesNotThrow(() -> moduleManager.loadAllModules(new File(TestModuleManager.class.getClassLoader().getResource("modules/").toURI())));
        assertEquals(3, moduleManager.getModules().size());
        // Unload all modules
        moduleManager.unloadAllModules();
        assertEquals(1, moduleManager.getModules().size());
        assertEquals(2, moduleManager.loadAllModules(new File(TestModuleManager.class.getClassLoader().getResource("modules/").toURI())));
        assertEquals(3, moduleManager.getModules().size());
    }

    @Test
    @DisplayName("Test loadAllModules file")
    public void testLoadAllModulesFile() {
        // Load without base module
        assertThrows(ModuleLoadException.class, () -> moduleManager.loadAllModules(new File(TestModuleManager.class.getClassLoader().getResource("modules/Test1-1.0.jar").toURI())));
        assertEquals(0, moduleManager.getModules().size());

        assertDoesNotThrow(() -> moduleManager.loadBaseModule());
        // Load with base module (should not load because we pass a file where we want a directory
        assertThrows(ModuleLoadException.class, () -> moduleManager.loadAllModules(new File(TestModuleManager.class.getClassLoader().getResource("modules/Test1-1.0.jar").toURI())));
        assertEquals(1, moduleManager.getModules().size());
    }

    @Test
    @DisplayName("Test loadModule")
    public void testLoadModule() {
        // Load base module
        assertDoesNotThrow(() -> moduleManager.loadBaseModule());
        // Load file
        assertDoesNotThrow(() -> moduleManager.loadModule(new File(TestModuleManager.class.getClassLoader().getResource("modules/Test1-1.0.jar").toURI())));
        assertEquals(2, moduleManager.getModules().size());
        // Load invalid module
        assertThrows(ModuleLoadException.class, () -> moduleManager.loadModule(new File(new File(TestModuleManager.class.getClassLoader().getResource(".").toURI()), "notfound.jar")));
        assertEquals(2, moduleManager.getModules().size());
    }

    @Test
    @DisplayName("Test load multiple times same module should throw")
    public void testLoadMultipleTimesSameModule() {
        // Load base module
        assertDoesNotThrow(() -> moduleManager.loadBaseModule());
        // Load a module
        assertDoesNotThrow(() -> moduleManager.loadModule(new File(TestModuleManager.class.getClassLoader().getResource("modules/Test1-1.0.jar").toURI())));
        assertThrows(ModuleAlreadyLoadedException.class, () -> moduleManager.loadModule(new File(TestModuleManager.class.getClassLoader().getResource("modules/Test1-1.0.jar").toURI())));
    }

    @Test
    @DisplayName("Test load multiple times base module should throw")
    public void loadMultipleTimesBaseModule() {
        // Load base module
        assertDoesNotThrow(() -> moduleManager.loadBaseModule());
        assertEquals(2, Events.countEvent());
        // Load base module again
        assertThrows(ModuleAlreadyLoadedException.class, () -> moduleManager.loadBaseModule());
        assertEquals(2, Events.countEvent());
    }

    @Test
    @DisplayName("Test load module with exception should unload module")
    public void testLoadModuleWithException() {
        // Load base module
        assertDoesNotThrow(() -> moduleManager.loadBaseModule());
        // Should throw
        assertThrows(ModuleLoadException.class, () -> moduleManager.loadModule(new File(TestModuleManager.class.getClassLoader().getResource("modules3/Test-module-throws-1.0.jar").toURI())));
        assertTrue(moduleManager.getModule("test module throws").isEmpty());
        assertEquals(2, Events.countEvent());
    }

    @Test
    @DisplayName("Test unloadAllModules unload all modules")
    public void testUnloadAllModules() {
        // Load base module
        assertDoesNotThrow(() -> moduleManager.loadBaseModule());
        // Load all modules
        assertDoesNotThrow(() -> moduleManager.loadModule(new File(TestModuleManager.class.getClassLoader().getResource("modules/Test1-1.0.jar").toURI())));
        assertDoesNotThrow(() -> moduleManager.loadAllModules(new File(TestModuleManager.class.getClassLoader().getResource("modules/").toURI())));
        assertEquals(3, moduleManager.getModules().size());
        assertDoesNotThrow(() -> moduleManager.unloadAllModules());
        assertEquals(1, moduleManager.getModules().size());
    }

    @Test
    @DisplayName("Test ModuleHelper#load()")
    public void testModuleHelperLoad() throws URISyntaxException {
        ModuleLayer moduleLayer = ModuleHelper.getModuleLayerFromFile(new File(TestModuleManager.class.getClassLoader().getResource("modules/Test1-1.0.jar").toURI()));
        // 2 modules: the one we want and Base module
        assertEquals(2, ServiceLoader.load(moduleLayer, Module.class).stream().count());
        // 1 module: the one we want
        assertEquals(1, ModuleHelper.load(moduleLayer, Module.class).size());
        assertEquals("me.oddlyoko.test1.TestModule1", ModuleHelper.load(moduleLayer, Module.class).stream().findFirst().get().getClass().getName());
    }

    @Test
    @DisplayName("Test ModuleLoadEvent ModuleUnloadEvent")
    public void testLoadModuleCreatesModuleLoadEvent(@Mock EventHandler<ModuleLoadEvent> loadEvent,
                                                     @Mock EventHandler<ModuleUnloadEvent> unloadEvent) {
        // Load base module
        assertDoesNotThrow(() -> moduleManager.loadBaseModule());
        // Register the events
        Events.subscribe(ModuleLoadEvent.class, loadEvent);
        Events.subscribe(ModuleUnloadEvent.class, unloadEvent);
        // Load a module
        assertDoesNotThrow(() -> moduleManager.loadModule(new File(getClass().getClassLoader().getResource("modules/Test1-1.0.jar").toURI())));
        verify(loadEvent).execute(any());
        verify(unloadEvent, never()).execute(any());
        // Unload the module
        moduleManager.unloadAllModules();
        verify(loadEvent).execute(any());
        verify(unloadEvent).execute(any());
        // Load again
        assertDoesNotThrow(() -> moduleManager.loadModule(new File(getClass().getClassLoader().getResource("modules/Test1-1.0.jar").toURI())));
        verify(loadEvent, times(2)).execute(any());
        verify(unloadEvent).execute(any());
        // Unload again
        moduleManager.unloadAllModules();
        verify(loadEvent, times(2)).execute(any());
        verify(unloadEvent, times(2)).execute(any());
    }

    @Test
    @DisplayName("Test unloadModule(Module)")
    public void testUnloadModule() {
        // Load base module
        assertDoesNotThrow(() -> moduleManager.loadBaseModule());
        assertDoesNotThrow(() -> moduleManager.loadModule(new File(getClass().getClassLoader().getResource("modules/Test1-1.0.jar").toURI())));
        assertEquals(2, moduleManager.getModules().size());
        Optional<Module> module = moduleManager.getModule("test 1");
        assertTrue(module.isPresent());
        assertDoesNotThrow(() -> moduleManager.unloadModule(module.get()));
        assertEquals(1, moduleManager.getModules().size());
    }
}
