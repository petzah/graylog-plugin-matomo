package uk.co.znik.graylog.plugins.piwik;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class PiwikHttp {

    private static final Logger LOG = LoggerFactory.getLogger(PiwikHttp.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final String piwik_url;
    private final OkHttpClient httpClient;

    private static final Headers.Builder commonHeaders = new Headers.Builder()
            .add("User-Agent", "graylog-plugin-piwik")
            .add("Content-Type", "application/x-www-form-urlencoded")
            .add("Accept-Encoding","identity");

    private static final HashMap<String, String> commonParams = new HashMap<String, String>(){{
        put("format", "json2");
        put("filterLimit", "-1");
        put("module", "API");
    }};

    PiwikHttp(String piwik_url, String piwik_token) {
        commonParams.put("token_auth", piwik_token);
        this.httpClient = new OkHttpClient();
        this.piwik_url = piwik_url;
    }

    List<PiwikSite> getAllSites() {
        LOG.warn("DEBUG: getting all sites from piwik");
        List<PiwikSite> sites = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("method", "SitesManager.getAllSites");
        try {
            sites = objectMapper.readValue(callPiwikApi(params), new TypeReference<List<PiwikSite>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sites;
    }

    private PiwikSite getSiteFromPiwik(Integer idsite) {
        HashMap<String,String> params = new HashMap<>();
        params.put("idSite", idsite.toString());
        params.put("method", "SitesManager.getSiteFromId");
        JsonNode rootNode = getJsonResponse(callPiwikApi(params));
        PiwikSite site = null;
        try {
            site = objectMapper.readValue(rootNode.toString(), PiwikSite.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return site;
    }

    synchronized public PiwikSite getSiteFromPiwik(String name) {
        PiwikSite piwikSite = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("url", name);
        params.put("method", "SitesManager.getSitesIdFromSiteUrl");

        JsonNode rootNode = getJsonResponse(callPiwikApi(params));
        if (rootNode.size() != 0) {
            piwikSite = getSiteFromPiwik(rootNode.get(0).path("idsite").asInt());
            LOG.warn("DEBUG: Got site: " + piwikSite.getName() + " from piwik with id: " + piwikSite.getIdsite());
        }
        return piwikSite;
    }

    public PiwikSite addNewSite(String name, String main_url) {
        LOG.warn("DEBUG: Adding new site with name: " + name);
        HashMap<String, String> params = new HashMap<>();
        params.put("urls[0]", main_url);
        params.put("siteName", name);
        params.put("method", "SitesManager.addSite");

        JsonNode rootNode = getJsonResponse(callPiwikApi(params));
        Integer idsite = rootNode.path("value").asInt();
        return getSiteFromPiwik(idsite);
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

    private byte[] callPiwikApi(HashMap<String, String> params) {
        params.putAll(commonParams);
        FormBody.Builder builder = new FormBody.Builder();
        for ( Map.Entry<String, String> entry : params.entrySet()) {
            builder.add( entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(piwik_url)
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
