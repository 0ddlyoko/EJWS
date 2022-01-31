package me.oddlyoko.ejws.module;

import me.oddlyoko.ejws.base.exceptions.InvalidModuleDescriptorException;
import me.oddlyoko.ejws.util.StringUtil;
import me.oddlyoko.ejws.util.Version;

import java.util.Locale;

public class ModuleDescriptor {
    private final String name;
    private final String description;
    private final String title;
    private final Version version;
    private final String[] authors;
    private final String[] dependencies;
    private final String bugs;
    private final String license;
    private final String licenseUrl;
    private final String url;

    /**
     * Gson
     */
    private ModuleDescriptor() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    private ModuleDescriptor(String name, String description, String title, Version version, String[] authors, String[] dependencies, String bugs, String license, String licenseUrl, String url) {
        this.name = name;
        this.description = description;
        this.title = title;
        this.version = version;
        this.authors = authors;
        this.dependencies = dependencies;
        this.bugs = bugs;
        this.license = license;
        this.licenseUrl = licenseUrl;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public Version getVersion() {
        return version;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public String getBugs() {
        return bugs;
    }

    public String getLicense() {
        return license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Check if the descriptor is valid and add some other stuff like minimum and maximum version.<br />
     * Module Descriptor is invalid if:
     * <ul>
     *     <li><b>name</b> or <b>version</b> is null or empty</li>
     * </ul>
     * Default fields are:
     * <ul>
     *     <li><b>description</b>: <i>{Name} - Description not found</i></li>
     *     <li><b>title</b>: <i>{Name}</i></li>
     *     <li><b>version</b>: <i>1.0.0</i></li>
     *     <li><b>authors</b>: <i>[]</i></li>
     *     <li><b>dependencies</b>: <i>[]</i></li>
     *     <li><b>bugs</b>: <i>{url}</i></li>
     *     <li><b>license</b>: <i>Unknown</i></li>
     *     <li><b>licenseUrl</b>: <i>{url}</i></li>
     *     <li><b>url</b>: <i>https://github.com</i></li>
     * </ul>
     */
    public static ModuleDescriptorBuilder builder(String name, Version version) {
        return new ModuleDescriptorBuilder(name, version);
    }

    public static class ModuleDescriptorBuilder {
        private final String name;
        private final Version version;
        private String description;
        private String title;
        private String[] authors;
        private String[] dependencies;
        private String bugs;
        private String license;
        private String licenseUrl;
        private String url;

        private ModuleDescriptorBuilder(String name, Version version) {
            this.name = name;
            this.version = version;
        }

        public ModuleDescriptorBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ModuleDescriptorBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ModuleDescriptorBuilder authors(String... authors) {
            this.authors = authors;
            return this;
        }

        public ModuleDescriptorBuilder dependencies(String... dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        public ModuleDescriptorBuilder bugs(String bugs) {
            this.bugs = bugs;
            return this;
        }

        public ModuleDescriptorBuilder license(String license) {
            this.license = license;
            return this;
        }

        public ModuleDescriptorBuilder licenseUrl(String licenseUrl) {
            this.licenseUrl = licenseUrl;
            return this;
        }

        public ModuleDescriptorBuilder url(String url) {
            this.url = url;
            return this;
        }

        public ModuleDescriptor build() throws InvalidModuleDescriptorException {
            // Check if required fields exist
            if (StringUtil.isBlank(name))
                throw new InvalidModuleDescriptorException("Name should exist");
            if (version == null)
                throw new InvalidModuleDescriptorException("Version should exist");
            // Set default fields
            if (StringUtil.isBlank(description))
                description = name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1) + " - Description not found";
            if (StringUtil.isBlank(title))
                title = name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
            if (authors == null)
                authors = new String[0];
            if (dependencies == null)
                dependencies = new String[0];
            if (StringUtil.isBlank(license))
                license = "Unknown";
            if (StringUtil.isBlank(url))
                url = "https://github.com";
            if (StringUtil.isBlank(bugs))
                bugs = "https://github.com/0ddlyoko/EJWS/issues";
            if (StringUtil.isBlank(licenseUrl))
                licenseUrl = "https://github.com/0ddlyoko/EJWS/LICENSE";
            return new ModuleDescriptor(name, description, title, version, authors, dependencies, bugs, license, licenseUrl, url);
        }
    }
}
