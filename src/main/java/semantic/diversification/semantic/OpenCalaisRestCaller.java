package semantic.diversification.semantic;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OpenCalaisRestCaller {
    private static final String CALAIS_API_ENDPOINT = "http://api.opencalais.com/tag/rs/enrich";
    private static final String CALAIS_API_KEY = "vrdcgrqa8nxyqkav4f9wfjw6";

    private final HttpClient client;

    public OpenCalaisRestCaller() {
        this.client = new HttpClient();
        this.client.getParams().setParameter("http.useragent", "Calais Rest Client");
    }

    public InputStream toRDF(InputStream input) {
        try {
            PostMethod method = createPostMethod(input);
            InputStream responseStream = doRequest(method);
            return responseStream;

        } catch (Exception e){
            System.err.println("Got exception while calling Calais service: " + e.getMessage());
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    private PostMethod createPostMethod(InputStream input) {
        PostMethod method = new PostMethod(CALAIS_API_ENDPOINT);
        // Set mandatory parameters
        method.setRequestHeader("x-calais-licenseID", CALAIS_API_KEY);
        // Set input content type
        method.setRequestHeader("Content-Type", "text/xml; charset=UTF-8");
        // Set response/output format
        method.setRequestHeader("Accept", "xml/rdf");
        // Enable Social Tags processing
        method.setRequestHeader("enableMetadataType", "SocialTags");
        // Add the file contents as request entity
        method.setRequestEntity(new InputStreamRequestEntity(input));

        return method;
    }

    private InputStream doRequest(PostMethod method) throws IOException {
        try {
            int returnCode = client.executeMethod(method);

            switch (returnCode) {
                case HttpStatus.SC_OK:
                    InputStream responseStream = method.getResponseBodyAsStream();
                    return new ByteArrayInputStream(IOUtils.toByteArray(responseStream));

                case HttpStatus.SC_NOT_IMPLEMENTED:
                    throw new RuntimeException("The Post method is not implemented by this URI: " + method.getResponseBodyAsString());

                default:
                    throw new RuntimeException("Got code: <" + returnCode + "> and response: <" + method.getResponseBodyAsString() + ">");
            }
        } finally {
            method.releaseConnection();
        }
    }
}
