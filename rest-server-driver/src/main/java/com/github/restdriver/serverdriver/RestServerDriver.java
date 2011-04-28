/**
 * Copyright © 2010-2011 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.restdriver.serverdriver;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.RequestBody;
import com.github.restdriver.serverdriver.http.RequestModifier;
import com.github.restdriver.serverdriver.http.exception.RuntimeHttpHostConnectException;
import com.github.restdriver.serverdriver.http.exception.RuntimeUnknownHostException;
import com.github.restdriver.serverdriver.http.response.DefaultResponse;
import com.github.restdriver.serverdriver.http.response.Response;

/**
 * Provides static methods for performing HTTP requests against a resource.
 * 
 * @author mjg
 */
public final class RestServerDriver {

    private RestServerDriver() {
    }

    private static final int DEFAULT_HTTP_TIMEOUT_MS = 10000;

    /* ****************************************************************************
     *                      Helper methods to make value objects                  *
     ******************************************************************************/

    /**
     * Make a Header.
     * 
     * @param name The name for the header
     * @param value The value for the header
     * 
     * @return The new header instance
     */
    public static Header header(String name, String value) {
        return new Header(name, value);
    }

    /**
     * Make a RequestBody for PUT or POST.
     *
     * @param content Request body content as String
     * @param contentType content-type eg text/plain
     * 
     * @return The new request body instance
     */
    public static RequestBody body(String content, String contentType) {
        return new RequestBody(content, contentType);
    }

    /* ****************************************************************************
     *                             HTTP OPTIONS methods                           *
     ******************************************************************************/

    /**
     * Perform an HTTP OPTIONS on a resource.
     *
     * @param url The URL of a resource.  Accepts any Object and calls .toString() on it.
     *
     * @return A Response encapsulating the server's reply.
     */
    public static Response options(Object url) {
        HttpOptions request = new HttpOptions(url.toString());
        return doHttpRequest(request);
    }

    /**
     * Synonym for {@link #options(Object)}.
     *
     * @param url The URL of a resource.  Accepts any Object and calls .toString() on it.
     *
     * @return A Response encapsulating the server's reply.
     */
    public static Response optionsOf(Object url) {
        return options(url);
    }

    /* ****************************************************************************
     *                               HTTP GET methods                             *
     ******************************************************************************/

    /**
     * Perform an HTTP GET on a resource.
     *
     * @param url The URL of a resource.  Accepts any Object and calls .toString() on it.
     * @param headers Optional HTTP headers to put on the request.
     *
     * @return A Response encapsulating the server's reply.
     */
    public static Response get(Object url, Header... headers) {
        HttpGet request = new HttpGet(url.toString());
        applyModifiersToRequest(headers, request);
        return doHttpRequest(request);
    }

    /**
     * Synonym for {@link #get(Object, Header...)}.
     * 
     * @param url The URL of a resource.  Accepts any Object and calls .toString() on it.
     * @param headers Optional HTTP headers to put on the request.
     *
     * @return A Response encapsulating the server's reply.
     */
    public static Response getOf(Object url, Header... headers) {
        return get(url, headers);
    }

    /**
     * Synonym for {@link #get(Object, Header...)}.
     * 
     * @param url The URL of a resource.  Accepts any Object and calls .toString() on it.
     * @param headers Optional HTTP headers to put on the request.
     *
     * @return A Response encapsulating the server's reply.
     */
    public static Response doGetOf(Object url, Header... headers) {
        return get(url, headers);
    }

    /**
     * Synonym for {@link #get(Object, Header...)}.
     * 
     * @param url The URL of a resource.  Accepts any Object and calls .toString() on it.
     * @param headers Optional HTTP headers to put on the request.
     *
     * @return A Response encapsulating the server's reply.
     */
    public static Response getting(Object url, Header... headers) {
        return get(url, headers);
    }

    /* ****************************************************************************
     *                              HTTP POST methods                             *
     ******************************************************************************/

    /**
     * Perform an HTTP POST to the given URL.
     *
     * @param url The URL.  Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     *
     * @return Response encapsulating the server's reply
     */
    public static Response post(Object url, RequestModifier... modifiers) {
        HttpPost request = new HttpPost(url.toString());
        applyModifiersToRequest(modifiers, request);
        return doHttpRequest(request);
    }

    /**
     * Synonym for {@link #post(Object, RequestModifier...)}.
     *
     * @param url The URL.  Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     *
     * @return Response encapsulating the server's reply
     */
    public static Response postOf(Object url, RequestModifier... modifiers) {
        return post(url, modifiers);
    }

