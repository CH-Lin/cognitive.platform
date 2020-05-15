package prj.cognitive.utils;

import com.google.common.base.CaseFormat;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Config {
    public static final String EMPTY_STRING = "";
    private static final List<String> POSITIVE_VALUES = Arrays.asList("yes", "1", "true", "y");

    private static String getName(Class clazz, String name) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, clazz.getSimpleName()) + "_" + name;
    }

    private static <T> T load(Class clazz, String name, T defaultValue, Function<String, T> func) {
        String envVariable = getName(clazz, name);
        String value = System.getenv(getName(clazz, name));

        System.out.println(envVariable);

        if (value == null) {
            return defaultValue;
        }

        return func.apply(value);
    }

    public static boolean isTruthy(String value) {
        return POSITIVE_VALUES.contains(value.toLowerCase());
    }

    public static String get(Class clazz, String name, String defaultValue) {
        return load(clazz, name, defaultValue, s -> s);
    }

    public static Integer get(Class clazz, String name, Integer defaultValue) {
        return load(clazz, name, defaultValue, Integer::parseInt);
    }

    public static Double get(Class clazz, String name, Double defaultValue) {
        return load(clazz, name, defaultValue, Double::parseDouble);
    }

    public static Boolean get(Class clazz, String name, Boolean defaultValue) {
        return load(clazz, name, defaultValue, Config::isTruthy);
    }

    public static List<String> get(Class clazz, String name, String[] defaultValue) {
        return load(clazz, name, Arrays.asList(defaultValue), s -> Arrays.asList(s.split(";")));
    }

    public static boolean is64bit() {
        // This defaults to 64 bit if unable to determined
        Boolean is64 = get(Config.class, "IS_64BIT", (Boolean) null);

        if (is64 == null) {
            is64 = System.getProperty("os.arch", "amd64").contains("64");
        }

        return is64;
    }
}
