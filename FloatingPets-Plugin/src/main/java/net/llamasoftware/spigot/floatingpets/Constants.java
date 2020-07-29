package net.llamasoftware.spigot.floatingpets;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

public final class Constants {

    public static final String STORAGE_TYPE_FLATFILE = "flatfile";
    public static final String STORAGE_TYPE_MYSQL = "mysql";

    public static final String INFO_MESSAGE_PREFIX = ":- ";

    public static final String METADATA_PET = "FloatingPets_Pet";
    public static final String METADATA_NAME_TAG = "FloatingPets_NameTag";
    public static final String PET_COMMAND_NAME = "floatingpets:pet";

    public static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat("#");
    public static final Pattern INTEGER_PATTERN = Pattern.compile("^\\d+$");

}