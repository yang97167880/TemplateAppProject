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
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.xuexiang.xhttp2.model.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FormRequest extends Request<String> {
    private final Response.ErrorListener errorListener;
    private final Response.Listener<String> successListener;
    private static final String BOUNDARY = "----WebKitFormBoundary5yUaMLXohbYiZp6E"; //数据分隔线
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private final List<FormField<?>> fieldList;
    private final String token;

    public FormRequest(int method, String url, Response.Listener<String> successListener, Response.ErrorListener listener, List<FormField<?>> fieldList, String token) {
        super(method, url, listener);
        this.successListener = successListener;
        this.errorListener = listener;
        this.fieldList = fieldList;
        this.token = token;
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
            Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(networkResponse);
            return Response.success(parseResult, cacheEntry);
        } catch (UnsupportedEncodingException e) {
            Log.e("ERROR", String.valueOf(e));
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String s) {
        this.successListener.onResponse(s);
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
                fieldBuilder.append(" name=\"").append(formField.getFieldName()).append("\"");
                if (formField.getExtras() != null && formField.getExtras().length > 0) {
                    fieldBuilder.append(";");
                    FormField.Pair[] extras = formField.getExtras();
                    for (FormField.Pair extra : extras) {
                        fieldBuilder.append(" ").append(extra.getKey()).append("=\"").append(extra.getValue()).append("\"");
                    }
                }
                if (formField.getContentType() != null && formField.getContentType().trim().length() > 0) {
                    fieldBuilder.append("\r\n").append("Content-Type: ").append(formField.getContentType());
                }

                fieldBuilder.append("\r\n\r\n");
                FileInputStream fileInputStream = null;
                try {
                    byteArrayOutputStream.write(fieldBuilder.toString().getBytes("utf-8"));
                    if (formField.getFieldValue() instanceof File) {
                        File file = (File) formField.getFieldValue();
                        byte[] bytes = new byte[(int) file.length()];
                        fileInputStream = new FileInputStream(file);
                        fileInputStream.read(bytes);
                        byteArrayOutputStream.write(bytes);
                        byteArrayOutputStream.write("\r\n".getBytes("utf-8"));
                    } else {
                        Object fieldValue = formField.getFieldValue();
                        if (fieldValue instanceof String) {
                            byteArrayOutputStream.write((fieldValue + "\r\n").getBytes("utf-8"));
                        } else {
                            throw new AuthFailureError("表单字段暂时不支持字符串以外的类型，请修改后重试");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fileInputStream != null) {
                            fileInputStream.close();
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

    @Override
    public Map<String, String> getHeaders() {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        if (token != null && token.trim().length() > 0) {
            headers.put("Authorization", token);
        }

        return headers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormRequest request = (FormRequest) o;
        return errorListener.equals(request.errorListener) &&
                fieldList.equals(request.fieldList) &&
                token.equals(request.token);
    }
}
