/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.auth.api

import raw.utils.Uid

/**
 * The following methods exist to allow a user of the frontend to generate a clientid/clientsecret
 * which can be used from the Python API. In Auth0, this corresponds to creating an Application
 * of type Machine to Machine, also called non-interactive. This is a separate entity of an
 * interactive user, who logs in using username/password.
 *
 * The user is linked to the Application by storing the user ID in the metadata of the Application
 * (key userID). The application is named "DelegateApp <userID>". Since users should have at most
 * one client id/secret pair, they should have at most one Application. As Auth0 does not enforce
 * unique applications names, we do it on our side.
 *
 * To see the applications, go to the Auth0 console, in the Application tab.
 *
 * Notes:
 * - We are using two tenants in Auth0 (independent groups of API, applications and users):
 * raw-test2.eu.auth0.com and raw.eu.auth0.com. So if you don't find the application, make sure
 * you are on the right tenant. The tests and the scripts that launch the local servers are all
 * using the raw-test2.eu.auth0.com tenant. The production
 * servers are running with the raw.eu.auth0.com tenant. In the Auth0 console, the option to switch
 * tenant is on a menu on the top-right.
 *
 * - When the Python Client authenticates, it first contacts the Auth0 servers to generate a JWT
 * token from the client id/secret (go here https://jwt.io/ to decode the JWT token). The JWT
 * is self-contained, that is, the RAW server will authenticate the client without contacting the
 * Auth0 servers, by verifying the signature and the validity period encoded in the token.
 * The validity period of the tokens is limited by default to 10h. After this, the RAW Server will
 * reject the token and the client has to again contact the Auth0 servers to generate a new token.
 *
 * - The Python client caches the JWT token in /tmp/rawserver.token.[RANDOMSTRING]. Therefore,
 * if the client id/client secret are invalidated (or deleted), the token will continue to
 * be accepted for the duration of its validity. To test that the deletion really worked, also
 * delete the cached token in /tmp. To see if the python client is using a cached token or requesting
 * a new one, run rawcli with --verbose.
 */
final case class DelegatedApplication(userID: Uid, clientID: String, clientSecret: String)
