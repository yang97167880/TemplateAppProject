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

package com.yiflyplan.app.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ReflectUtil {
    public static final class PrimitiveClassEnum {
        private static final Class<?> BYTE_TYPE = byte.class;
        private static final Class<?> CHAR_TYPE = char.class;
        private static final Class<?> BOOLEAN_TYPE = boolean.class;
        private static final Class<?> SHORT_TYPE = short.class;
        private static final Class<?> INT_TYPE = int.class;
        private static final Class<?> FLOAT_TYPE = float.class;
        private static final Class<?> DOUBLE_TYPE = double.class;
        private static final Class<?> LONG_TYPE = long.class;

        public static boolean isByte(Class<?> cls) {
            return BYTE_TYPE.equals(cls);
        }

        public static boolean isChar(Class<?> cls) {
            return CHAR_TYPE.equals(cls);
        }

        public static boolean isBoolean(Class<?> cls) {
            return BOOLEAN_TYPE.equals(cls);
        }

        public static boolean isShort(Class<?> cls) {
            return SHORT_TYPE.equals(cls);
        }

        public static boolean isInt(Class<?> cls) {
            return INT_TYPE.equals(cls);
        }

        public static boolean isFloat(Class<?> cls) {
            return FLOAT_TYPE.equals(cls);
        }

        public static boolean isDouble(Class<?> cls) {
            return DOUBLE_TYPE.equals(cls);
        }

        public static boolean isLong(Class<?> cls) {
            return LONG_TYPE.equals(cls);
        }
    }

    public static final class TypeDefaultValueEnum {
        private static final byte BYTE_DEFAULT = 0;
        private static final boolean BOOLEAN_DEFAULT = false;
        private static final short SHORT_DEFAULT = 0;
        private static final int INT_DEFAULT = 0;
        private static final float FLOAT_DEFAULT = 0.0f;
        private static final double DOUBLE_DEFAULT = 0.00d;
        private static final long LONG_DEFAULT = 0L;
        private static final String STRING_DEFAULT = " ";
        private static final Object OBJECT_DEFAULT = null;
    }

    public static <T> T convertToObject(JSONObject json, Class<T> cls) {
        try {
            T instance = cls.newInstance();
            //拿到所有字段
            List<Field> fieldList = new LinkedList<>();
            Class<?> superClass = cls;
            while (!superClass.equals(Object.class)) {
                Collections.addAll(fieldList, superClass.getDeclaredFields());
                Class<?> superclass = superClass.getSuperclass();
                if (superclass == null) {
                    break;
                }
                superClass = superclass;
            }
            //映射
            for (Field field : fieldList) {
                String fieldName = field.getName();
                Class<?> fieldType = field.getType();
                //整形
                try {
                    // Byte
                    if (PrimitiveClassEnum.isByte(fieldType) || fieldType.equals(Byte.class)) {
                        Number value = getValue(() -> json.getInt(fieldName), TypeDefaultValueEnum.BYTE_DEFAULT);
                        field.setAccessible(true);
                        field.set(instance, value.byteValue());
                    }
                    // Short
                    else if (PrimitiveClassEnum.isShort(fieldType) || fieldType.equals(Short.class)) {
                        Number value = getValue(() -> json.getInt(fieldName), TypeDefaultValueEnum.SHORT_DEFAULT);
                        field.setAccessible(true);
                        field.set(instance, value.shortValue());
                    }
                    // Int
                    else if (PrimitiveClassEnum.isInt(fieldType) || fieldType.equals(Integer.class)) {
                        Number value = getValue(() -> json.getInt(fieldName), TypeDefaultValueEnum.INT_DEFAULT);
                        field.setAccessible(true);
                        field.set(instance, value.intValue());
                    }
                    // Float
                    else if (PrimitiveClassEnum.isFloat(fieldType) || fieldType.equals(Float.class)) {
                        Number value = getValue(() -> json.getDouble(fieldName), TypeDefaultValueEnum.FLOAT_DEFAULT);
                        field.setAccessible(true);
                        field.set(instance, value.floatValue());
                    }
                    // Double
                    else if (PrimitiveClassEnum.isDouble(fieldType) || fieldType.equals(Double.class)) {
                        Number value = getValue(() -> json.getDouble(fieldName), TypeDefaultValueEnum.DOUBLE_DEFAULT);
                        field.setAccessible(true);
                        field.set(instance, value.doubleValue());
                    }
                    // Long
                    else if (PrimitiveClassEnum.isLong(fieldType) || fieldType.equals(Long.class)) {
                        Number value = getValue(() -> json.getLong(fieldName), TypeDefaultValueEnum.LONG_DEFAULT);
                        field.setAccessible(true);
                        field.set(instance, value.longValue());
                    }
                    // Char
                    else if (PrimitiveClassEnum.isChar(fieldType) || fieldType.equals(Character.class)) {
                        String value = getValue(() -> json.getString(fieldName), TypeDefaultValueEnum.STRING_DEFAULT);
                        field.setAccessible(true);
                        field.set(instance, value.charAt(0));
                    }
                    // Boolean
                    else if (PrimitiveClassEnum.isBoolean(fieldType) || fieldType.equals(Boolean.class)) {
                        boolean value = getValue(() -> json.getBoolean(fieldName), TypeDefaultValueEnum.BOOLEAN_DEFAULT);
                        field.setAccessible(true);
                        field.set(instance, value);
                    }
                    // Array
                    else if (fieldType.isArray()) {
                        JSONArray jsonArray = json.getJSONArray(fieldName);
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            Object value = convertToObject(jsonObj, fieldType);
                            field.setAccessible(true);
                            field.set(instance, value);
                        }
                    }
                    //String
                    else if (fieldType.equals(String.class)) {
                        String value = getValue(() -> json.getString(fieldName), TypeDefaultValueEnum.STRING_DEFAULT);
                        field.setAccessible(true);
                        field.set(instance, value);
                    }
                    // Object
                    else {
                        JSONObject jsonObj = json.getJSONObject(fieldName);
                        Object value = getValue(() -> convertToObject(jsonObj, fieldType), TypeDefaultValueEnum.OBJECT_DEFAULT);
                        field.setAccessible(true);
                        field.set(instance, value);
                    }
                }
                // 异常直接置空，不影响其他属性注入
                catch (Exception e) {
                    field.setAccessible(true);
                    field.set(instance, null);
                }
            }
            return instance;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> convertToList(JSONArray array, Class<T> cls) {
        if (array == null) {
            return new ArrayList<>();
        }
        int length = array.length();
        List<T> list = new ArrayList<>(length);
        try {
            for (int i = 0; i < length; i++) {
                JSONObject arrayJSONObject = array.getJSONObject(i);
                T value = convertToObject(arrayJSONObject, cls);
                list.add(value);
            }
        } catch (Exception ignored) {
        }
        return list;
    }

    private static <T> T getValue(Callable<T> callable, T defaultValue) {
        try {
            return callable.call();
        } catch (Exception e) {
            Log.e("【 Reflect Error 】", String.format("%s", e));
            return defaultValue;
        }
    }

    interface Callable<T> {
        T call() throws JSONException;
    }

}
