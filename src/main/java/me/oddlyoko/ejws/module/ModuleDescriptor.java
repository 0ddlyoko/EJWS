package me.oddlyoko.ejws.module;

import java.util.Locale;

import me.oddlyoko.ejws.EJWS;
import me.oddlyoko.ejws.exceptions.InvalidModuleDescriptorException;
import me.oddlyoko.ejws.util.StringUtil;
import me.oddlyoko.ejws.util.Version;

public class ModuleDescriptor {
    private String name;
    private String description;
    private Version minimumCoreVersion;
    private Version maximumCoreVersion;
    private String title;
    private Version version;
    private String[] authors;
    private String[] dependencies;
    private String bugs;
    private String license;
    private String licenseUrl;
    private String url;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Version getMinimumCoreVersion() {
        return minimumCoreVersion;
    }

    public Version getMaximumCoreVersion() {
        return maximumCoreVersion;
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
     *     <li><b>minimumCoreVersion</b>: <i>{Version of EJWS}</i></li>
     *     <li><b>maximumCoreVersion</b>: <i>{Version of EJWS}</i></li>
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
    public void validate() throws InvalidModuleDescriptorException {
        // Check if required fields exist
        if (StringUtil.isBlank(name))
            throw new InvalidModuleDescriptorException("Name should exist");
        if (version == null)
            throw new InvalidModuleDescriptorException("Version should exist");
        // Set default fields
        if (StringUtil.isBlank(description))
            description = name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1) + " - Description not found";
        if (minimumCoreVersion == null)
            minimumCoreVersion = EJWS.get().getVersion();
        if (maximumCoreVersion == null)
            maximumCoreVersion = EJWS.get().getVersion();
        if (StringUtil.isBlank(title))
            title = name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
        if (version == null)
            version = Version.V1_0;
        if (authors == null)
            authors = new String[0];
        if (dependencies == null)
            dependencies = new String[0];
        if (StringUtil.isBlank(license))
            license = "Unknown";
        if (StringUtil.isBlank(url))
            url = "https://github.com";
        if (StringUtil.isBlank(bugs))
            bugs = url;
        if (StringUtil.isBlank(licenseUrl))
            licenseUrl = url;
    }
}
