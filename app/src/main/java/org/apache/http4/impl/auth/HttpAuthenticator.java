/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http4.impl.auth;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http4.auth.AuthOption;
import org.apache.http4.auth.AuthProtocolState;
import org.apache.http.auth.AuthScheme;
import org.apache.http4.auth.AuthStateHC4;
import org.apache.http.auth.AuthenticationException;
import org.apache.http4.auth.ContextAwareAuthScheme;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http4.client.AuthenticationStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http4.util.Asserts;

/**
 * @since 4.3
 */
public class HttpAuthenticator {

    private final static String TAG = "HttpClient";

    public HttpAuthenticator() {
        super();
    }

    public boolean isAuthenticationRequested(
            final HttpHost host,
            final HttpResponse response,
            final AuthenticationStrategy authStrategy,
            final AuthStateHC4 authState,
            final HttpContext context) {
        if (authStrategy.isAuthenticationRequested(host, response, context)) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Authentication required");
            }
            if (authState.getState() == AuthProtocolState.SUCCESS) {
                authStrategy.authFailed(host, authState.getAuthScheme(), context);
            }
            return true;
        } else {
            switch (authState.getState()) {
            case CHALLENGED:
            case HANDSHAKE:
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Authentication succeeded");
                }
                authState.setState(AuthProtocolState.SUCCESS);
                authStrategy.authSucceeded(host, authState.getAuthScheme(), context);
                break;
            case SUCCESS:
                break;
            default:
                authState.setState(AuthProtocolState.UNCHALLENGED);
            }
            return false;
        }
    }

    public boolean handleAuthChallenge(
            final HttpHost host,
            final HttpResponse response,
            final AuthenticationStrategy authStrategy,
            final AuthStateHC4 authState,
            final HttpContext context) {
        try {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, host.toHostString() + " requested authentication");
            }
            final Map<String, Header> challenges = authStrategy.getChallenges(host, response, context);
            if (challenges.isEmpty()) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Response contains no authentication challenges");
                }
                return false;
            }

            final AuthScheme authScheme = authState.getAuthScheme();
            switch (authState.getState()) {
            case FAILURE:
                return false;
            case SUCCESS:
                authState.reset();
                break;
            case CHALLENGED:
            case HANDSHAKE:
                if (authScheme == null) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Auth scheme is null");
                    }
                    authStrategy.authFailed(host, null, context);
                    authState.reset();
                    authState.setState(AuthProtocolState.FAILURE);
                    return false;
                }
            case UNCHALLENGED:
                if (authScheme != null) {
                    final String id = authScheme.getSchemeName();
                    final Header challenge = challenges.get(id.toLowerCase(Locale.ENGLISH));
                    if (challenge != null) {
                        if (Log.isLoggable(TAG, Log.DEBUG)) {
                            Log.d(TAG, "Authorization challenge processed");
                        }
                        authScheme.processChallenge(challenge);
                        if (authScheme.isComplete()) {
                            if (Log.isLoggable(TAG, Log.DEBUG)) {
                                Log.d(TAG, "Authentication failed");
                            }
                            authStrategy.authFailed(host, authState.getAuthScheme(), context);
                            authState.reset();
                            authState.setState(AuthProtocolState.FAILURE);
                            return false;
                        } else {
                            authState.setState(AuthProtocolState.HANDSHAKE);
                            return true;
                        }
                    } else {
                        authState.reset();
                        // Retry authentication with a different scheme
                    }
                }
            }
            final Queue<AuthOption> authOptions = authStrategy.select(challenges, host, response, context);
            if (authOptions != null && !authOptions.isEmpty()) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Selected authentication options: " + authOptions);
                }
                authState.setState(AuthProtocolState.CHALLENGED);
                authState.update(authOptions);
                return true;
            } else {
                return false;
            }
        } catch (final MalformedChallengeException ex) {
            if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "Malformed challenge: " +  ex.getMessage());
            }
            authState.reset();
            return false;
        }
    }

    public void generateAuthResponse(
            final HttpRequest request,
            final AuthStateHC4 authState,
            final HttpContext context) throws HttpException, IOException {
        AuthScheme authScheme = authState.getAuthScheme();
        Credentials creds = authState.getCredentials();
        switch (authState.getState()) {
        case FAILURE:
            return;
        case SUCCESS:
            ensureAuthScheme(authScheme);
            if (authScheme.isConnectionBased()) {
                return;
            }
            break;
        case CHALLENGED:
            final Queue<AuthOption> authOptions = authState.getAuthOptions();
            if (authOptions != null) {
                while (!authOptions.isEmpty()) {
                    final AuthOption authOption = authOptions.remove();
                    authScheme = authOption.getAuthScheme();
                    creds = authOption.getCredentials();
                    authState.update(authScheme, creds);
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Generating response to an authentication challenge using "
                                + authScheme.getSchemeName() + " scheme");
                    }
                    try {
                        final Header header = doAuth(authScheme, creds, request, context);
                        request.addHeader(header);
                        break;
                    } catch (final AuthenticationException ex) {
                        if (Log.isLoggable(TAG, Log.WARN)) {
                            Log.w(TAG, authScheme + " authentication error: " + ex.getMessage());
                        }
                    }
                }
                return;
            } else {
                ensureAuthScheme(authScheme);
            }
        }
        if (authScheme != null) {
            try {
                final Header header = doAuth(authScheme, creds, request, context);
                request.addHeader(header);
            } catch (final AuthenticationException ex) {
                if (Log.isLoggable(TAG, Log.ERROR)) {
                    Log.e(TAG, authScheme + " authentication error: " + ex.getMessage());
                }
            }
        }
    }

    private void ensureAuthScheme(final AuthScheme authScheme) {
        Asserts.notNull(authScheme, "Auth scheme");
    }

    @SuppressWarnings("deprecation")
    private Header doAuth(
            final AuthScheme authScheme,
            final Credentials creds,
            final HttpRequest request,
            final HttpContext context) throws AuthenticationException {
        if (authScheme instanceof ContextAwareAuthScheme) {
            return ((ContextAwareAuthScheme) authScheme).authenticate(creds, request, context);
        } else {
            return authScheme.authenticate(creds, request);
        }
    }

}
