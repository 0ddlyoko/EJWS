package me.oddlyoko.ejws.util;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Version implements Comparable<Version>, Cloneable {
    public static final Version V1_0 = of(new int[] { 1 });
    public static final Version V2_0 = of(new int[] { 2 });

    public static final int MAX_SIZE = 4;
    private static final Pattern VERSION_SPLIT = Pattern.compile("\\d+");

    private final int[] version;

    private Version(int[] version) {
        this.version = version;
    }

    public int get(int pos) {
        return version[pos];
    }

    /**
     * @return A copied array representing the version
     */
    public int[] getVersion() {
        return Arrays.copyOf(this.version, MAX_SIZE);
    }

    @Override
    public int compareTo(Version version) {
        // Go to the first position where version number is different
        int pos = 0;
        while (pos < MAX_SIZE && this.get(pos) == version.get(pos)) {
            pos++;
        }
        // If we are at MAX_SIZE, that means versions are the same
        return (pos == MAX_SIZE) ? 0 : Integer.compare(this.get(pos), version.get(pos));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        return compareTo((Version) o) == 0;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(version);
    }

    @Override
    protected Version clone() throws CloneNotSupportedException {
        return (Version) super.clone();
    }

    @Override
    public String toString() {
        return Arrays.stream(version).mapToObj(String::valueOf).collect(Collectors.joining("."));
    }

    public static Version of(int[] version) {
        if (version.length != MAX_SIZE) {
            // Resize
            version = Arrays.copyOf(version, MAX_SIZE);
        }
        return new Version(version);
    }

    /**
     * Transform a String version to a usable Version<br />
     * The recommended String version is of one of this format:<br />
     * <ul>
     *     <li>major</li>
     *     <li>major.minor</li>
     *     <li>major.minor.rev</li>
     *     <li>major.minor.rev.beta</li>
     * </ul>
     * In fact, we just retrieves the 4 first numbers present in the given string<br />
     * So, a string like <b>1a2b3c4d</b> returns <b>{ 1, 2, 3, 4 }</b>
     *
     * @param stringVersion The String version
     * @return The correct version
     */
    public static Version of(String stringVersion) {
        // Check if the pattern is correct
        int[] version = VERSION_SPLIT.matcher(stringVersion).results().mapToInt(ver -> Integer.parseInt(ver.group())).toArray();
        return of(version);
    }
}
