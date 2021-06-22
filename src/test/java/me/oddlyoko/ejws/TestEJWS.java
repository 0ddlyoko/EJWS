package me.oddlyoko.ejws;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.net.URISyntaxException;
import me.oddlyoko.ejws.module.ModuleManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestEJWS {

    @AfterEach
    public void afterEach() {
        // Unload all
        EJWS.get().getModuleManager().unloadAllModules();
        EJWS.get().getModuleManager().unloadModule(ModuleManager.BASE_MODULE_NAME);
    }

    @Test
    @DisplayName("Test EJWS without parameters")
    public void testEJWSWithoutParameters() throws URISyntaxException {
        String rootDirectory = new File(TestEJWS.class.getClassLoader().getResource("modules").toURI()).getParentFile().getAbsolutePath();
        assertDoesNotThrow(() -> EJWS.main(new String[] {}));
        // Give invalid directory
        assertThrows(IllegalArgumentException.class, () -> EJWS.main(new String[] { rootDirectory + "/invalid" }));
        // Give valid directory
        assertDoesNotThrow(() -> EJWS.main(new String[] { rootDirectory + "/modules" }));
        assertEquals(3, EJWS.get().getModuleManager().getModules().size());
    }
}
