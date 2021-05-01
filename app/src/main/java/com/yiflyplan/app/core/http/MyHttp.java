package com.yiflyplan.app.core.http;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yiflyplan.app.MyApp;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于http请求
 */
public final class MyHttp {
    private static RequestQueue mQueue;



    private static final String Message = "message";
    private static final String SUCCESS = "success";
    private static final String DATA = "data";

    private static final String API = "http://118.190.97.125:8080";

    static {
        // 创建请求队列对象，自动管理请求对象
        mQueue = Volley.newRequestQueue(MyApp.getContext());
        // 添加自动cookie管理，不要手动在getHeader中添加cookie
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
    }

    // 回调接口，在done函数中处理后端返回的数据
    public interface Callback {
        void success(JSONObject data) throws JSONException;

        void fail(JSONObject error) throws JSONException;
    }

    /**
     * String请求，普通请求
     */
    // 参数为：请求方式、请求路径（前面默认添加API）、请求参数列表、返回结果回调接口
    // 请求参数列表使用LinkedHashMap，确保添加顺序和读取顺序一致
    private static void request(int method, final String url,final String token, final LinkedHashMap<String, String> params, final Callback callback) {
        //生成请求对象
        StringRequest jsonObjectRequest = new StringRequest(
                method,//请求方式，GET、POST
                API + url,//请求url
                new Response.Listener<String>() {
                    // 成功返回
                    @Override
                    public void onResponse(String response) {// 获得后端返回的json数据，包括status和data两部分
                        try {
                            if(response != null && response.startsWith("\ufeff")) {
                                response = response.substring(1);
                            }
                            JSONObject res = new JSONObject(response);//将返回数据转化为json
                            // 业务处理成功，status字段值为success

                            if (SUCCESS.equals(res.getString(Message))) {
                                // 获得后端json中的data部分
                                JSONObject data = res.getJSONObject(DATA);
                                callback.success(data);
                            } else {// 业务处理中产生的异常
                                Log.e("TAG", res.getString(Message));
                                XToastUtils.error(res.getString(Message));
                                callback.fail(res);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                // 请求过程的出错处理
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                        try{
                            JSONObject err = new JSONObject("err:"+error.getMessage());
                            callback.fail(err);
                        }catch (Exception e){
                            Log.e("Ex:",e.getMessage());
                        }
                        XToastUtils.error("请求失败！请检查网络问题。");
                    }
                }) {
            // 对返回的数据的编码格式进行处理
            // 本身返回的是字符串，由于返回处理函数参数为JSONObject，需要进行转换
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    //获取字符串信息
                    String jsonString = new String(response.data, StandardCharsets.UTF_8);
                    //后端不返回数据时，返回数据中的data值为null，但是JSONObject不能自动生成空对象，所以需要转换
                    jsonString = jsonString.replaceAll("null", "{}");
                    //返回由字符串生成的JSONObject
                    return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                LinkedHashMap<String, String> headers = new LinkedHashMap<>();
                headers.put("Charset", "UTF-8");
                headers.put("Content-Type", "application/json;charset=utf-8");
                headers.put("Accept-Encoding", "gzip,deflate");
                headers.put("Authorization",token);
                return headers;
            }
        };
        // 超时设置,10秒超时，失败后不重试
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 1.0f));
        //将请求放入请求队列等待发送
        mQueue.add(jsonObjectRequest);
    }

    // 使用POST模式传参给后端，并获得后端返回数据（修改数据）
    public static void post(final String url,final String token, final LinkedHashMap<String, String> params, final Callback callback) {
        request(Request.Method.POST, url,token, params, callback);
    }

    /**
     * json请求
     */
    // 参数为：请求方式、请求路径（前面默认添加API）、请求参数列表、返回结果回调接口
    // 请求参数列表使用LinkedHashMap，确保添加顺序和读取顺序一致
    private static void requestJson(int method, final String url,final String token, final LinkedHashMap<String, ?> params, final Callback callback) {
        //JSONObject请求不支持Map传递参数列表，需要自行构造请求参数对象，如果没有参数，则为null
        JSONObject jsonRequest = (params == null || params.isEmpty()) ? null : new JSONObject(params);
        //生成请求对象
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                method,//请求方式，GET、POST
                API + url,//请求url
                jsonRequest,//请求参数列表
                new Response.Listener<JSONObject>() {
                    // 成功返回
                    @Override
                    public void onResponse(JSONObject response) {// 获得后端返回的json数据，包括status和data两部分
                        try {
                            // 业务处理成功，success字段值为success
                            if (SUCCESS.equals(response.getString(Message))) {
                                JSONObject data = response.getJSONObject(DATA);
                                callback.success(data);
                            } else {// 业务处理中产生的异常
                                Log.e("TAG", response.getString(Message));
                                XToastUtils.error(response.getString(Message));
                                callback.fail(response);
                            }
                        } catch (JSONException e) {
                            XToastUtils.error(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                // 请求过程的出错处理
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAGERR", error.getMessage(), error);
                        try{
                            JSONObject err = new JSONObject("{err:"+error.getMessage()+"}");
                            callback.fail(err);
                        }catch (Exception e){
                            Log.e("Ex:",e.getMessage());
                        }
                        XToastUtils.error("请求失败！请检查网络问题。");
                    }
                }) {
            // 对返回的数据的编码格式进行处理
            // 本身返回的是字符串，由于返回处理函数参数为JSONObject，需要进行转换
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    //获取字符串信息
                    String jsonString = new String(response.data, StandardCharsets.UTF_8);
                    //后端不返回数据时，返回数据中的data值为null，但是JSONObject不能自动生成空对象，所以需要转换
                    jsonString = jsonString.replace("\"data\":null", "\"data\":{}");
                    //返回由字符串生成的JSONObject
                    return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                LinkedHashMap<String, String> headers = new LinkedHashMap<>();
                headers.put("Charset", "UTF-8");
                headers.put("Content-Type", "application/json;charset=utf-8");
                headers.put("Accept-Encoding", "gzip,deflate");
                headers.put("Authorization",token);
                return headers;
            }

        };
        // 超时设置,10秒超时，失败后不重试
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 1.0f));
        //将请求放入请求队列等待发送
        mQueue.add(jsonObjectRequest);
    }

    // 参数为：请求方式、请求路径（前面默认添加API）、请求参数列表、返回结果回调接口
    // 请求参数列表使用LinkedHashMap，确保添加顺序和读取顺序一致
    private static void requestJsonByForm(int method, final String url,final String token, final LinkedHashMap<String, ?> params, final Callback callback) {
        //JSONObject请求不支持Map传递参数列表，需要自行构造请求参数对象，如果没有参数，则为null
        JSONObject jsonRequest = (params == null || params.isEmpty()) ? null : new JSONObject(params);
        //生成请求对象
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                method,//请求方式，GET、POST
                API + url,//请求url
                jsonRequest,//请求参数列表
                new Response.Listener<JSONObject>() {
                    // 成功返回
                    @Override
                    public void onResponse(JSONObject response) {// 获得后端返回的json数据，包括status和data两部分
                        try {
                            // 业务处理成功，success字段值为success
                            if (SUCCESS.equals(response.getString(Message))) {
                                JSONObject data = response.getJSONObject(DATA);
                                callback.success(data);
                            } else {// 业务处理中产生的异常
                                Log.e("TAG", response.getString(Message));
                                XToastUtils.error(response.getString(Message));
                                callback.fail(response);
                            }
                        } catch (JSONException e) {
                            XToastUtils.error(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                // 请求过程的出错处理
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAGERR", error.getMessage(), error);
                        try{
                            JSONObject err = new JSONObject("{err:"+error.getMessage()+"}");
                            callback.fail(err);
                        }catch (Exception e){
                            Log.e("Ex:",e.getMessage());
                        }
                        XToastUtils.error("请求失败！请检查网络问题。");
                    }
                }) {
            // 对返回的数据的编码格式进行处理
            // 本身返回的是字符串，由于返回处理函数参数为JSONObject，需要进行转换
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    //获取字符串信息
                    String jsonString = new String(response.data, StandardCharsets.UTF_8);
                    //后端不返回数据时，返回数据中的data值为null，但是JSONObject不能自动生成空对象，所以需要转换
                    jsonString = jsonString.replace("\"data\":null", "\"data\":{}");
                    //返回由字符串生成的JSONObject
                    return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                LinkedHashMap<String, String> headers = new LinkedHashMap<>();
                headers.put("Charset", "UTF-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Accept-Encoding", "gzip,deflate");
                headers.put("Authorization",token);
                return headers;
            }

        };
        // 超时设置,10秒超时，失败后不重试
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 1.0f));
        //将请求放入请求队列等待发送
        mQueue.add(jsonObjectRequest);
    }

    private static void requestJsonArray(int method, final String url,final String token, final LinkedHashMap<String, ?> params, final Callback callback) {
        //JSONObject请求不支持Map传递参数列表，需要自行构造请求参数对象，如果没有参数，则为null
        JSONObject jsonRequest = (params == null || params.isEmpty()) ? null : new JSONObject(params);
        //生成请求对象
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                method,//请求方式，GET、POST
                API + url,//请求url
                jsonRequest,//请求参数列表
                new Response.Listener<JSONObject>() {
                    // 成功返回
                    @Override
                    public void onResponse(JSONObject response) {// 获得后端返回的json数据，包括status和data两部分
                        try {
                            // 业务处理成功，success字段值为success
                            if (SUCCESS.equals(response.getString(Message))) {
                                callback.success(response);
                            } else {// 业务处理中产生的异常
                                Log.e("TAG", response.getString(Message));
                                XToastUtils.error(response.getString(Message));
                                callback.fail(response);
                            }
                        } catch (JSONException e) {
                            XToastUtils.error(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                // 请求过程的出错处理
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAGERR", error.getMessage(), error);
                        try{
                            JSONObject err = new JSONObject("{err:"+error.getMessage()+"}");
                            callback.fail(err);
                        }catch (Exception e){
                            Log.e("Ex:",e.getMessage());
                        }
                        XToastUtils.error("请求失败！请检查网络问题。");
                    }
                }) {
            // 对返回的数据的编码格式进行处理
            // 本身返回的是字符串，由于返回处理函数参数为JSONObject，需要进行转换
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    //获取字符串信息
                    String jsonString = new String(response.data, StandardCharsets.UTF_8);
                    //后端不返回数据时，返回数据中的data值为null，但是JSONObject不能自动生成空对象，所以需要转换
                    jsonString = jsonString.replace("\"data\":null", "\"data\":{}");
                    //返回由字符串生成的JSONObject
                    return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                LinkedHashMap<String, String> headers = new LinkedHashMap<>();
                headers.put("Charset", "UTF-8");
                headers.put("Content-Type", "application/json;charset=utf-8");
                headers.put("Accept-Encoding", "gzip,deflate");
                headers.put("Authorization",token);
                return headers;
            }

        };
        // 超时设置,10秒超时，失败后不重试
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 1.0f));
        //将请求放入请求队列等待发送
        mQueue.add(jsonObjectRequest);
    }
    // 使用POST模式传参给后端，并获得后端返回数据（修改数据）

    public static void postJson(final String url,final String token, final LinkedHashMap<String, ?> params, final Callback callback) {
        requestJson(Request.Method.POST, url,token, params, callback);
    }

    public static void postForm(final String url,final String token, final LinkedHashMap<String, ?> params, final Callback callback) {
        requestJsonByForm(Request.Method.POST, url,token, params, callback);
    }
    // 使用PUT模式传参给后端，并获得后端返回数据（修改数据）
    public static void putJson(final String url,final String token, final LinkedHashMap<String, ?> params, final Callback callback) {
        requestJson(Request.Method.PUT, url,token, params, callback);
    }

    // 使用GET模式获得后端返回数据，不传参给后端（查询数据）
    public static void getWithoutParams(final String url,final String token, final Callback callback) {
        requestJson(Request.Method.GET, url,token, null, callback);
    }

    // 使用GET模式传参给后端，并获得后端返回数据（查询数据）
    // GET模式不支持直接用Map传递参数列表，需要将参数列表拼接到请求链接上
    public static void get(final String url,final String token, final LinkedHashMap<String, String> params, final Callback callback) {
        getWithoutParams(appendParamsToUrl(url, params), token,callback);
    }

    // 使用GET模式获得后端返回数据，不传参给后端（查询数据）
    public static void getJsonList(final String url,final String token, final Callback callback) {
        requestJsonArray(Request.Method.GET, url,token, null, callback);
    }

    // 将参数列表添加到请求链接后面
    private static String appendParamsToUrl(final String url, final Map<String, String> params) {
        // 无参数直接返回原url
        if (params == null || params.isEmpty())
        {  return url;}

        // 构造新的请求链接
        StringBuilder sb = new StringBuilder(url);
        sb.append('?');
        // 遍历参数列表
        for (Map.Entry entry : params.entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append("&");
        }
        return sb.toString();
    }

    //将数组转为字符串，以便附加在请求链接后面
    public static String transArrayToStr(final String listName, final List list) {
        StringBuilder sb = new StringBuilder(listName.length() * list.size());
        for (Object item : list) {
            sb.append(listName).append('=').append(item).append('&');
        }
        return sb.toString();
    }

}
