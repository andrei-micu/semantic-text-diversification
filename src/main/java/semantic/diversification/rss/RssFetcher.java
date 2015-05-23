package semantic.diversification.rss;

import com.google.common.base.Throwables;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import semantic.diversification.model.NewsEntry;
import semantic.diversification.persistence.NewsEntryStorage;

import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class RssFetcher {
    private static final Logger LOG = LoggerFactory.getLogger(RssFetcher.class);

    public List<NewsEntry> fetch(String sourceName, URL inputURL) {
        SyndFeed feed = null;
        try {
            SyndFeedInput input = new SyndFeedInput();
            feed = input.build(new XmlReader(inputURL));
        } catch (Exception e) {
            LOG.error("Exception occurred while retrieving the RSS data for <{}>", sourceName, e);
            throw Throwables.propagate(e);
        }

        try {
            return feed.getEntries().stream()
                    .map(x -> new NewsEntry(
                            NewsEntryStorage.getAvailableId(),
                            x.getTitle(),
                            cleanDescription(x.getDescription().getValue()),
                            sourceName,
                            new DateTime()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Exception occurred while converting the RSS data for <{}>", sourceName, e);
            throw Throwables.propagate(e);
        }
    }

    private String cleanDescription(String value) {
        return value.replaceAll("\\<.*?>","").trim();
    }
}
