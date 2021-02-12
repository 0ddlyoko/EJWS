package me.oddlyoko.ejws.module;

import java.io.File;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import org.jetbrains.annotations.NotNull;

public class TheModule<M extends Module> {
    private final File jarFile;
    private final ModuleDescriptor moduleDescriptor;
    private final ModuleFinder moduleFinder;
    private final ModuleReference moduleReference;
    private final ModuleLayer moduleLayer;
    private final M module;

    public TheModule(@NotNull File jarFile, @NotNull ModuleDescriptor moduleDescriptor, @NotNull ModuleFinder moduleFinder, @NotNull ModuleReference moduleReference, @NotNull ModuleLayer moduleLayer, @NotNull M module) {
        this.jarFile = jarFile;
        this.moduleDescriptor = moduleDescriptor;
        this.moduleFinder = moduleFinder;
        this.moduleReference = moduleReference;
        this.moduleLayer = moduleLayer;
        this.module = module;
    }

    public File getJarFile() {
        return jarFile;
    }

    public ModuleDescriptor getModuleDescriptor() {
        return moduleDescriptor;
    }

    public ModuleFinder getModuleFinder() {
        return moduleFinder;
    }

    public ModuleReference getModuleReference() {
        return moduleReference;
    }

    public ModuleLayer getModuleLayer() {
        return moduleLayer;
    }

    public M getModule() {
        return module;
    }

    public String getName() {
        return getModuleDescriptor().getName();
    }
}
