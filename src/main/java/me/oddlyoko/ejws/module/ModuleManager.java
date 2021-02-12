package me.oddlyoko.ejws.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import me.oddlyoko.ejws.algo.dependencygraph.DependencyGraph;
import me.oddlyoko.ejws.event.Events;
import me.oddlyoko.ejws.events.ModuleLoadEvent;
import me.oddlyoko.ejws.events.ModuleUnloadEvent;
import me.oddlyoko.ejws.exceptions.InvalidModuleDescriptorException;
import me.oddlyoko.ejws.exceptions.ModuleLoadException;
import me.oddlyoko.ejws.util.ModuleHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * The way the method load a module is like that:<br />ModuleHelper.
 * <ol>
 *     <li>Check if the {@link ModuleDescriptor} is valid by calling {@link ModuleDescriptor#validate()}</li>
 *     <li>Check if dependencies are here</li>
 *     <li>Create the module instance by calling his default constructor</li>
 *     <li>Register events with {@link Events#registerEventModule(Class, TheModule)} and {@link Module#getModuleEvents()}</li>
 *     <li>Add the module to the list</li>
 *     <li>Call {@link Module#onEnable()}</li>
 *     <li>Generate and call a {@link ModuleLoadEvent}</li>
 *     <li></li>
 * </ol>
 */
public class ModuleManager {
    private static final Logger LOGGER = LogManager.getLogger(ModuleManager.class);
    public static final String MODULE_JSON_NAME = "module.json";
    private static final String MODULE_LOAD_ERROR = "Error while loading module %s";

    private final Map<String, TheModule<? extends Module>> modulesByName;
    private final Map<Class<? extends Module>, TheModule<? extends Module>> modulesByClass;

    public ModuleManager() {
        modulesByName = new HashMap<>();
        modulesByClass = new HashMap<>();
    }

    public <M extends Module> Optional<M> getModule(@NotNull Class<M> clazz) {
        Objects.requireNonNull(clazz, "Given class should not be null");
        return getTheModule(clazz).map(TheModule::getModule);
    }

    public <M extends Module> Optional<M> getModule(@NotNull String name) {
        Objects.requireNonNull(name, "Given name should not be null");
        return this.<M>getTheModule(name).map(TheModule::getModule);
    }

    @SuppressWarnings("unchecked")
    public <M extends Module> Optional<TheModule<M>> getTheModule(@NotNull Class<M> clazz) {
        Objects.requireNonNull(clazz, "Given class should not be null");
        return Optional.ofNullable((TheModule<M>) modulesByClass.get(clazz));
    }

    @SuppressWarnings("unchecked")
    public <M extends Module> Optional<TheModule<M>> getTheModule(@NotNull String name) {
        Objects.requireNonNull(name, "Given name should not be null");
        return Optional.ofNullable((TheModule<M>) modulesByName.get(name));
    }

    /**
     * Load all modules from a specific directory
     */
    public void loadAllModules(@NotNull File directory) throws ModuleLoadException {
        Objects.requireNonNull(directory, "Given directory should not be null");
        if (!directory.isDirectory())
            throw new IllegalArgumentException(String.format("Given path (%s) is not a directory", directory.getAbsolutePath()));
        // Get a list of files that are valid jar file
        Map<String, ModuleDescriptor> modules = new HashMap<>();
        Map<String, File> files = new HashMap<>();
        File[] jarFiles = directory.listFiles();
        if (jarFiles == null)
            return;
        for (File file : jarFiles) {
            if (file.isFile()) {
                ModuleDescriptor moduleDescriptor;
                try {
                    moduleDescriptor = ModuleHelper.getModuleDescriptorFromJarFile(file);
                } catch (InvalidModuleDescriptorException ex) {
                    LOGGER.error(String.format("Error while loading file %s", file.getPath()), ex);
                    continue;
                }
                modules.put(moduleDescriptor.getName(), moduleDescriptor);
                files.put(moduleDescriptor.getName(), file);
            }
        }
        // Reorder dependencies
        Map<String, String[]> dependencies = new HashMap<>();
        modules.values().forEach(moduleDescriptor -> dependencies.put(moduleDescriptor.getName(), moduleDescriptor.getDependencies()));
        List<String> orderedModules = DependencyGraph.getOrderedGraph(dependencies);
        for (String moduleName : orderedModules) {
            try {
                loadModule(files.get(moduleName), modules.get(moduleName));
            } catch (ModuleLoadException ex) {
                LOGGER.error(String.format("Error while load module %s", moduleName), ex);
                throw ex;
            }
        }
    }

    /**
     * Load a jar file as a module
     *
     * @param jarFile The jar file to load
     * @throws ModuleLoadException If an error occurs while loading the module
     */
    public void loadModule(@NotNull File jarFile) throws ModuleLoadException {
        Objects.requireNonNull(jarFile, "Given file should not be null");
        if (!jarFile.exists())
            throw new ModuleLoadException(new FileNotFoundException(String.format("File %s does not exist", jarFile.getPath())));
        ModuleDescriptor moduleDescriptor;
        try {
            moduleDescriptor = ModuleHelper.getModuleDescriptorFromJarFile(jarFile);
        } catch (InvalidModuleDescriptorException ex) {
            throw new ModuleLoadException(ex);
        }
        loadModule(jarFile, moduleDescriptor);
    }

    /**
     * Load a jar file as a module with given {@link ModuleDescriptor}
     *
     * @param jarFile          The jar File
     * @param moduleDescriptor The {@link ModuleDescriptor}
     * @throws ModuleLoadException If an error occurs while loading the module
     */
    public void loadModule(@NotNull File jarFile, @NotNull ModuleDescriptor moduleDescriptor) throws ModuleLoadException {
        Objects.requireNonNull(jarFile, "Given jar file should not be null");
        Objects.requireNonNull(moduleDescriptor, "Given moduleDescriptor should not be null");
        if (!jarFile.exists())
            throw new ModuleLoadException(new FileNotFoundException(String.format("File %s does not exist", jarFile.getPath())));
        // Retrieves the first module
        ModuleFinder moduleFinder = ModuleHelper.ofFile(jarFile);
        loadModule(jarFile, moduleDescriptor, moduleFinder);
    }

    /**
     * Load a jar file as a module with given {@link ModuleDescriptor} and {@link ModuleFinder}
     *
     * @param jarFile          The jar File
     * @param moduleDescriptor The {@link ModuleDescriptor}
     * @param moduleFinder     The {@link ModuleFinder}
     * @throws ModuleLoadException If an error occurs while loading the module
     */
    public void loadModule(@NotNull File jarFile, @NotNull ModuleDescriptor moduleDescriptor, @NotNull ModuleFinder moduleFinder) throws ModuleLoadException {
        Objects.requireNonNull(jarFile, "Given jar file should not be null");
        Objects.requireNonNull(moduleDescriptor, "Given moduleDescriptor should not be null");
        Objects.requireNonNull(moduleFinder, "Given moduleFinder should not be null");
        if (!jarFile.exists())
            throw new ModuleLoadException(new FileNotFoundException(String.format("File %s does not exist", jarFile.getPath())));
        // Retrieves the first module
        ModuleReference moduleReference = moduleFinder.findAll().stream().findFirst().orElseThrow(() -> new ModuleLoadException(String.format("Cannot have a ModuleReference for module %s", moduleDescriptor.getName())));
        loadModule(jarFile, moduleDescriptor, moduleFinder, moduleReference);
    }

    /**
     * Load a jar file as a module with given {@link ModuleDescriptor} and {@link ModuleReference}
     *
     *
     * @param jarFile          The jar file
     * @param moduleDescriptor The {@link ModuleDescriptor}
     * @param moduleFinder     The {@link ModuleFinder}
     * @param moduleReference  The {@link ModuleReference}
     * @throws ModuleLoadException If an error occurs while loading the module
     */
    public void loadModule(@NotNull File jarFile, @NotNull ModuleDescriptor moduleDescriptor, @NotNull ModuleFinder moduleFinder, @NotNull ModuleReference moduleReference) throws ModuleLoadException {
        Objects.requireNonNull(jarFile, "Given jar file should not be null");
        Objects.requireNonNull(moduleDescriptor, "Given moduleDescriptor should not be null");
        Objects.requireNonNull(moduleFinder, "Given moduleFinder should not be null");
        Objects.requireNonNull(moduleReference, "Given moduleReference should not be null");
        if (!jarFile.exists())
            throw new ModuleLoadException(String.format(MODULE_LOAD_ERROR, moduleDescriptor.getName()), new FileNotFoundException(String.format("File %s does not exist", jarFile.getPath())));
        ModuleLayer moduleLayer = ModuleHelper.getModuleLayer(moduleFinder);
        loadModule(jarFile, moduleDescriptor, moduleFinder, moduleReference, moduleLayer);
    }

    /**
     * Load a jar file as a module with given {@link ModuleDescriptor} and {@link ModuleReference}
     *
     *
     * @param jarFile          The jar file
     * @param moduleDescriptor The {@link ModuleDescriptor}
     * @param moduleFinder     The {@link ModuleFinder}
     * @param moduleReference  The {@link ModuleReference}
     * @param moduleLayer      The {@link ModuleLayer}
     * @throws ModuleLoadException If an error occurs while loading the module
     */
    public void loadModule(@NotNull File jarFile, @NotNull ModuleDescriptor moduleDescriptor, @NotNull ModuleFinder moduleFinder, @NotNull ModuleReference moduleReference, @NotNull ModuleLayer moduleLayer) throws ModuleLoadException {
        Objects.requireNonNull(jarFile, "Given jar file should not be null");
        Objects.requireNonNull(moduleDescriptor, "Given moduleDescriptor should not be null");
        Objects.requireNonNull(moduleFinder, "Given moduleFinder should not be null");
        Objects.requireNonNull(moduleReference, "Given moduleReference should not be null");
        Objects.requireNonNull(moduleLayer, "Given moduleLayer should not be null");
        if (!jarFile.exists())
            throw new ModuleLoadException(String.format(MODULE_LOAD_ERROR, moduleDescriptor.getName()), new FileNotFoundException(String.format("File %s does not exist", jarFile.getPath())));
        if (moduleLayer.modules().isEmpty())
            throw new ModuleLoadException(String.format(MODULE_LOAD_ERROR, moduleDescriptor.getName()), new IllegalArgumentException("Given moduleLayer is empty"));
        if (moduleLayer.modules().size() > 1)
            throw new ModuleLoadException(String.format(MODULE_LOAD_ERROR, moduleDescriptor.getName()), new IllegalArgumentException("Given moduleLayer has more than one module"));

        // Check if the moduleDescriptor is correct
        try {
            moduleDescriptor.validate();
        } catch (InvalidModuleDescriptorException ex) {
            throw new ModuleLoadException(String.format(MODULE_LOAD_ERROR, moduleDescriptor.getName()), ex);
        }

        // Check if dependencies are installed

        LOGGER.info("Loading module {}", moduleDescriptor.getName());
        ServiceLoader<Module> moduleServiceLoader = ServiceLoader.load(moduleLayer, Module.class);
        Module module = moduleServiceLoader.findFirst().orElseThrow(() -> new ModuleLoadException(String.format(MODULE_LOAD_ERROR, moduleDescriptor.getName()), new IllegalStateException("Given module doesn't have any module !")));
        TheModule<Module> theModule = new TheModule<>(jarFile, moduleDescriptor, moduleFinder, moduleReference, moduleLayer, module);
        // Register events
        if (module.getModuleEvents() != null)
            module.getModuleEvents().forEach(eventClass -> Events.registerEventModule(eventClass, theModule));
        // Save it
        modulesByName.put(theModule.getName(), theModule);
        modulesByClass.put(module.getClass(), theModule);
        try {
            // Call onEnable
            module.onEnable();
        } catch (Exception ex) {
            // An error occurred when calling onEnable. Unloading the module
            ModuleLoadException exception = new ModuleLoadException(String.format(MODULE_LOAD_ERROR, moduleDescriptor.getName()), ex);
            LOGGER.error("Module {} failed to load:", moduleDescriptor.getName(), exception);
            try {
                // Call onDisable
                module.onDisable();
            } catch (Exception ex2) {
                LOGGER.error("Error while calling onDisable()", ex2);
            }
            // Unregister events
            if (module.getModuleEvents() != null)
                module.getModuleEvents().forEach(Events::unregisterEventModule);
            // Remove it from the list
            modulesByName.remove(theModule.getName());
            modulesByClass.remove(module.getClass());
            throw exception;
        }
        // Call ModuleLoadEvent
        Events.publish(new ModuleLoadEvent<>(theModule));
        LOGGER.info("Module {} loaded", moduleDescriptor.getName());
    }

    /**
     * Unload a specific module<br />
     * If a module exists with that class and the module is loaded, call {@link #unloadModule(TheModule)}
     *
     * @param module The Module Class to unload
     * @see #unloadModule(TheModule)
     */
    public void unloadModule(@NotNull Module module) {
        Objects.requireNonNull(module, "Given module should not be null");
        getTheModule(module.getClass()).ifPresent(this::unloadModule);
    }

    public void unloadModule(@NotNull String name) {
        Objects.requireNonNull(name, "Given name should not be null");
        getTheModule(name).ifPresent(this::unloadModule);
    }

    /**
     * Unload specific module<br />
     * The way the method unload a module is like that:<br />
     * <ol>
     *     <li>Generate and call a {@link ModuleUnloadEvent}</li>
     *     <li>Call {@link Module#onDisable()} method</li>
     *     <li>Call {@link Events#unregisterEventModule(Class)} to unregister events that are defined in this module</li>
     *     <li>Remove the module from the list</li>
     * </ol>
     *
     * @param theModule The module to unload
     * @param <M> The Module
     */
    public <M extends Module> void unloadModule(@NotNull TheModule<M> theModule) {
        Objects.requireNonNull(theModule, "Given module should not be null");
        LOGGER.info("Unloading module {}", theModule.getModuleDescriptor().getName());
        // Call ModuleUnloadEvent
        Events.publish(new ModuleUnloadEvent<>(theModule));
        try {
            // Call onDisable
            theModule.getModule().onDisable();
        } catch (Exception ex) {
            LOGGER.error("Error while calling onDisable():", ex);
        }
        // Unregister events
        if (theModule.getModule().getModuleEvents() != null)
            theModule.getModule().getModuleEvents().forEach(Events::unregisterEventModule);
        // Remove it from the list
        modulesByName.remove(theModule.getName());
        modulesByClass.remove(theModule.getModule().getClass());
        LOGGER.info("Module {} unloaded", theModule.getModuleDescriptor().getName());
    }
}
