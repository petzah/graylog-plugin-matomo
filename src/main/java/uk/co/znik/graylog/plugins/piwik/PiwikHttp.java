package uk.co.znik.graylog.plugins.piwik;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class PiwikHttp {

    private static final Logger LOG = LoggerFactory.getLogger(PiwikHttp.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final String url;
    private HttpClient httpClient;

    private static final List<Header> commonHeaders = new ArrayList<>(Arrays.asList(
            new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded"),
            new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "identity")
    ));

    private static final List<NameValuePair> commonParams = new ArrayList<>(Arrays.asList(
            new BasicNameValuePair("format", "json2"),
            new BasicNameValuePair("filterLimit", "-1"),
            new BasicNameValuePair("module", "API")
    ));

    PiwikHttp(String piwik_url, String piwik_token) {
        commonParams.add(new BasicNameValuePair("token_auth", piwik_token));
        this.httpClient = HttpClientBuilder.create().setDefaultHeaders(commonHeaders).build();
        this.url = piwik_url;
    }

    List<PiwikSite> getAllSites() {
        List<PiwikSite> sites = null;
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("method", "SitesManager.getAllSites"));
        try {
            sites = objectMapper.readValue(call_api(params).getEntity().getContent(),
                    new TypeReference<List<PiwikSite>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sites;
    }

    private PiwikSite getSiteFromPiwik(Integer idsite) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("idSite", idsite.toString()));
        params.add(new BasicNameValuePair("method", "SitesManager.getSiteFromId"));
        JsonNode rootNode = getJsonResponse(call_api(params));
        PiwikSite site = null;
        try {
            site = objectMapper.readValue(rootNode.toString(), PiwikSite.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return site;
    }

    public PiwikSite getSiteFromPiwik(String name) {
        PiwikSite piwikSite = null;
        List<NameValuePair> urlParams = new ArrayList<>();
        urlParams.add(new BasicNameValuePair("url", name));
        urlParams.add(new BasicNameValuePair("method", "SitesManager.getSitesIdFromSiteUrl"));

        JsonNode rootNode = getJsonResponse(call_api(urlParams));
        if (rootNode.size() != 0) {
            piwikSite = getSiteFromPiwik(rootNode.get(0).path("idsite").asInt());
            LOG.warn("DEBUG: Got site: " + piwikSite.getName() + " from piwik with id: " + piwikSite.getIdsite());
        }
        return piwikSite;
    }

    public PiwikSite addNewSite(String name, String main_url) {
        LOG.warn("DEBUG: Adding new site with name: " + name);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("urls[0]", main_url));
        params.add(new BasicNameValuePair("siteName", name));
        params.add(new BasicNameValuePair("method", "SitesManager.addSite"));

        JsonNode rootNode = getJsonResponse(call_api(params));
        Integer idsite =  rootNode.path("value").asInt();
        return getSiteFromPiwik(idsite);
    }

    private JsonNode getJsonResponse(HttpResponse response) {
        JsonNode json = null;
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.readTree(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private HttpResponse call_api(List<NameValuePair> params) {
        params.addAll(commonParams);
        HttpPost post = new HttpPost(url);
        try {
            post.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(post);
            //LOG.error("Response: " + httpResponse.getStatusLine());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return httpResponse;
    }


}
