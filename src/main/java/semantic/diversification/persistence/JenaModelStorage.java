package semantic.diversification.persistence;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;

public class JenaModelStorage {
    private static final Logger LOG = LoggerFactory.getLogger(NewsEntryStorage.class);

    private static final String STORAGE_PATH = "working-files/jena-models";
    private static final String SERIALIZATION_SYNTAX = "Turtle";

    public static void putModel(String id, Model jenaModel) throws IOException {
        File outputFile = new File(STORAGE_PATH + "/" + id);
        try(Writer jenaOutput = new FileWriter(outputFile)) {
            jenaModel.write(jenaOutput, SERIALIZATION_SYNTAX);
        }
    }

    public static boolean isModel(String id) {
        File outputFile = new File(STORAGE_PATH + "/" + id);
        return outputFile.exists();
    }

    public static Map<String, Model> getAllModels() throws IOException {
        return FileUtils.listFiles(new File(STORAGE_PATH), null, true)
                .stream()
                .collect(Collectors.toMap(
                        (File::getName),
                        (x -> {
                            try (Reader reader = new FileReader(x)){
                                return ModelFactory.createDefaultModel().read(reader, null, SERIALIZATION_SYNTAX);
                            } catch (IOException e) {
                                LOG.error("Exception occurred while reading model from file {}.", x.getAbsoluteFile(), e);
                                return ModelFactory.createDefaultModel();
                            }
                        })
                ));
    }
}
