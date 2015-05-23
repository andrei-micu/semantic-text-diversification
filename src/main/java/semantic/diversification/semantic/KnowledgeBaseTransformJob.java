package semantic.diversification.semantic;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import semantic.diversification.model.NewsEntry;
import semantic.diversification.persistence.JenaModelStorage;
import semantic.diversification.persistence.NewsEntryStorage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class KnowledgeBaseTransformJob implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeBaseTransformJob.class);

    private OpenCalaisRestCaller openCalaisRestCaller = new OpenCalaisRestCaller();

    @Override
    public void run() {
        try {
            List<NewsEntry> newsEntries = NewsEntryStorage.retrieveAll();

            int totalEntries = newsEntries.size();
            int currentEntry = 0;

            for (NewsEntry newsEntry : newsEntries) {
                LOG.info("Processing news entry {} ({}/{}).", newsEntry.getId(), ++currentEntry, totalEntries);
                processNewsEntry(newsEntry);
            }
        } catch (IOException ex) {
            LOG.error("Exception occurred while transforming raw text to knowledge base.", ex);
        }
    }

    private void processNewsEntry(NewsEntry newsEntry) {
        String idString = String.valueOf(newsEntry.getId());
        String content = newsEntry.getTitle() + ". " + newsEntry.getDescription();
        ByteArrayInputStream contentStream = new ByteArrayInputStream(content.getBytes());

        try {
            if (!JenaModelStorage.isModel(idString)) {
                InputStream response = openCalaisRestCaller.toRDF(contentStream);

                Model jenaModel = ModelFactory.createDefaultModel();
                jenaModel.read(response, null);
                JenaModelStorage.putModel(idString, jenaModel);

                LOG.info("Successfully stored news entry {}.", newsEntry.getId());
            } else {
                LOG.info("Skipping news entry {} because it already exists.", newsEntry.getId());
            }

        } catch (Exception e) {
            LOG.error("Exception occurred while processing news entry {}.", newsEntry.getId(), e);
        }
    }
}
