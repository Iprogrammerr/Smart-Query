package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.TestDatabase;
import com.iprogrammerr.smart.query.example.AuthorWithBooksWithUsersWithPets;
import com.iprogrammerr.smart.query.example.BookWithUsersWithPets;
import com.iprogrammerr.smart.query.example.UserWithPet;
import com.iprogrammerr.smart.query.example.active.AuthorRecord;
import com.iprogrammerr.smart.query.example.active.BookRecord;
import com.iprogrammerr.smart.query.example.active.PetRecord;
import com.iprogrammerr.smart.query.example.active.UserRecord;
import com.iprogrammerr.smart.query.example.table.Author;
import com.iprogrammerr.smart.query.example.table.Book;
import com.iprogrammerr.smart.query.example.table.Pet;
import com.iprogrammerr.smart.query.example.table.User;
import com.iprogrammerr.smart.query.example.table.UserBook;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BiOneToManyMappingTest {

    private QueryFactory factory;

    @Before
    public void setup() {
        TestDatabase setup = new TestDatabase();
        setup.setup();
        factory = new SmartQueryFactory(setup.source());
    }

    @Test
    public void doesMapping() {
        AuthorWithBooksWithUsersWithPets expected = prepare();
        AuthorWithBooksWithUsersWithPets actual = factory.newQuery().dsl()
            .select("a.*", "b.id as bid", Book.AUTHOR_ID, Book.TITLE, "u.id as uid", "u.name as uname",
                "p.user_id as p_id", "p.name as p_name")
            .from(Author.TABLE).as("a")
            .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
            .innerJoin(UserBook.TABLE).as("ub").on("b.id", "ub.book_id")
            .innerJoin(User.TABLE).as("u").on("ub.user_id", "u.id")
            .innerJoin(Pet.TABLE).as("p").on("u.id", "p.user_id")
            .where(Author.ALIAS).equal().value(expected.author.alias)
            .query()
            .fetch(Mappings.biOneToMany(Author.class, Book.class, UserWithPet.class,
                (one, many) -> {
                    List<BookWithUsersWithPets> books = many.entrySet().stream()
                        .map(e -> new BookWithUsersWithPets(e.getKey(), e.getValue()))
                        .collect(Collectors.toList());
                    return new AuthorWithBooksWithUsersWithPets(one, books);
                }));

        MatcherAssert.assertThat(actual, Matchers.equalTo(expected));
    }

    private AuthorWithBooksWithUsersWithPets prepare() {
        Map<Book, List<UserWithPet>> records = new LinkedHashMap<>();

        AuthorRecord ar = new AuthorRecord(factory)
            .setName("Aristotle")
            .setAlias("Philosopher");
        ar.insert();

        BookRecord br1 = new BookRecord(factory)
            .setAuthorId(ar.getId())
            .setTitle("Politics");
        br1.insert();
        Book politics = br1.fetch();
        records.put(politics, new ArrayList<>());

        BookRecord br2 = new BookRecord(factory)
            .setAuthorId(ar.getId())
            .setTitle("Physics");
        br2.insert();
        Book physics = br2.fetch();
        records.put(physics, new ArrayList<>());

        UserRecord ur1 = new UserRecord(factory)
            .setName("Igor");
        ur1.insert();

        UserRecord ur2 = new UserRecord(factory)
            .setName("Olek");
        ur2.insert();

        factory.newQuery().dsl()
            .insertInto(UserBook.TABLE).values(ur1.getId(), politics.id)
            .query().end().dsl()
            .insertInto(UserBook.TABLE).values(ur1.getId(), physics.id)
            .query().end().dsl()
            .insertInto(UserBook.TABLE).values(ur2.getId(), physics.id)
            .query()
            .executeTransaction();

        PetRecord pr1 = new PetRecord(factory)
            .setName("Filemon")
            .setUserId(ur1.getId());
        pr1.insert();
        UserWithPet igor = new UserWithPet(ur1.fetch(), pr1.fetch());
        records.get(politics).add(igor);
        records.get(physics).add(igor);

        PetRecord pr2 = new PetRecord(factory)
            .setName("Gapek")
            .setUserId(ur2.getId());
        pr2.insert();
        UserWithPet olek = new UserWithPet(ur2.fetch(), pr2.fetch());
        records.get(physics).add(olek);

        List<BookWithUsersWithPets> books = records.entrySet().stream()
            .map(e -> new BookWithUsersWithPets(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
        return new AuthorWithBooksWithUsersWithPets(ar.fetch(), books);
    }
}
