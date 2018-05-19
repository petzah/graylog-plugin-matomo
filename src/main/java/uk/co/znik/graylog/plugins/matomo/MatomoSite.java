package uk.co.znik.graylog.plugins.matomo;


public class MatomoSite {

    private String name;
    private String main_url;

    private Integer idsite;

    /**
     * for ObjectMapper
     */
    MatomoSite() {
        super();
    }

    MatomoSite(String name, int idsite, String main_url) {
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

    public String getMainUrl() {
        return main_url;
    }
}