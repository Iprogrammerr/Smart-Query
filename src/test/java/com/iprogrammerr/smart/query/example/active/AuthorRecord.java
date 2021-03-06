package com.iprogrammerr.smart.query.example.active;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.active.ActiveRecord;
import com.iprogrammerr.smart.query.active.UpdateableColumn;
import com.iprogrammerr.smart.query.example.table.Author;
import com.iprogrammerr.smart.query.mapping.Mappings;

public class AuthorRecord extends ActiveRecord<Integer, Author> {

    public AuthorRecord(QueryFactory factory, Integer id) {
        super(factory, Author.TABLE, new UpdateableColumn<>(Author.ID, id), Integer.class, true,
            new UpdateableColumn<>(Author.NAME), new UpdateableColumn<>(Author.ALIAS));
    }

    public AuthorRecord(QueryFactory factory) {
        this(factory, null);
    }

    @Override
    public Author fetch() {
        return fetchQuery().fetch(Mappings.ofClass(Author.class));
    }

    public AuthorRecord setName(String name) {
        set(Author.NAME, name);
        return this;
    }

    public AuthorRecord setAlias(String alias) {
        set(Author.ALIAS, alias);
        return this;
    }
}
