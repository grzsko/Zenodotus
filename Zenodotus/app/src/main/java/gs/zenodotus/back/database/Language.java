package gs.zenodotus.back.database;

public enum Language {
    GRC("Greek", "grc"),
    LAT("Latin", "lat"),
    ENG("English", "eng");

    private String naturalLanguageName;
    private String abbreviation;

    Language(String name, String abbreviation) {
        this.abbreviation = abbreviation;
        this.naturalLanguageName = name;
    }

    public static Language fromAbbreviation(String abbreviation) {
        for (Language lang : Language.values()) {
            if (lang.name().equalsIgnoreCase(abbreviation)) {
                return lang;
            }
        }
        throw new IllegalArgumentException(
                "There is no language with given abbreviation");
    }
}
