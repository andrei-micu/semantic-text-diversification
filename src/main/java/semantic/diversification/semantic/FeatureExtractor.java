package semantic.diversification.semantic;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FeatureExtractor {
    private static final Query QUERY;

    static {
        String queryString = "";
        try {
            queryString = IOUtils.toString(FeatureExtractor.class.getResourceAsStream("/queries/entities-categories.sparql"));
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        QUERY = QueryFactory.create(queryString);
    }

    public static Map<String, Set<String>> extractFeatures(Map<String, Model> modelMap) {
        return modelMap
                .entrySet()
                .stream()
                .map(x -> new AbstractMap.SimpleImmutableEntry<>(x.getKey(), extractEntitiesAndCategories(x.getValue())))
                .filter(x -> !x.getValue().isEmpty())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    private static Set<String> extractEntitiesAndCategories(Model model) {
        QueryExecution queryExecution = QueryExecutionFactory.create(QUERY, model);
        com.hp.hpl.jena.query.ResultSet resultSet = queryExecution.execSelect();

        Set<String> results = Sets.newTreeSet();
        resultSet.forEachRemaining(x -> results.add(x.get("result").toString().trim()));
        return results;
    }
}
