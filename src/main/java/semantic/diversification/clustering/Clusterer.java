package semantic.diversification.clustering;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Clusterer {
    private static final Logger LOG = LoggerFactory.getLogger(Clusterer.class);

    private final Map<String, Set<String>> dataset;
    private final Map<String, Map<String, Double>> entryDistances;

    public Clusterer(Map<String, Set<String>> dataset, EntryDistance entryDistance) {
        LOG.info("Started building clusterer with entry distance {}.", entryDistance.name());
        this.dataset = dataset;

        this.entryDistances = Maps.newHashMap();
        for (String id1 : dataset.keySet()) {
            for (String id2 : dataset.keySet()) {
                if (!this.entryDistances.containsKey(id1)) {
                    this.entryDistances.put(id1, Maps.newHashMap());
                }
                entryDistances.get(id1).put(id2, entryDistance.getDistance(dataset.get(id1), dataset.get(id2)));
            }
        }
        LOG.info("Successfully built clusterer with entry distance {}.", entryDistance.name());
    }

    public List<String[]> doClustering(ClusterDistance clusterDistance, StopCriterion stopCriterion) {
        LOG.info("Started clustering algorithm with cluster distance {}.", clusterDistance.name());

        List<String[]> clusters = Lists.newArrayList();

        for (String id : dataset.keySet()) {
            clusters.add(new String[]{id});
        }

        double minInterClusterDistance;

        do {
            minInterClusterDistance = Double.MAX_VALUE;
            int minDistanceI = 0;
            int minDistanceJ = 0;

            for (int i = 0; i < clusters.size() - 1 && minInterClusterDistance > 0d; i++) {
                for (int j = i + 1; j < clusters.size() && minInterClusterDistance > 0d; j++) {
                    double distance = clusterDistance.getDistance(clusters.get(i), clusters.get(j), entryDistances);
                    if (distance < minInterClusterDistance) {
                        minInterClusterDistance = distance;
                        minDistanceI = i;
                        minDistanceJ = j;
                    }
                }
            }

            if (minDistanceI == minDistanceJ) {
                throw new RuntimeException("Error while clustering. Cannot merge same cluster with index " + minDistanceI);
            }

            String[] clusterI = clusters.remove(minDistanceI);
            String[] clusterJ = clusters.remove(minDistanceJ - 1);
            String[] newCluster = ArrayUtils.addAll(clusterI, clusterJ);
            clusters.add(newCluster);

            LOG.info("Current number of clusters: {}.", clusters.size());

        } while (!stopCriterion.shouldStop(clusters, minInterClusterDistance));

        return clusters;
    }

    public void displayClusters(List<String[]> clusters) {
        for (String[] cluster : clusters) {
            LOG.info(">>>>> Intra-cluster average distance: " + intraClusterAverageDistance(cluster));
            for (int i = 0; i < cluster.length - 1; i++) {
                LOG.info("{}: {}", cluster[i], dataset.get(cluster[i]));
                LOG.info("Distance: {}", entryDistances.get(cluster[i]).get(cluster[i + 1]));
            }
            LOG.info("{}: {}", cluster[cluster.length - 1], dataset.get(cluster[cluster.length - 1]));
        }
    }

    public void displayDistances(List<String[]> clusters) {
        SummaryStatistics summaryStatistics = new SummaryStatistics();

        for (int i = 0; i < clusters.size(); i++) {
            double distance = intraClusterAverageDistance(clusters.get(i));
            LOG.info("Cluster {} has {} elements with an intra-cluster average distance of {}", i, clusters.get(i).length, distance);
            summaryStatistics.addValue(distance);
        }

        LOG.info("Final average distance: {}", summaryStatistics.getMean());
        LOG.info("Variance: {}", summaryStatistics.getVariance());
    }

    private double intraClusterAverageDistance(String[] cluster) {
        if (cluster.length <= 1) {
            return 0;
        }

        double totalDistance = 0;
        int numberOfDistances = 0;

        for (int i = 0; i < cluster.length - 1; i++) {
            for (int j = i + 1; j < cluster.length; j++) {
                totalDistance += entryDistances.get(cluster[i]).get(cluster[j]);
                numberOfDistances++;
            }
        }

        return totalDistance / numberOfDistances;
    }

}
