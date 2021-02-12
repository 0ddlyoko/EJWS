package me.oddlyoko.ejws;

import java.io.File;
import me.oddlyoko.ejws.event.Events;
import me.oddlyoko.ejws.events.ModuleLoadEvent;
import me.oddlyoko.ejws.events.ModuleUnloadEvent;
import me.oddlyoko.ejws.exceptions.ModuleLoadException;
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

    private void run(String[] args) throws ModuleLoadException {
        LOGGER.info("Loading EJWS, please wait ...");
        // Register events
        Events.registerEventModule(ModuleLoadEvent.class, null);
        Events.registerEventModule(ModuleUnloadEvent.class, null);

        LOGGER.info("Loading modules ...");
        File moduleFile = new File("/home/odoo/Desktop/EJWS/target/module/");
        moduleManager.loadAllModules(moduleFile);
        // Load the base module
        /*LOGGER.info("Loading base module ...");
        File file = new File("/home/odoo/Desktop/Test/target/Test-1.0.jar");
        try {
            moduleManager.load(file);
        } catch (ModuleLoadException ex) {
            ex.printStackTrace();
        }*/
        LOGGER.info("Loaded !");

        // TODO Lock here
    }

    /**
     * Log an error to the console and stop the app.<br />
     * Used while loading the base module.
     *
     * @param error The error message
     * @param exception The exception, if there is one.
     */
    private void logAndStop(String error, Throwable exception) {
        LOGGER.error("Could not load base module: {}. Stopping the app", error);
        if (exception != null)
            LOGGER.error("", exception);
        System.exit(1);
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
