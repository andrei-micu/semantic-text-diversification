package semantic.diversification.semantic;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

public class ModelExtractor {
    private static final Query QUERY;

    static {
        String queryString = "";
        try {
            queryString = IOUtils.toString(ModelExtractor.class.getResourceAsStream("/queries/entities-categories.sparql"));
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        QUERY = QueryFactory.create(queryString);
    }

    public static List<String> getEntitiesAndCategories(Model model) {
        QueryExecution queryExecution = QueryExecutionFactory.create(QUERY, model);
        com.hp.hpl.jena.query.ResultSet resultSet = queryExecution.execSelect();

        //ResultSetFormatter.out(System.out, results, QUERY);

        List<String> results = Lists.newArrayList();
        resultSet.forEachRemaining(x -> results.add(x.get("result").toString()));
        return results;
    }
}
