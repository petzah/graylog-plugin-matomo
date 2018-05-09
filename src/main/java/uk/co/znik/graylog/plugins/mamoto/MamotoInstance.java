package uk.co.znik.graylog.plugins.mamoto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class MamotoInstance {

    private static final Logger LOG = LoggerFactory.getLogger(MamotoInstance.class);

    private String mamotoUrl;
    private List<MamotoSite> siteList;
    private MamotoHttp mamotoHttp;

    public MamotoInstance(String mamotoUrl, String mamotoToken) {
        this.mamotoUrl = mamotoUrl;
        this.mamotoHttp = new MamotoHttp(mamotoUrl, mamotoToken);
        this.siteList = mamotoHttp.getAllSites();
        LOG.warn("DEBUG: Loaded " + siteList.size() + " sites from " + mamotoUrl);
    }

    public MamotoSite getSite(String name) {
        MamotoSite mamotoSite = null;
        try {
            return siteList.stream().filter(s -> s.getName().equals(name)).findFirst().get();
        } catch (NoSuchElementException e) {
            mamotoSite = mamotoHttp.getSiteFromPiwik(name);
            if (mamotoSite != null) {
                LOG.warn("DEBUG: Got site " + mamotoSite.getName() + " from mamoto server: " + this);
                addSite(mamotoSite);
            }
        }
        return mamotoSite;
    }

    private void addSite(MamotoSite mamotoSite) {
        LOG.warn("DEBUG: Adding " + mamotoSite + " to cache");
        siteList.add(mamotoSite);
    }

    public MamotoSite getSite(Integer siteid) {
        return siteList.stream().filter(s -> s.getIdsite().equals(siteid)).findFirst().get();
    }

    public MamotoSite addNewSite(String name, String main_url) {
        MamotoSite mamotoSite = mamotoHttp.addNewSite(name, main_url);
        addSite(mamotoSite);
        return mamotoSite;
    }

    @Override
    public String toString() {
        return mamotoUrl;
    }
}
