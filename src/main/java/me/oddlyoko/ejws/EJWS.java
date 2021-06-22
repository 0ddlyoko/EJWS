package me.oddlyoko.ejws;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import me.oddlyoko.ejws.base.exceptions.ModuleLoadException;
import me.oddlyoko.ejws.module.ModuleManager;
import me.oddlyoko.ejws.util.Version;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class EJWS {
    private static final Logger LOGGER = LogManager.getLogger(EJWS.class);
    private static EJWS ejws;

    private final Version version;
    private final ModuleManager moduleManager;

    private EJWS(String[] args) throws ModuleLoadException, IOException {
        ejws = this;
        moduleManager = new ModuleManager();
        File directory = null;
        if (args.length > 0) {
            directory = new File(args[0]);
            LOGGER.info("File is {}", directory.getAbsolutePath());
            if (!directory.exists() || !directory.isDirectory())
                throw new IllegalArgumentException(String.format("Given directory (%s) argument is not a valid directory", args[0]));
        }
        LOGGER.info("Loading EJWS, please wait ...");
        // Load version
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("version.properties"));
        version = Version.of(properties.getProperty("version"));
        // Load base module
        LOGGER.info("Loading base module");
        moduleManager.loadBaseModule();
        LOGGER.info("Base module loaded");
        if (directory != null) {
            LOGGER.info("Loading modules ...");
            moduleManager.loadAllModules(directory);
            LOGGER.info("Loaded !");
        }

        // TODO Lock here
    }

    public void unload() {
        // Unload ModuleManager
        moduleManager.unloadAllModules();
        moduleManager.unloadModule(ModuleManager.BASE_MODULE_NAME);
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public Version getVersion() {
        return version;
    }

    public static EJWS get() {
        return ejws;
    }

    public static void main(String[] args) throws ModuleLoadException, IOException {
        new EJWS(args);
    }
}
