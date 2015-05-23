package semantic.diversification.model;

import org.joda.time.DateTime;


public class NewsEntry {
    private long id;
    private String title;
    private String description;
    private String source;
    private DateTime date;

    public NewsEntry() {
    }

    public NewsEntry(long id, String title, String description, String source, DateTime date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.source = source;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "NewsEntry{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", source='" + source + '\'' +
                ", date=" + date +
                '}';
    }
}
