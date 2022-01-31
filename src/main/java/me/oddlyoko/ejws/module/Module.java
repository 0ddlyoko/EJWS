package me.oddlyoko.ejws.module;

import me.oddlyoko.ejws.EJWS;
import me.oddlyoko.ejws.event.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class Module {
    private static final Logger LOGGER = LogManager.getLogger(EJWS.class);
    private TheModule<? extends Module> theModule;

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
        if (theModule == null)
            theModule = EJWS.get().getModuleManager().getTheModule(getClass()).orElseThrow(() ->
                    new IllegalStateException(String.format("Cannot find TheModule for module %s", getClass().getName())));
        return theModule;
    }

    public File getPath() {
        return new File(EJWS.get().getModuleDirectory(), getTheModule().getName());
    }

    /**
     * Export default config from jar file if found<br />
     * If config file has already been exported, do not try to export it again
     */
    public void exportDefaultConfig() {
        exportConfig(ModuleManager.CONFIG_NAME);
    }

    public void exportConfig(String fileName) {
        exportConfig(fileName, false);
    }

    /**
     * Export given config file
     *
     * @param fileName The name of the file
     * @param override Override the exported file if the target file already exist
     */
    public void exportConfig(String fileName, boolean override) {
        File file = getTheModule().getJarFile().orElseThrow(() -> new IllegalStateException(
                String.format("Could not find Jar file for module %s", getClass().getName())));
        File target = new File(getPath(), fileName);
        if (target.exists() && !override)
            return;
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry(fileName);
            if (entry == null) {
                LOGGER.warn("{} file does not exist in jar file of module {}", fileName, getClass().getName());
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
