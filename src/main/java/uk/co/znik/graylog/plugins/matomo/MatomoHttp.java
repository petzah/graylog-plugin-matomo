package uk.co.znik.graylog.plugins.matomo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class MatomoHttp {

    private static final Logger LOG = LoggerFactory.getLogger(MatomoHttp.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final String matomoUrl;
    private final OkHttpClient httpClient;

    private static final Headers.Builder commonHeaders = new Headers.Builder()
            .add("User-Agent", "graylog-plugin-matomo")
            .add("Content-Type", "application/x-www-form-urlencoded")
            .add("Accept-Encoding","identity");

    private static final HashMap<String, String> commonParams = new HashMap<String, String>(){{
        put("format", "json2");
        put("filterLimit", "-1");
        put("module", "API");
    }};

    MatomoHttp(String matomoUrl, String matomoToken) {
        commonParams.put("token_auth", matomoToken);
        this.httpClient = new OkHttpClient();
        this.matomoUrl = matomoUrl;
    }

    List<MatomoSite> getAllSites() {
        LOG.warn("DEBUG: getting all sites from matomo");
        List<MatomoSite> sites = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("method", "SitesManager.getAllSites");
        try {
            sites = objectMapper.readValue(callMatomoApi(params), new TypeReference<List<MatomoSite>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sites;
    }

    private MatomoSite getSiteFromMatomo(Integer idsite) {
        HashMap<String,String> params = new HashMap<>();
        params.put("idSite", idsite.toString());
        params.put("method", "SitesManager.getSiteFromId");
        JsonNode rootNode = getJsonResponse(callMatomoApi(params));
        MatomoSite site = null;
        try {
            site = objectMapper.readValue(rootNode.toString(), MatomoSite.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return site;
    }

    synchronized public MatomoSite getSiteFromMatomo(String name) {
        MatomoSite matomoSite = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("url", name);
        params.put("method", "SitesManager.getSitesIdFromSiteUrl");

        JsonNode rootNode = getJsonResponse(callMatomoApi(params));
        if (rootNode.size() != 0) {
            matomoSite = getSiteFromMatomo(rootNode.get(0).path("idsite").asInt());
            LOG.warn("DEBUG: Got site: " + matomoSite.getName() + " from matomo with id: " + matomoSite.getIdsite());
        }
        return matomoSite;
    }

    public MatomoSite addNewSite(String name, String main_url) {
        LOG.warn("DEBUG: Adding new site with name: " + name);
        HashMap<String, String> params = new HashMap<>();
        params.put("urls[0]", main_url);
        params.put("siteName", name);
        params.put("method", "SitesManager.addSite");

        JsonNode rootNode = getJsonResponse(callMatomoApi(params));
        Integer idsite = rootNode.path("value").asInt();
        return getSiteFromMatomo(idsite);
    }

    private JsonNode getJsonResponse(byte[] responseBody) {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (IOException e) {
            LOG.error("Can't parse JSON from HTTP response.");
            e.printStackTrace();
        }
        return jsonNode;
    }

    private byte[] callMatomoApi(HashMap<String, String> params) {
        params.putAll(commonParams);
        FormBody.Builder builder = new FormBody.Builder();
        for ( Map.Entry<String, String> entry : params.entrySet()) {
            builder.add( entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(matomoUrl)
                .headers(commonHeaders.build())
                .post(requestBody)
                .build();

        byte[] responseBody = new byte[0];

        try {
            Response response = httpClient.newCall(request).execute();
            responseBody = response.body().bytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBody;
    }
}
