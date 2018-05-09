package uk.co.znik.graylog.plugins.mamoto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class MamotoHttp {

    private static final Logger LOG = LoggerFactory.getLogger(MamotoHttp.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final String mamotoUrl;
    private final OkHttpClient httpClient;

    private static final Headers.Builder commonHeaders = new Headers.Builder()
            .add("User-Agent", "graylog-plugin-mamoto")
            .add("Content-Type", "application/x-www-form-urlencoded")
            .add("Accept-Encoding","identity");

    private static final HashMap<String, String> commonParams = new HashMap<String, String>(){{
        put("format", "json2");
        put("filterLimit", "-1");
        put("module", "API");
    }};

    MamotoHttp(String mamotoUrl, String mamotoToken) {
        commonParams.put("token_auth", mamotoToken);
        this.httpClient = new OkHttpClient();
        this.mamotoUrl = mamotoUrl;
    }

    List<MamotoSite> getAllSites() {
        LOG.warn("DEBUG: getting all sites from mamoto");
        List<MamotoSite> sites = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("method", "SitesManager.getAllSites");
        try {
            sites = objectMapper.readValue(callPiwikApi(params), new TypeReference<List<MamotoSite>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sites;
    }

    private MamotoSite getSiteFromPiwik(Integer idsite) {
        HashMap<String,String> params = new HashMap<>();
        params.put("idSite", idsite.toString());
        params.put("method", "SitesManager.getSiteFromId");
        JsonNode rootNode = getJsonResponse(callPiwikApi(params));
        MamotoSite site = null;
        try {
            site = objectMapper.readValue(rootNode.toString(), MamotoSite.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return site;
    }

    synchronized public MamotoSite getSiteFromPiwik(String name) {
        MamotoSite mamotoSite = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("url", name);
        params.put("method", "SitesManager.getSitesIdFromSiteUrl");

        JsonNode rootNode = getJsonResponse(callPiwikApi(params));
        if (rootNode.size() != 0) {
            mamotoSite = getSiteFromPiwik(rootNode.get(0).path("idsite").asInt());
            LOG.warn("DEBUG: Got site: " + mamotoSite.getName() + " from mamoto with id: " + mamotoSite.getIdsite());
        }
        return mamotoSite;
    }

    public MamotoSite addNewSite(String name, String main_url) {
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
                .url(mamotoUrl)
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
