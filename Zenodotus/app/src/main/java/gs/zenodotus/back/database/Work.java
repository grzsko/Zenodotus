package gs.zenodotus.back.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Work")
public class Work extends Model {

    @Column
    public String title;

    @Column(index = true)
    public Author category;

    @Column
    public String urn;

    @Column
    public Language language;

    public Work() {
        super();
    }

    public Work(String title, Author category, String urn, Language language) {
        super();
        this.title = title;
        this.category = category;
        this.urn = urn;
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }
}
