package gs.zenodotus.back.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "EditionItem")
public class EditionItem extends Model {

    @Column
    public String description;

    @Column
    public Language language;

    @Column(index = true)
    public Work work;

    public EditionItem() {
        super();
    }

    public EditionItem(String description, Language language, Work work) {
        this.description = description;
        this.language = language;
        this.work = work;
    }
}