package uk.co.znik.graylog.plugins.piwik;

public class PiwikSite {

    private String name;
    private String main_url;

    private Integer idsite;

    /**
     * for ObjectMapper
     */
    PiwikSite() {
        super();
    }

    PiwikSite(String name, int idsite, String main_url) {
        this.name = name;
        this.main_url = main_url;
        this.idsite = idsite;
    }

    public String getName() {
        return name;
    }

    public Integer getIdsite() {
        return idsite;
    }

    @Override
    public String toString() {
        return name;
    }
}