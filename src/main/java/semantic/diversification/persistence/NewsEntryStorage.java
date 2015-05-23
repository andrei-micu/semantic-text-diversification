package semantic.diversification.persistence;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import semantic.diversification.model.NewsEntry;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

public class NewsEntryStorage {
    private static final Logger LOG = LoggerFactory.getLogger(NewsEntryStorage.class);

    private static final String STORAGE_PATH = "working-files/raw";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("YYYY-MM-dd");
    private static final Random RANDOM = new Random();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.registerModule(new JodaModule());
        MAPPER.configure(WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private static final JavaType COLLECTION_TYPE =
            MAPPER.getTypeFactory().constructCollectionType(List.class, NewsEntry.class);

    private static final Set<Long> ID_SET = Sets.newTreeSet();

    static {
        try {
            retrieveAll().stream().forEach(x -> {
                Long id = x.getId();
                if (ID_SET.contains(id)) {
                    throw new RuntimeException("Id " + x.getId() + " is a duplicate.");
                }
                ID_SET.add(id);
            });
        } catch (Exception e) {
            LOG.error("Exception occurred while reading the existing news entries.", e);
        }
    }

    public static void store(String source, DateTime date, List<NewsEntry> entries) throws IOException {
        String folderPath = STORAGE_PATH + "/" + date.toString(DATE_FORMATTER);
        String fileName = source + ".json";

        new File(folderPath).mkdirs();

        File file = new File(folderPath + "/" + fileName);
        file.createNewFile();

        MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, entries);
    }

    public static List<NewsEntry> retrieveAll() throws IOException {
        List<NewsEntry> results = Lists.newArrayList();
        for (File file : FileUtils.listFiles(new File(STORAGE_PATH), new String[]{"json"}, true)) {
            results.addAll(MAPPER.readValue(file, COLLECTION_TYPE));
        }
        return results;
    }

    public static long getAvailableId() {
        do {
            long randomLong = RANDOM.nextLong();
            if (!ID_SET.contains(randomLong)) {
                return randomLong;
            }
        } while (true);
    }

    public static void resetIdsAndSanitizeDates() {
        try {
            List<NewsEntry> newsEntryList = NewsEntryStorage.retrieveAll();
            newsEntryList.stream().forEach(x -> {
                x.setId(NewsEntryStorage.getAvailableId());
                x.setDate(x.getDate().withMillisOfDay(0));
            });

            Map<DateTime, List<NewsEntry>> map = newsEntryList.stream().collect(Collectors.groupingBy(NewsEntry::getDate));
            map.entrySet().stream().forEach(x -> {
                Map<String, List<NewsEntry>> map2 = x.getValue().stream().collect(Collectors.groupingBy(NewsEntry::getSource));
                map2.entrySet().stream().forEach(y -> {
                    try {
                        NewsEntryStorage.store(y.getKey(), x.getKey(), y.getValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
