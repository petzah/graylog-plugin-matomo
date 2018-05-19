package uk.co.znik.graylog.plugins.matomo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class MatomoInstance {

    private static final Logger LOG = LoggerFactory.getLogger(MatomoInstance.class);

    private String matomoUrl;
    private List<MatomoSite> siteList;
    private MatomoHttp matomoHttp;

    public MatomoInstance(String matomoUrl, String matomoToken) {
        this.matomoUrl = matomoUrl;
        this.matomoHttp = new MatomoHttp(matomoUrl, matomoToken);
        this.siteList = matomoHttp.getAllSites();
        LOG.info("Loaded {} sites to cache from {}", siteList.size(), matomoUrl);
    }

    synchronized public MatomoSite getSite(String name) {
        MatomoSite matomoSite = null;
        try {
            return siteList.stream().filter(s -> s.getName().equals(name)).findFirst().get();
        } catch (NoSuchElementException e) {
            matomoSite = matomoHttp.getSiteFromMatomo(name);
            if (matomoSite != null) {
                LOG.debug("Got site {} from matomo instance {}", matomoSite, this);
                cacheSite(matomoSite);
            }
        }
        return matomoSite;
    }

    private void cacheSite(MatomoSite matomoSite) {
        siteList.add(matomoSite);
        LOG.debug("Added {} to cache", matomoSite);
    }

    public MatomoSite getSite(Integer siteid) {
        return siteList.stream().filter(s -> s.getIdsite().equals(siteid)).findFirst().get();
    }

    public MatomoSite addNewSite(String name, String main_url) {
        MatomoSite matomoSite = matomoHttp.addNewSite(name, main_url);
        cacheSite(matomoSite);
        return matomoSite;
    }

    @Override
    public String toString() {
        return matomoUrl;
    }
}
