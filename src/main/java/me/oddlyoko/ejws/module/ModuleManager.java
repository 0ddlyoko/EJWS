package me.oddlyoko.ejws.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import me.oddlyoko.ejws.algo.dependencygraph.DependencyGraph;
import me.oddlyoko.ejws.base.events.ModuleLoadEvent;
import me.oddlyoko.ejws.base.events.ModuleUnloadEvent;
import me.oddlyoko.ejws.base.exceptions.InvalidModuleDescriptorException;
import me.oddlyoko.ejws.base.exceptions.ModuleAlreadyLoadedException;
import me.oddlyoko.ejws.base.exceptions.ModuleLoadException;
import me.oddlyoko.ejws.base.exceptions.ModuleNotFoundException;
import me.oddlyoko.ejws.base.exceptions.ModuleProviderNotFoundException;
import me.oddlyoko.ejws.event.Events;
import me.oddlyoko.ejws.util.ModuleHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public static final String BASE_MODULE_NAME = "EJWS";

    private final Map<String, TheModule<? extends Module>> modulesByName;
    private final Map<Class<? extends Module>, TheModule<? extends Module>> modulesByClass;

    public ModuleManager() {
        modulesByName = new HashMap<>();
        modulesByClass = new HashMap<>();
    }

    public <M extends Module> Optional<M> getModule(Class<M> clazz) {
        return getTheModule(clazz).map(TheModule::getModule);
    }

    public <M extends Module> Optional<M> getModule(String name) {
        return this.<M>getTheModule(name).map(TheModule::getModule);
    }

    @SuppressWarnings("unchecked")
    public <M extends Module> Optional<TheModule<M>> getTheModule(Class<M> clazz) {
        return Optional.ofNullable((TheModule<M>) modulesByClass.get(clazz));
    }

    @SuppressWarnings("unchecked")
    public <M extends Module> Optional<TheModule<M>> getTheModule(String name) {
        return Optional.ofNullable((TheModule<M>) modulesByName.get(name));
    }

    public List<TheModule<? extends Module>> getModules() {
        return new ArrayList<>(modulesByClass.values());
    }

    /**
     * Load all modules from a specific directory<br />
     * Loading a module before the base module could result in an unknown state
     *
     * @param directory The directory where modules are
     * @return The number of loaded modules
     */
    public int loadAllModules(File directory) throws ModuleLoadException {
        if (!directory.isDirectory())
            throw new ModuleLoadException(String.format("Given path (%s) is not a directory", directory.getAbsolutePath()));
        // Get a list of files that are valid jar file
        Map<String, ModuleDescriptor> modules = new HashMap<>();
        Map<String, File> files = new HashMap<>();
        File[] jarFiles = directory.listFiles();
        if (jarFiles == null)
            return 0;
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
        int loaded = 0;
        for (String moduleName : orderedModules) {
            try {
                loadModule(files.get(moduleName), modules.get(moduleName));
                loaded++;
            } catch (ModuleAlreadyLoadedException ex) {
                // Trying to load a module that is already loaded.
                // We do not throw an exception if that occurs
                LOGGER.warn(String.format("Trying to load module %s but this module is already loaded", moduleName));
            } catch (ModuleLoadException ex) {
                LOGGER.error(String.format("Error while load module %s", moduleName), ex);
                throw ex;
            }
        }
        return loaded;
    }

    /**
     * Load a jar file as a module<br />
     * Loading a module before the base module could result in an unknown state
     *
     * @param jarFile The jar file to load
     * @throws ModuleLoadException If an error occurs while loading the module
     */
    public void loadModule(File jarFile) throws ModuleLoadException {
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
     * Load a jar file as a module with given {@link ModuleDescriptor}<br />
     * Loading a module before the base module could result in an unknown state
     *
     * @param jarFile          The jar file to load
     * @param moduleDescriptor The {@link ModuleDescriptor}
     * @throws ModuleLoadException If an error occurs while loading the module
     */
    public void loadModule(File jarFile, ModuleDescriptor moduleDescriptor) throws ModuleLoadException {
        ModuleFinder moduleFinder = ModuleHelper.ofFile(jarFile);
        ModuleReference moduleReference = moduleFinder.findAll().stream().findFirst()
                .orElseThrow(() -> new ModuleLoadException("Given file is not a java module !"));
        ModuleLayer moduleLayer = ModuleHelper.getModuleLayer(moduleFinder);
        java.lang.Module javaModule = moduleLayer.modules().stream().findFirst()
                .orElseThrow(() -> new ModuleLoadException("Given file is not a java module !"));
        Module module = ModuleHelper.load(moduleLayer, Module.class).stream().findFirst()
                .orElseThrow(() -> new ModuleProviderNotFoundException("Given module does not provide a Module provider !"));
        loadModule(
                TheModule.builder(moduleDescriptor, module)
                        .moduleFinder(moduleFinder)
                        .moduleReference(moduleReference)
                        .moduleLayer(moduleLayer)
                        .javaModule(javaModule)
                        .build());
    }

    /**
     * Load a module by passing his {@link TheModule}<br />
     * Loading a module before the base module could result in an unknown state
     *
     * @param theModule The module to load
     * @param <M>       The class of the module
     * @throws ModuleLoadException If an error occurs when loading the module
     */
    public <M extends Module> void loadModule(TheModule<M> theModule) throws ModuleLoadException {
        M module = theModule.getModule();
        ModuleDescriptor moduleDescriptor = theModule.getModuleDescriptor();
        LOGGER.info("Loading module {}", moduleDescriptor.getName());
        // Validate the ModuleDescriptor
        try {
            theModule.getModuleDescriptor().validate();
        } catch (InvalidModuleDescriptorException ex) {
            throw new ModuleLoadException(ex);
        }
        // Check if module is already loaded
        if (modulesByName.containsKey(moduleDescriptor.getName()))
            throw new ModuleAlreadyLoadedException(String.format("Error while loading module %s: a module with the same name is already loaded", moduleDescriptor.getName()));
        if (modulesByClass.containsKey(module.getClass()))
            throw new ModuleAlreadyLoadedException(String.format("Error while loading module %s: a module with the same class is already loaded", moduleDescriptor.getName()));
        // Register events
        LOGGER.info("Registering {} events", module.getModuleEvents().size());
        module.getModuleEvents().forEach(eventClass -> Events.registerEventModule(eventClass, theModule));
        // Save it
        modulesByName.put(theModule.getName(), theModule);
        modulesByClass.put(module.getClass(), theModule);
        try {
            // Call onEnable
            module.onEnable();
        } catch (Exception ex) {
            // An error occurred when calling onEnable. Unloading the module
            ModuleLoadException moduleLoadException = new ModuleLoadException(String.format("Error while loading module %s", moduleDescriptor.getName()), ex);
            LOGGER.error("Module {} failed to load:", moduleDescriptor.getName(), moduleLoadException);
            try {
                // Call onDisable
                module.onDisable();
            } catch (Exception ex2) {
                LOGGER.error("Error while calling onDisable()", ex2);
            }
            // Unregister events
            module.getModuleEvents().forEach(Events::unregisterEventModule);
            // Remove it from the list
            modulesByName.remove(theModule.getName());
            modulesByClass.remove(module.getClass());
            throw moduleLoadException;
        }
        // Call event
        Events.publish(new ModuleLoadEvent(theModule));
        LOGGER.info("Module {} loaded", moduleDescriptor.getName());
    }

    /**
     * Load the base module
     */
    public void loadBaseModule() throws ModuleLoadException {
        ModuleLayer moduleLayer = getClass().getModule().getLayer();
        if (moduleLayer == null)
            throw new ModuleLoadException(String.format("Module %s not found", BASE_MODULE_NAME));
        try {
            ModuleDescriptor moduleDescriptor = ModuleHelper.getModuleDescriptorFromReader(
                    new InputStreamReader(
                            Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(MODULE_JSON_NAME))));
            java.lang.Module javaModule = moduleLayer.findModule(BASE_MODULE_NAME)
                    .orElseThrow(() ->
                            new ModuleNotFoundException(String.format("Module %s not found", BASE_MODULE_NAME)));
            ServiceLoader<Module> moduleServiceLoader = ServiceLoader.load(moduleLayer, Module.class);
            Module module = moduleServiceLoader.findFirst()
                    .orElseThrow(() ->
                            new ModuleProviderNotFoundException(String.format("Module %s does not provide a Module provider !", BASE_MODULE_NAME)));
            // Load the module
            loadModule(
                    TheModule.builder(moduleDescriptor, module)
                            .javaModule(javaModule)
                            .moduleLayer(moduleLayer)
                            .build()
            );
        } catch (InvalidModuleDescriptorException ex) {
            throw new ModuleLoadException(String.format("Error while loading module %s", BASE_MODULE_NAME), ex);
        }
    }

    /**
     * Unload a specific module<br />
     * If a module exists with that class and the module is loaded, call {@link #unloadModule(TheModule)}
     *
     * @param module The Module Class to unload
     * @see #unloadModule(TheModule)
     */
    public void unloadModule(Module module) {
        getTheModule(module.getClass()).ifPresent(this::unloadModule);
    }

    public void unloadModule(String moduleName) {
        getTheModule(moduleName).ifPresent(this::unloadModule);
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
    public <M extends Module> void unloadModule(TheModule<M> theModule) {
        LOGGER.info("Unloading module {}", theModule.getModuleDescriptor().getName());
        // Call ModuleUnloadEvent
        Events.publish(new ModuleUnloadEvent(theModule));
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

    /**
     * Unload all modules except the base module
     */
    public void unloadAllModules() {
        List<TheModule<?>> modules = new ArrayList<>(modulesByName.values());
        // Create a dependency graph to unload in order
        Map<String, String[]> dependencies = new HashMap<>();
        modules.forEach(m -> dependencies.put(m.getName(), m.getModuleDescriptor().getDependencies()));
        List<String> orderedModules = DependencyGraph.getOrderedGraph(dependencies);
        // Reverse it
        Collections.reverse(orderedModules);
        // Unload modules
        orderedModules.forEach(m -> {
            // Do not unload base module
            if (!BASE_MODULE_NAME.equalsIgnoreCase(m))
                unloadModule(m);
        });
    }
}
