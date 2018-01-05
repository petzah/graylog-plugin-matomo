package uk.co.znik.graylog.plugins.piwik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class PiwikInstance {

    private static final Logger LOG = LoggerFactory.getLogger(PiwikInstance.class);

    private String piwik_url;
    private List<PiwikSite> siteList;
    private PiwikHttp piwikHttp;

    public PiwikInstance(String piwik_url, String piwik_token) {
        this.piwik_url = piwik_url;
        this.piwikHttp = new PiwikHttp(piwik_url, piwik_token);
        this.siteList = piwikHttp.getAllSites();
        LOG.warn("DEBUG: Loaded " + siteList.size() + " sites from " + piwik_url);
    }

    public PiwikSite getSite(String name) {
        PiwikSite piwikSite = null;
        try {
            return siteList.stream().filter(s -> s.getName().equals(name)).findFirst().get();
        } catch (NoSuchElementException e) {
            piwikSite = piwikHttp.getSiteFromPiwik(name);
            if (piwikSite != null) {
                LOG.warn("DEBUG: Got site " + piwikSite.getName() + " from piwik server: " + this);
                addSite(piwikSite);
            }
        }
        return piwikSite;
    }

    private void addSite(PiwikSite piwikSite) {
        LOG.warn("DEBUG: Adding " + piwikSite + " to cache");
        siteList.add(piwikSite);
    }

    public PiwikSite getSite(Integer siteid) {
        return siteList.stream().filter(s -> s.getIdsite().equals(siteid)).findFirst().get();
    }

    public PiwikSite addNewSite(String name, String main_url) {
        PiwikSite piwikSite = piwikHttp.addNewSite(name, main_url);
        addSite(piwikSite);
        return piwikSite;
    }

    @Override
    public String toString() {
        return piwik_url;
    }
}
