package me.oddlyoko.ejws;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    private final File directory;
    private final File moduleDirectory;

    private EJWS(String[] args) throws ModuleLoadException, IOException {
        ejws = this;
        moduleManager = new ModuleManager();
        if (args.length > 0) {
            this.directory = new File(args[0]);
            LOGGER.info("File is {}", directory.getAbsolutePath());
            if (!directory.exists() || !directory.isDirectory())
                throw new IllegalArgumentException(String.format("Given directory (%s) argument is not a valid directory", args[0]));
        } else {
            // Do not load modules if no directory has been passed through arguments
            this.directory = new File(".");
        }
        this.moduleDirectory = new File(directory, "modules");
        System.out.println(moduleDirectory);
        if (!this.moduleDirectory.exists() && !this.moduleDirectory.mkdirs())
            throw new IllegalStateException(String.format("Cannot create directory %s", this.moduleDirectory.getAbsolutePath()));
        LOGGER.info("Loading EJWS, please wait ...");
        // Load version
        version = getVersionFromResource();
        LOGGER.info("Version is {}", version);
        // Load base module
        LOGGER.info("Loading base module");
        moduleManager.loadBaseModule();
        LOGGER.info("Base module loaded");
        boolean loadModules = true;
        //boolean loadModules = System.getProperty("LoadModules", "true");
        if (loadModules) {
            LOGGER.info("Loading modules ...");
            moduleManager.loadAllModules(directory);
            LOGGER.info("Modules loaded !");
        }
        LOGGER.info("EJWS is fully loaded !");
        // TODO Lock here
    }
    
    private Version getVersionFromResource() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("version.properties"));
        return Version.of(properties.getProperty("version"));
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

    public File getDirectory() {
        return directory;
    }

    public File getModuleDirectory() {
        return moduleDirectory;
    }

    public static EJWS get() {
        return ejws;
    }

    public static void main(String[] args) throws ModuleLoadException, IOException {
        new EJWS(args);
    }
}
