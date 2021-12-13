package me.oddlyoko.ejws.module;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import me.oddlyoko.ejws.EJWS;
import me.oddlyoko.ejws.event.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Module {
    private static final Logger LOGGER = LogManager.getLogger(EJWS.class);
    private final TheModule<? extends Module> theModule;

    protected Module() {
        this.theModule = EJWS.get().getModuleManager().getTheModule(getClass()).orElseThrow(() ->
                new IllegalStateException(String.format("Cannot find TheModule for module %s", getClass().getName())));
    }

    /**
     * Called when the module is enabling<br />
     * Here, you can register events that are required to make the module what it should do
     */
    public abstract void onEnable();

    /**
     * Called when the module is disabling<br />
     * Used to unregister registered events<br />
     * !!! onDisable() is called even if onEnable() throws an exception !!!
     */
    public abstract void onDisable();

    /**
     * Retrieves events which are defined in this module
     *
     * @return A list of events which are defined in this module
     */
    public abstract List<Class<? extends Event>> getModuleEvents();

    public TheModule<? extends Module> getTheModule() {
        return theModule;
    }

    public File getPath() {
        return new File(EJWS.get().getModuleDirectory(), theModule.getName());
    }

    /**
     * Load default config from jar file if found
     */
    public void loadDefaultConfig() {
        File file = theModule.getJarFile().orElseThrow(() -> new IllegalStateException(
                String.format("Could not find Jar file for module %s", getClass().getName())));
        File target = new File(getPath(), ModuleManager.CONFIG_NAME);
        if (target.exists())
            return;
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry(ModuleManager.CONFIG_NAME);
            if (entry == null) {
                LOGGER.warn("No config.properties file exist in jar file of module {}", getClass().getName());
                return;
            }
            try (InputStream stream = jar.getInputStream(entry)) {
                Files.copy(stream, target.toPath());
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
