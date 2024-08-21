/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.protocol.HttpHeadersConfig;
import com.rawlabs.compiler.protocol.LocationConfig;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.RawContext;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.RawTruffleInternalErrorException;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.list.ListNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.list.ListNodesFactory;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.*;
import com.rawlabs.utils.core.RawSettings;
import com.rawlabs.utils.sources.bytestream.http.HttpByteStreamLocation;
import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.Map;
import scala.None$;
import scala.Option;
import scala.Some;
import scala.Tuple2;
import scala.collection.mutable.ArrayBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

@NodeInfo(shortName = "Location.FromHttp")
public class LocationFromHttpNode extends ExpressionNode {

  private String method;
  @Child private ExpressionNode url;
  @Child private ExpressionNode bodyString;
  @Child private ExpressionNode bodyBinary;
  @Child private ExpressionNode authCredentialName;
  @Child private ExpressionNode username;
  @Child private ExpressionNode password;
  @Child private ExpressionNode args;
  @Child private ExpressionNode headers;
  @Child private ExpressionNode expectedStatus;

  @Child private InteropLibrary argsInterops = InteropLibrary.getFactory().createDispatched(3);
  @Child private ListNodes.SizeNode argsSizeNode = ListNodesFactory.SizeNodeGen.create();
  @Child private ListNodes.GetNode argsGetNode = ListNodesFactory.GetNodeGen.create();

  @Child private InteropLibrary headersInterops = InteropLibrary.getFactory().createDispatched(3);
  @Child private ListNodes.SizeNode headersSizeNode = ListNodesFactory.SizeNodeGen.create();
  @Child private ListNodes.GetNode headersGetNode = ListNodesFactory.GetNodeGen.create();

  @Child private ListNodes.SizeNode expectedStatusSizeNode = ListNodesFactory.SizeNodeGen.create();
  @Child private ListNodes.GetNode expectedStatusGetNode = ListNodesFactory.GetNodeGen.create();

