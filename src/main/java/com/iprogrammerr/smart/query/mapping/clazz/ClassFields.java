package com.iprogrammerr.smart.query.mapping.clazz;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClassFields {

    public final Map<Field, Type> fieldTypes = new LinkedHashMap<>();

    public void putPrimitive(Field field) {
        fieldTypes.put(field, Type.PRIMITIVE);
    }

    public void putObject(Field field) {
        fieldTypes.put(field, Type.OBJECT);
    }

    public int size() {
        return fieldTypes.size();
    }

    @Override
    public String toString() {
        return "ClassFields{" +
            "fieldTypes=" + fieldTypes +
            '}';
    }

    public enum Type {
        PRIMITIVE, OBJECT
    }
}