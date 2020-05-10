package com.example.rideshareapp;

import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;


class TokenRequest extends StringRequest {
    private String token =null;


    public TokenRequest(int method, String url, Response.Listener
            <String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);

    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        token = response.headers.get("AUTHORIZATION");
        if(token != null ) {
            return  Response.success(token, HttpHeaderParser.parseCacheHeaders(response));

        }
        else return Response.error(new ParseError());
    }
}