package semantic.diversification;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.commons.io.IOUtils;
import semantic.diversification.persistence.JenaModelStorage;
import semantic.diversification.rss.RssJobs;
import semantic.diversification.semantic.KnowledgeBaseTransformJob;
import semantic.diversification.semantic.ModelExtractor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Starting point for this software
 */

public class SemanticDiversificationMain {

    public static void main(String[] args) {
        RssJobs.runAll();
        new KnowledgeBaseTransformJob().run();
/*
        try {
            Map<String, Model> map = JenaModelStorage.getAllModels();
            Model model = map.get("11843500891183553");
            System.out.print(ModelExtractor.getEntitiesAndCategories(model));
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }
}
