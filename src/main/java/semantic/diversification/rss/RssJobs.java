package semantic.diversification.rss;


import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import semantic.diversification.model.NewsEntry;
import semantic.diversification.persistence.NewsEntryStorage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public enum RssJobs implements Runnable {
    CNN("CNN News", "http://rss.cnn.com/rss/cnn_topstories.rss"),
    NEW_YORK_TIMES("New York Times", "http://feeds.nytimes.com/nyt/rss/HomePage"),
    NPR_NEWS("NPR News", "http://www.npr.org/rss/rss.php"),
    REUTERS("Reuters Top News", "http://feeds.reuters.com/reuters/topNews"),
    BBC_NEWS("BBC News", "http://newsrss.bbc.co.uk/rss/newsonline_world_edition/americas/rss.xml");

    private static final Logger LOG = LoggerFactory.getLogger(RssFetcher.class);

    private final String source;
    private final String url;

    private RssJobs(String source, String url) {
        this.source = source;
        this.url = url;
    }

    @Override
    public void run() {
        List<NewsEntry> newsEntries;
        try {
            newsEntries = new RssFetcher().fetch(source, new URL(url));
        } catch (MalformedURLException e) {
            LOG.error("Error while fetching RSS data.", e);
            throw Throwables.propagate(e);
        }

        try {
            NewsEntryStorage.store(source, new DateTime(), newsEntries);
        } catch (IOException e) {
            LOG.error("Error while storing RSS data.", e);
            throw Throwables.propagate(e);
        }
    }

    public static void runAll(){
        Lists.newArrayList(values()).stream().forEach(x -> x.run());
    }
}
