package me.oddlyoko.ejws.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.URISyntaxException;
import me.oddlyoko.ejws.util.ModuleHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestTheModule {

    @Test
    @DisplayName("Test getters")
    void testGetters(@Mock File jarFile, @Mock ModuleDescriptor moduleDescriptor, @Mock Module module) throws URISyntaxException {
        ModuleFinder moduleFinder = ModuleHelper.ofFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test1-1.0.jar").toURI()));
        ModuleReference moduleReference = moduleFinder.find("me.oddlyoko.test1").get();
        ModuleLayer moduleLayer = ModuleHelper.getModuleLayer(moduleFinder);
        TheModule<Module> theModule = new TheModule<>(jarFile, moduleDescriptor, moduleFinder, moduleReference, moduleLayer, module);
        when(moduleDescriptor.getName()).thenReturn("Name");
        assertEquals(jarFile, theModule.getJarFile());
        assertEquals(moduleDescriptor, theModule.getModuleDescriptor());
        assertEquals(moduleFinder, theModule.getModuleFinder());
        assertEquals(moduleReference, theModule.getModuleReference());
        assertEquals(moduleLayer, theModule.getModuleLayer());
        assertEquals(module, theModule.getModule());
        assertEquals("Name", theModule.getName());
    }
}
