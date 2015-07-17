package gs.zenodotus.back;

import java.util.List;

import gs.zenodotus.back.database.Author;
import gs.zenodotus.back.database.Work;

public class AuthorRow {

    public Author author;
    public List<Work> children;

    public AuthorRow() {
    }

    public AuthorRow(Author author, List<Work> books) {
        this.author = author;
        children = books;
    }
}