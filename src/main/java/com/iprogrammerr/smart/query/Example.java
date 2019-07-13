package com.iprogrammerr.smart.query;

import java.sql.Connection;

public class Example {

    public static void main(String... args) {
        Connection connection = null;
        SmartQuery query = new SmartQuery(connection);
        query.dsl()
            .selectAll().from("author as a").innerJoin("book as b").on("a.id", "b.author_id")
            .build();
        System.out.println(query.template());
        System.out.println(query.values());
    }
}
