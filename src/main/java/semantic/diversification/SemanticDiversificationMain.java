package semantic.diversification;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Model;
import semantic.diversification.clustering.ClusterDistance;
import semantic.diversification.clustering.Clusterer;
import semantic.diversification.clustering.EntryDistance;
import semantic.diversification.clustering.StopCriterion;
import semantic.diversification.persistence.JenaModelStorage;
import semantic.diversification.rss.RssJobs;
import semantic.diversification.semantic.FeatureExtractor;
import semantic.diversification.semantic.KnowledgeBaseTransformJob;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static semantic.diversification.clustering.EntryDistance.DIFFERENT_ITEMS;
import static semantic.diversification.clustering.EntryDistance.DIFFERENT_ITEMS_PERCENTAGE;
import static semantic.diversification.clustering.ClusterDistance.SINGLE_LINKAGE;
import static semantic.diversification.clustering.ClusterDistance.COMPLETE_LINKAGE;
import static semantic.diversification.clustering.ClusterDistance.AVERAGE_LINKAGE;

/**
 * Starting point for this software
 */

public class SemanticDiversificationMain {

    public static void main(String[] args) {
        //runAcquisitionJob();

        //runClusteringJob(DIFFERENT_ITEMS, SINGLE_LINKAGE, new StopCriterion(10, Double.MAX_VALUE), false);
        //runClusteringJob(DIFFERENT_ITEMS, COMPLETE_LINKAGE, new StopCriterion(10, Double.MAX_VALUE), false);
        //runClusteringJob(DIFFERENT_ITEMS, AVERAGE_LINKAGE, new StopCriterion(10, Double.MAX_VALUE), false);
        //runClusteringJob(DIFFERENT_ITEMS_PERCENTAGE, SINGLE_LINKAGE, new StopCriterion(10, Double.MAX_VALUE), false);
        //runClusteringJob(DIFFERENT_ITEMS_PERCENTAGE, COMPLETE_LINKAGE, new StopCriterion(10, Double.MAX_VALUE), false);
        //runClusteringJob(DIFFERENT_ITEMS_PERCENTAGE, AVERAGE_LINKAGE, new StopCriterion(10, Double.MAX_VALUE), false);
        //runClusteringJob(DIFFERENT_ITEMS, SINGLE_LINKAGE, new StopCriterion(1, 3d), false);
        //runClusteringJob(DIFFERENT_ITEMS, COMPLETE_LINKAGE, new StopCriterion(1, 3d), false);
        //runClusteringJob(DIFFERENT_ITEMS, AVERAGE_LINKAGE, new StopCriterion(1, 3d), false);
        //runClusteringJob(DIFFERENT_ITEMS_PERCENTAGE, SINGLE_LINKAGE, new StopCriterion(1, 20d), false);
        //runClusteringJob(DIFFERENT_ITEMS_PERCENTAGE, COMPLETE_LINKAGE, new StopCriterion(1, 20d), false);
        runClusteringJob(DIFFERENT_ITEMS_PERCENTAGE, AVERAGE_LINKAGE, new StopCriterion(1, 20d), false);

    }

    private static void runAcquisitionJob() {
        RssJobs.runAll();
        new KnowledgeBaseTransformJob().run();
    }

    private static void runClusteringJob(EntryDistance entryDistance,
                                         ClusterDistance clusterDistance,
                                         StopCriterion stopCriterion,
                                         boolean restrictedSet) {
        try {
            Map<String, Model> models = JenaModelStorage.getAllModels();

            if (restrictedSet) {
                models = Lists.newArrayList(Iterables.limit(models.entrySet(), 100))
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }

            Map<String, Set<String>> dataset = FeatureExtractor.extractFeatures(models);
            Clusterer clusterer = new Clusterer(dataset, entryDistance);
            List<String[]> results = clusterer.doClustering(clusterDistance, stopCriterion);
            clusterer.displayClusters(results);
            clusterer.displayDistances(results);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }
}
