package uk.co.znik.graylog.plugins.mamoto;


public class MamotoSite {

    private String name;
    private String main_url;

    private Integer idsite;

    /**
     * for ObjectMapper
     */
    MamotoSite() {
        super();
    }

    MamotoSite(String name, int idsite, String main_url) {
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