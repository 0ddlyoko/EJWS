package me.oddlyoko.ejws.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.URISyntaxException;
import java.util.Optional;
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
    public void testGetters(@Mock File jarFile, @Mock ModuleDescriptor moduleDescriptor, @Mock Module module) throws URISyntaxException {
        ModuleFinder moduleFinder = ModuleHelper.ofFile(new File(Thread.currentThread().getContextClassLoader().getResource("modules/Test1-1.0.jar").toURI()));
        ModuleReference moduleReference = moduleFinder.find("me.oddlyoko.test1").get();
        ModuleLayer moduleLayer = ModuleHelper.getModuleLayer(moduleFinder);
        Optional<java.lang.Module> javaModule = moduleLayer.modules().stream().findFirst();
        assertTrue(javaModule.isPresent());
        TheModule<Module> theModule = TheModule
                .builder(moduleDescriptor, module)
                .jarFile(jarFile)
                .moduleFinder(moduleFinder)
                .moduleReference(moduleReference)
                .moduleLayer(moduleLayer)
                .javaModule(javaModule.get())
                .build();
        when(moduleDescriptor.getName()).thenReturn("Name");
        assertTrue(theModule.getJarFile().isPresent());
        assertEquals(jarFile, theModule.getJarFile().get());
        assertEquals(moduleDescriptor, theModule.getModuleDescriptor());
        assertTrue(theModule.getModuleFinder().isPresent());
        assertEquals(moduleFinder, theModule.getModuleFinder().get());
        assertTrue(theModule.getModuleReference().isPresent());
        assertEquals(moduleReference, theModule.getModuleReference().get());
        assertTrue(theModule.getModuleLayer().isPresent());
        assertEquals(moduleLayer, theModule.getModuleLayer().get());
        assertTrue(theModule.getJavaModule().isPresent());
        assertEquals(javaModule.get(), theModule.getJavaModule().get());
        assertEquals(module, theModule.getModule());
        assertEquals("Name", theModule.getName());
    }

}