    /**
     * Synonym for {@link #post(Object, RequestModifier...)}.
     *
     * @param url The URL.  Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     *
     * @return Response encapsulating the server's reply
     */
    public static Response doPostOf(Object url, RequestModifier... modifiers) {
        return post(url, modifiers);
    }

    /**
     * Synonym for {@link #post(Object, RequestModifier...)}.
     *
     * @param url The URL.  Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     *
     * @return Response encapsulating the server's reply
     */
    public static Response posting(Object url, RequestModifier... modifiers) {
        return post(url, modifiers);
    }

    /* ****************************************************************************
     *                              HTTP POST methods                             *
     ******************************************************************************/

    /**
     * Perform an HTTP PUT to the given URL.
     *
     * @param url The URL.  Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     *
     * @return Response encapsulating the server's reply
     */
    public static Response put(Object url, RequestModifier... modifiers) {
        HttpPut request = new HttpPut(url.toString());
        applyModifiersToRequest(modifiers, request);
        return doHttpRequest(request);
    }

    /**
     * Synonym for {@link #put(Object, RequestModifier...)}.
     *
     * @param url The URL.  Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     *
     * @return Response encapsulating the server's reply
     */
    public static Response putOf(Object url, RequestModifier... modifiers) {
        return put(url, modifiers);
    }

    /**
     * Synonym for {@link #put(Object, RequestModifier...)}.
     *
     * @param url The URL.  Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     *
     * @return Response encapsulating the server's reply
     */
    public static Response doPutOf(Object url, RequestModifier... modifiers) {
        return put(url, modifiers);
    }

    /**
     * Synonym for {@link #put(Object, RequestModifier...)}.
     *
     * @param url The URL.  Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     *
     * @return Response encapsulating the server's reply
     */
    public static Response putting(Object url, RequestModifier... modifiers) {
        return put(url, modifiers);
    }

    /* ****************************************************************************
     *                            HTTP DELETE methods                             *
     ******************************************************************************/

    /**
     * Send an HTTP delete.
     *
     * @param url The resource to delete
     * @param headers Any http headers
     * @return Response encapsulating the server's reply
     */
    public static Response delete(Object url, Header... headers) {
        HttpDelete request = new HttpDelete(url.toString());
        applyModifiersToRequest(headers, request);
        return doHttpRequest(request);
    }

    /**
     * Synonym for {@link #delete(Object, Header...)}.
     *
     * @param url The resource to delete
     * @param headers Any http headers
     * @return Response encapsulating the server's reply
     */
    public static Response deleteOf(Object url, Header... headers) {
        return delete(url, headers);
    }

    /**
     * Synonym for {@link #delete(Object, Header...)}.
     *
     * @param url The resource to delete
     * @param headers Any http headers
     * @return Response encapsulating the server's reply
     */
    public static Response doDeleteOf(Object url, Header... headers) {
        return delete(url, headers);
    }

    /**
     * Synonym for {@link #delete(Object, Header...)}.
     *
     * @param url The resource to delete
     * @param headers Any http headers
     * @return Response encapsulating the server's reply
     */
    public static Response deleting(Object url, Header... headers) {
        return delete(url, headers);
    }

    /*
     * Internal methods for creating requests and responses
     */

    private static void applyModifiersToRequest(RequestModifier[] modifiers, HttpUriRequest request) {
        if (modifiers == null) {
            return;
        }

        for (RequestModifier modifier : modifiers) {
            modifier.applyTo(request);
        }
    }

    /**
     * This is the method which actually makes http requests over the wire.
     *
     * @param request The Apache Http request to make
     *
     * @return Our wrapped response type
     */
    private static Response doHttpRequest(HttpUriRequest request) {

        HttpClient httpClient = new DefaultHttpClient();

        HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_HTTP_TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(httpParams, 0);

        HttpResponse response;

        try {
            long startTime = System.currentTimeMillis();
            response = httpClient.execute(request);
            long endTime = System.currentTimeMillis();

            return new DefaultResponse(response, (endTime - startTime));

        } catch (UnknownHostException uhe) {
            throw new RuntimeUnknownHostException(uhe);

        } catch (HttpHostConnectException hhce) {
            throw new RuntimeHttpHostConnectException(hhce);

        } catch (IOException e) {
            throw new RuntimeException("Error executing request", e);
        }

    }

}
