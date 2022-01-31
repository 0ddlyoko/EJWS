package me.oddlyoko.ejws.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import me.oddlyoko.ejws.base.exceptions.InvalidModuleDescriptorException;
import me.oddlyoko.ejws.module.ModuleDescriptor;
import me.oddlyoko.ejws.module.ModuleManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public final class ModuleHelper {

    private ModuleHelper() {
        // Nothing to do here
    }

    /**
     * Retrieves {@link ModuleFinder} from a directory
     *
     * @param directory The directory where are modules
     * @return A {@link ModuleFinder} representing all modules in that directory
     * @throws IOException If an error occurs while reading the directory
     */
    public static ModuleFinder ofDirectory(File directory) throws IOException {
        if (!directory.isDirectory())
            throw new IllegalArgumentException(String.format("Given path (%s) is not a directory !", directory.getAbsolutePath()));
        return ModuleFinder.of(Files.list(directory.toPath()).toArray(Path[]::new));
    }

    /**
     * Retrieves {@link ModuleFinder} from a file
     *
     * @param file The file where is the module
     * @return A {@link ModuleFinder} representing the file
     */
    public static ModuleFinder ofFile(File file) {
        if (!file.isFile())
            throw new IllegalArgumentException(String.format("Given path (%s) is not a file !", file.getAbsolutePath()));
        return ModuleFinder.of(file.toPath());
    }

    /**
     * Retrieves a {@link Set} of String representing modules present in the given {@link ModuleFinder}
     *
     * @param moduleFinder The {@link ModuleFinder} containing modules
     * @return a {@link Set} of String representing modules present in the given {@link ModuleFinder}
     */
    public static Set<String> getModulesName(ModuleFinder moduleFinder) {
        return moduleFinder.findAll().stream().map(moduleReference -> moduleReference.descriptor().name()).collect(Collectors.toSet());
    }

    /**
     * Retrieves a {@link Set} of String representing modules present in a specific directory
     *
     * @param directory The directory where are modules
     * @return a {@link Set} of String representing modules present in the given directory
     */
    public static Set<String> getModulesNameFromDirectory(File directory) throws IOException {
        return getModulesName(ofDirectory(directory));
    }

    /**
     * Retrieves a {@link String} representing a module present in a specific file
     *
     * @param file The file where are modules
     * @return a {@link String} representing a module present in the given file
     */
    public static Optional<String> getModuleNameFromFile(File file) {
        return getModulesName(ofFile(file)).stream().findFirst();
    }

    /**
     * Retrieves the {@link ModuleLayer} from a specific {@link ModuleFinder}
     *
     * @param moduleFinder The specific {@link ModuleFinder}
     * @return The {@link ModuleLayer}
     */
    public static ModuleLayer getModuleLayer(ModuleFinder moduleFinder) {
        Set<String> moduleNames = getModulesName(moduleFinder);
        Configuration configuration = ModuleLayer.boot().configuration().resolveAndBind(moduleFinder, ModuleFinder.of(), moduleNames);
        return ModuleLayer.boot().defineModulesWithOneLoader(configuration, ModuleHelper.class.getClassLoader());
    }

    /**
     * Retrieves the {@link ModuleLayer} from a specific file
     *
     * @param file The specific file
     * @return The {@link ModuleLayer}
     */
    public static ModuleLayer getModuleLayerFromFile(File file) {
        return getModuleLayer(ofFile(file));
    }

    /**
     * Retrieves the {@link ModuleLayer} from a specific directory
     *
     * @param directory The specific directory
     * @return The {@link ModuleLayer}
     */
    public static ModuleLayer getModuleLayerFromDirectory(File directory) throws IOException {
        return getModuleLayer(ofDirectory(directory));
    }

    /**
     * Retrieves the {@link ModuleDescriptor} that is inside a jar file<br />
     * Same as
     * <code>
     *     ModuleHelper.getModuleDescriptorFromJarFile(file, ModuleManager.MODULE_JSON_NAME);
     * </code>
     *
     * @param file The jar file
     * @return The {@link ModuleDescriptor} that describes the given jar file
     * @throws InvalidModuleDescriptorException If an error occurs while reading the {@link ModuleDescriptor}
     */
    public static ModuleDescriptor getModuleDescriptorFromJarFile(File file) throws InvalidModuleDescriptorException {
        return getModuleDescriptorFromJarFile(file, ModuleManager.MODULE_JSON_NAME);
    }

    /**
     * Retrieves the {@link ModuleDescriptor} that is inside a jar file
     *
     * @param file     The jar file
     * @param fileName The name of the json file inside the jar file that describes the module
     * @return The {@link ModuleDescriptor} that describes the given jar file
     * @throws InvalidModuleDescriptorException If an error occurs while reading the {@link ModuleDescriptor}
     */
    public static ModuleDescriptor getModuleDescriptorFromJarFile(File file, String fileName) throws InvalidModuleDescriptorException {
        if (!file.isFile())
            throw new InvalidModuleDescriptorException(String.format("%s is not a file !", file.getPath()));
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry(fileName);
            if (entry == null)
                throw new InvalidModuleDescriptorException(new FileNotFoundException(String.format("File %s doesn't have %s file", file.getPath(), fileName)));
            try (Reader reader = new InputStreamReader(jar.getInputStream(entry))) {
                return getModuleDescriptorFromReader(reader);
            }
        } catch (IOException ex) {
            throw new InvalidModuleDescriptorException(ex);
        }
    }

    /**
     * Retrieves the {@link ModuleDescriptor} of a json file
     *
     * @param reader The reader describing a module
     * @return The {@link ModuleDescriptor}
     * @throws InvalidModuleDescriptorException If given reader cannot be parsed into a ModuleDescriptor
     */
    public static ModuleDescriptor getModuleDescriptorFromReader(Reader reader) throws InvalidModuleDescriptorException {
        try {
            return new GsonBuilder().registerTypeAdapter(Version.class, GsonUtil.getVersionDeserializer()).create()
                    .fromJson(reader, ModuleDescriptor.class);
        } catch (JsonSyntaxException | JsonIOException ex) {
            throw new InvalidModuleDescriptorException("Given reader cannot be parsed to a ModuleDescriptor", ex);
        }
    }

    /**
     * Retrieves the {@link ModuleDescriptor} of a json file
     *
     * @param json The json file describing a module
     * @return The {@link ModuleDescriptor}
     * @throws InvalidModuleDescriptorException If given json cannot be parsed into a ModuleDescriptor
     */
    public static ModuleDescriptor getModuleDescriptorFromJson(String json) throws InvalidModuleDescriptorException {
        try {
            return new GsonBuilder().registerTypeAdapter(Version.class, GsonUtil.getVersionDeserializer()).create()
                    .fromJson(json, ModuleDescriptor.class);
        } catch (JsonSyntaxException ex) {
            throw new InvalidModuleDescriptorException("Given json cannot be parsed to a ModuleDescriptor", ex);
        }
    }

    /**
     * Retrieves provided class for specific {@link ModuleLayer} WITHOUT provided classes that are in "uses".
     *
     * @param moduleLayer The {@link ModuleLayer}
     * @param clazz       The class
     * @param <S>         The type of the class
     * @return A {@link Set} of loaded classes where these classes are provided by the {@link Module}
     */
    public static <S> Set<S> load(ModuleLayer moduleLayer, Class<S> clazz) {
        ServiceLoader<S> serviceLoader = ServiceLoader.load(moduleLayer, clazz);
        Set<String> modules = moduleLayer.modules()
                .stream()
                // Retrieves the needed class
                .map(module ->
                        module.getDescriptor()
                                .provides()
                                .stream()
                                // Filter to retrieve needed class
                                .filter(provides ->
                                        clazz.getName().equalsIgnoreCase(provides.service()))
                                // Returns the first one (In fact, there should only be one element in the stream)
                                .findFirst())
                .filter(Optional::isPresent)
                // Get provides
                .map(p -> p.get().providers())
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        return serviceLoader
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(s -> modules.contains(s.getClass().getName()))
                .collect(Collectors.toSet());
    }
}
