package gs.zenodotus.back.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Author")
public class Author extends Model {

    @Column(index = true)
    public String name;

    public Author() {
        super();
    }

    public Author(String name) {
        this.name = name;
    }

}