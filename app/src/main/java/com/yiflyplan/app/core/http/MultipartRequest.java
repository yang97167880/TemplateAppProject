/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.yiflyplan.app.core.http;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.yiflyplan.app.core.http.entity.MultipartEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mrsimple
 */
public class MultipartRequest extends Request<String> {

    MultipartEntity mMultiPartEntity = new MultipartEntity();

    Map<String, String> mHeaders = new HashMap<String, String>();

    private final Response.Listener<String> mListener;

    /**
     * Creates a new request with the given url.
     *
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     */
    public MultipartRequest(String url, Response.Listener<String> listener) {
        this(url, listener, null);
    }

    /**
     * Creates a new POST request.
     *
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public MultipartRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
    }

    /**
     * @return
     */
    public MultipartEntity getMultiPartEntity() {
        return mMultiPartEntity;
    }

    @Override
    public String getBodyContentType() {
        return mMultiPartEntity.getContentType().getValue();
    }

    public void addHeader(String key, String value) {
        mHeaders.put(key, value);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

    @Override
    public byte[] getBody() {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // multipart body
            mMultiPartEntity.writeTo(bos);
        } catch (IOException e) {
            Log.e("", "IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed = "";
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

}