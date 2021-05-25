package me.oddlyoko.ejws;

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

    private void run(String[] args) throws ModuleLoadException {
        LOGGER.info("Loading EJWS, please wait ...");
        // Load base module
        LOGGER.info("Loading base module");
        moduleManager.loadBaseModule();
        LOGGER.info("Base module loaded");

        LOGGER.info("Loading modules ...");
        //File moduleFile = new File("/home/odoo/Desktop/EJWS/src/test/resources/modules");
        //moduleManager.loadAllModules(moduleFile);
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
