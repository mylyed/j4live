package com.mylyed.j4live.protocol.amf;

import java.util.LinkedHashMap;

/**
 * 实际为有序map
 */
public class AMF0Object extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public static final String TYPED_OBJECT_KEY = "classname";

    public AMF0Object addProperty(String key, Object value) {
        put(key, value);
        return this;
    }
}
