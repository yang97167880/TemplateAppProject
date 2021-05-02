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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class FormRequest extends Request<String> {
    private Response.ErrorListener errorListener;
    private static final String BOUNDARY = "--------------520-13-14"; //数据分隔线
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private List<FormField> fieldList;

    public FormRequest(int method, String url, Response.ErrorListener listener, List<FormField> fieldList) {
        super(method, url, listener);
        this.errorListener = listener;
        this.fieldList = fieldList;
        setShouldCache(false);
        //设置请求的响应事件，因为文件上传需要较长的时间，所以在这里加大了，设为5秒
        setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String parseResult =
                    new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
            Log.v("FromRequest", "====parseResult===" + parseResult);
            return Response.success(parseResult,
                    HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String s) {
        errorListener.onErrorResponse(new VolleyError(s));
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (fieldList == null || fieldList.size() == 0) {
            return super.getBody();
        }
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            for (FormField formField : fieldList) {
                StringBuilder fieldBuilder = new StringBuilder();
                fieldBuilder.append("--" + BOUNDARY);
                fieldBuilder.append("\r\n");
                /*第二行*/
                //Content-Disposition: form-data; name="参数的名称"; filename="上传的文件名" + "\r\n"
                fieldBuilder.append("Content-Disposition: form-data;");
                fieldBuilder.append(" name=\"").append(formField.getFieldName()).append("\";");
                if (formField.getExtras() != null && formField.getExtras().length > 0) {
                    FormField.Pair[] extras = formField.getExtras();
                    for (FormField.Pair extra : extras) {
                        fieldBuilder.append(extra.getKey()).append("\"").append(extra.getValue()).append("\"");
                    }
                }
                if (formField.getContentType() != null) {
                    fieldBuilder.append("\r\n").append("Content-Type: ").append(formField.getContentType()).append("\r\n");
                }

                fieldBuilder.append("\r\n");
                ObjectOutputStream objectOutputStream = null;
                try {
                    byteArrayOutputStream.write(fieldBuilder.toString().getBytes("utf-8"));
                    objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                    objectOutputStream.writeObject(formField.getFieldValue());
                    objectOutputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (objectOutputStream != null) {
                            objectOutputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            String endLine = "--" + BOUNDARY + "--" + "\r\n";
            byteArrayOutputStream.write(endLine.getBytes("utf-8"));
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new AuthFailureError("form转换失败");
    }

    @Override
    public String getBodyContentType() {
        return MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY;
    }
}