  public LocationFromHttpNode(
      String method,
      ExpressionNode url,
      ExpressionNode bodyString,
      ExpressionNode bodyBinary,
      ExpressionNode authCredentialName,
      ExpressionNode username,
      ExpressionNode password,
      ExpressionNode args,
      ExpressionNode headers,
      ExpressionNode expectedStatus) {
    this.method = method;
    this.url = url;
    this.bodyString = bodyString;
    this.bodyBinary = bodyBinary;
    this.authCredentialName = authCredentialName;
    this.username = username;
    this.password = password;
    this.args = args;
    this.headers = headers;
    this.expectedStatus = expectedStatus;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    try {
      String url = (String) this.url.executeGeneric(frame);

      // Build body
      Option<byte[]> maybeBody;
      if (this.bodyString != null) {
        maybeBody = new Some(((String) this.bodyString.executeGeneric(frame)).getBytes());
      } else if (this.bodyBinary != null) {
        maybeBody = new Some(((BinaryObject) this.bodyBinary.executeGeneric(frame)).getBytes());
      } else {
        maybeBody = None$.empty();
      }

      // Build args vector
      ClassTag<Tuple2<String, String>> tupleClassTag =
          (ClassTag<Tuple2<String, String>>) (ClassTag<?>) ClassTag$.MODULE$.apply(Tuple2.class);
      ArrayBuilder<Tuple2<String, String>> argsBuilder = new ArrayBuilder.ofRef(tupleClassTag);
      if (this.args != null) {
        Object value = this.args.executeGeneric(frame);
        int size = (int) this.argsSizeNode.execute(this, value);
        for (int i = 0; i < size; i++) {
          Object record = this.argsGetNode.execute(this, value, i);
          Object keys = this.argsInterops.getMembers(record);
          Object key =
              this.argsInterops.readMember(
                  record, (String) this.argsInterops.readArrayElement(keys, 0));
          Object val =
              this.argsInterops.readMember(
                  record, (String) this.argsInterops.readArrayElement(keys, 1));
          // ignore entries where key or val is null
          if (key != NullObject.INSTANCE && val != NullObject.INSTANCE) {
            argsBuilder =
                (ArrayBuilder<Tuple2<String, String>>)
                    argsBuilder.$plus$eq(Tuple2.apply((String) key, (String) val));
          }
        }
      }

      // Build headers vector
      ArrayBuilder<Tuple2<String, String>> headersBuilder = new ArrayBuilder.ofRef(tupleClassTag);
      if (this.headers != null) {
        Object value = this.headers.executeGeneric(frame);
        int size = (int) this.headersSizeNode.execute(this, value);
        for (int i = 0; i < size; i++) {
          Object record = this.headersGetNode.execute(this, value, i);
          Object keys = this.headersInterops.getMembers(record);
          Object key =
              this.headersInterops.readMember(
                  record, (String) this.headersInterops.readArrayElement(keys, 0));
          Object val =
              this.headersInterops.readMember(
                  record, (String) this.headersInterops.readArrayElement(keys, 1));
          // ignore entries where key or val is null
          if (key != NullObject.INSTANCE && val != NullObject.INSTANCE) {
            headersBuilder =
                (ArrayBuilder<Tuple2<String, String>>)
                    headersBuilder.$plus$eq(Tuple2.apply((String) key, (String) val));
          }
        }
      }

      // Append Authorization header if username and password are provided
      if (this.username != null && this.password != null) {
        String username = (String) this.username.executeGeneric(frame);
        String password = (String) this.password.executeGeneric(frame);
        headersBuilder =
            (ArrayBuilder<Tuple2<String, String>>)
                headersBuilder.$plus$eq(
                    Tuple2.apply(
                        "Authorization",
                        "Basic "
                            + Base64.getEncoder()
                                .encodeToString((username + ":" + password).getBytes())));
      }

      RawContext context = RawContext.get(this);

      // Append any additional headers related to the authentication (if credential name is defined)
      if (this.authCredentialName != null) {
        String authCredentialName = (String) this.authCredentialName.executeGeneric(frame);
        LocationConfig l = context.getLocationConfig(authCredentialName);
        if (l.hasHttpHeaders()) {
          HttpHeadersConfig l1 = l.getHttpHeaders();
          Map<String, String> credHeaders = l1.getHeadersMap();
          for (Map.Entry<String, String> entry : credHeaders.entrySet()) {
            headersBuilder =
                (ArrayBuilder<Tuple2<String, String>>)
                    headersBuilder.$plus$eq(Tuple2.apply(entry.getKey(), entry.getValue()));
          }
        } else {
          throw new RawTruffleInternalErrorException("credential is not an HTTP headers");
        }
      }

      // Build expected status vector
      int[] expectedStatusArray = {
        HttpURLConnection.HTTP_OK,
        HttpURLConnection.HTTP_ACCEPTED,
        HttpURLConnection.HTTP_CREATED,
        HttpURLConnection.HTTP_PARTIAL
      };
      if (this.expectedStatus != null) {
        Object value = this.expectedStatus.executeGeneric(frame);
        int size = (int) this.expectedStatusSizeNode.execute(this, value);
        expectedStatusArray = new int[size];
        for (int i = 0; i < size; i++) {
          expectedStatusArray[i] = (int) this.expectedStatusGetNode.execute(this, value, i);
        }
      }

      RawSettings rawSettings = context.getSettings();

      HttpByteStreamLocation location =
          new HttpByteStreamLocation(
              url,
              this.method,
              (Tuple2<String, String>[]) argsBuilder.result(),
              (Tuple2<String, String>[]) headersBuilder.result(),
              maybeBody,
              expectedStatusArray,
              rawSettings);

      return new LocationObject(location, url);
    } catch (UnsupportedMessageException
        | InvalidArrayIndexException
        | UnknownIdentifierException e) {
      throw new RawTruffleInternalErrorException(e, this);
    }
  }
}
