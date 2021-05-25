package me.oddlyoko.ejws.module;

import java.io.File;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.util.Optional;

public class TheModule<M extends Module> {
    private final File jarFile;
    private final ModuleDescriptor moduleDescriptor;
    private final ModuleFinder moduleFinder;
    private final ModuleReference moduleReference;
    private final ModuleLayer moduleLayer;
    private final java.lang.Module javaModule;
    private final M module;

    private TheModule(File jarFile, ModuleDescriptor moduleDescriptor, ModuleFinder moduleFinder, ModuleReference moduleReference, ModuleLayer moduleLayer, java.lang.Module javaModule, M module) {
        this.jarFile = jarFile;
        this.moduleDescriptor = moduleDescriptor;
        this.moduleFinder = moduleFinder;
        this.moduleReference = moduleReference;
        this.moduleLayer = moduleLayer;
        this.javaModule = javaModule;
        this.module = module;
    }

    public Optional<File> getJarFile() {
        return Optional.ofNullable(jarFile);
    }

    public ModuleDescriptor getModuleDescriptor() {
        return moduleDescriptor;
    }

    public Optional<ModuleFinder> getModuleFinder() {
        return Optional.ofNullable(moduleFinder);
    }

    public Optional<ModuleReference> getModuleReference() {
        return Optional.ofNullable(moduleReference);
    }

    public Optional<ModuleLayer> getModuleLayer() {
        return Optional.ofNullable(moduleLayer);
    }

    public Optional<java.lang.Module> getJavaModule() {
        return Optional.ofNullable(javaModule);
    }

    public M getModule() {
        return module;
    }

    public String getName() {
        return getModuleDescriptor().getName();
    }


    public static <M extends Module> TheModuleBuilder<M> builder(ModuleDescriptor moduleDescriptor, M module) {
        return new TheModuleBuilder<>(moduleDescriptor, module);
    }

    public static class TheModuleBuilder<M extends Module> {
        private final ModuleDescriptor moduleDescriptor;
        private final M module;
        private File jarFile;
        private ModuleFinder moduleFinder;
        private ModuleReference moduleReference;
        private ModuleLayer moduleLayer;
        private java.lang.Module javaModule;

        public TheModuleBuilder(ModuleDescriptor moduleDescriptor, M module) {
            this.moduleDescriptor = moduleDescriptor;
            this.module = module;
        }

        public TheModuleBuilder<M> jarFile(File jarFile) {
            this.jarFile = jarFile;
            return this;
        }

        public TheModuleBuilder<M> moduleFinder(ModuleFinder moduleFinder) {
            this.moduleFinder = moduleFinder;
            return this;
        }

        public TheModuleBuilder<M> moduleReference(ModuleReference moduleReference) {
            this.moduleReference = moduleReference;
            return this;
        }

        public TheModuleBuilder<M> moduleLayer(ModuleLayer moduleLayer) {
            this.moduleLayer = moduleLayer;
            return this;
        }

        public TheModuleBuilder<M> javaModule(java.lang.Module javaModule) {
            this.javaModule = javaModule;
            return this;
        }

        public TheModule<M> build() {
            return new TheModule<>(
                    jarFile,
                    moduleDescriptor,
                    moduleFinder,
                    moduleReference,
                    moduleLayer,
                    javaModule,
                    module);
        }
    }
}
