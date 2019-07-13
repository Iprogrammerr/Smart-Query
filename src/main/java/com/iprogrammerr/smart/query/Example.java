package com.iprogrammerr.smart.query;

import java.sql.Connection;

public class Example {

    public static void main(String... args) {
        Connection connection = null;
        new SmartQuery(connection)
            .dsl()
            .selectAll().from("author")
            .build()
            .fetch(r -> {
                r.next();
                return r.getString(1);
            });
    }
}
