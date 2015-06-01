package semantic.diversification.clustering;


import java.util.List;

public class StopCriterion {
    private final int clusterSizeThreshold;
    private final double interClusterSizeThreshold;

    public StopCriterion(int clusterSizeThreshold, double interClusterSizeThreshold) {
        this.clusterSizeThreshold = clusterSizeThreshold;
        this.interClusterSizeThreshold = interClusterSizeThreshold;
    }

    public boolean shouldStop(List<String[]> clusters, double minInterClusterDistance) {
        return clusters.size() <= this.clusterSizeThreshold || minInterClusterDistance >= this.interClusterSizeThreshold;
    }
}
