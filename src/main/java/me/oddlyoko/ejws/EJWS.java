package me.oddlyoko.ejws;

import java.io.File;
import me.oddlyoko.ejws.base.exceptions.ModuleLoadException;
import me.oddlyoko.ejws.module.ModuleManager;
import me.oddlyoko.ejws.util.Version;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class EJWS {
    private static final Logger LOGGER = LogManager.getLogger(EJWS.class);
    private static final EJWS ejws = new EJWS();

    private final Version version = Version.of("1.0.0.0");
    private final ModuleManager moduleManager;

    private EJWS() {
        moduleManager = new ModuleManager();
    }

    public void run(String[] args) throws ModuleLoadException {
        if (args.length < 1)
            throw new IllegalArgumentException("Missing argument: <directory>");
        File file = new File(args[0]);
        System.out.println("file.getAbsolutePath() = " + file.getAbsolutePath());
        if (!file.exists() || !file.isDirectory())
            throw new IllegalArgumentException("Given <directory> argument is not a valid directory");
        LOGGER.info("Loading EJWS, please wait ...");
        // Load base module
        LOGGER.info("Loading base module");
        moduleManager.loadBaseModule();
        LOGGER.info("Base module loaded");
        LOGGER.info("Loading modules ...");
        moduleManager.loadAllModules(file);
        LOGGER.info("Loaded !");

        // TODO Lock here
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

    public static void main(String[] args) throws ModuleLoadException {
        ejws.run(args);
    }
}
