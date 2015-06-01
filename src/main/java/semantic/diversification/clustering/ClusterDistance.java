package semantic.diversification.clustering;

import java.util.Map;

public enum ClusterDistance {
    SINGLE_LINKAGE {
        @Override
        public Double getDistance(String[] cluster1, String[] cluster2, Map<String, Map<String, Double>> entryDistances) {
            double distance = Double.MAX_VALUE;

            for (String id1 : cluster1) {
                for (String id2 : cluster2) {
                    double interClusterDistance = entryDistances.get(id1).get(id2);
                    if (interClusterDistance < distance) {
                        distance = interClusterDistance;
                    }
                }
            }

            return distance;
        }
    },

    COMPLETE_LINKAGE {
        @Override
        public Double getDistance(String[] cluster1, String[] cluster2, Map<String, Map<String, Double>> entryDistances) {
            double distance = Double.MIN_VALUE;

            for (String id1 : cluster1) {
                for (String id2 : cluster2) {
                    double interClusterDistance = entryDistances.get(id1).get(id2);
                    if (interClusterDistance > distance) {
                        distance = interClusterDistance;
                    }
                }
            }

            return distance;
        }
    },

    AVERAGE_LINKAGE {
        @Override
        public Double getDistance(String[] cluster1, String[] cluster2, Map<String, Map<String, Double>> entryDistances) {
            double totalDistance = 0;
            int numberOfDistances = 0;

            for (String id1 : cluster1) {
                for (String id2 : cluster2) {
                    totalDistance += entryDistances.get(id1).get(id2);
                    numberOfDistances++;
                }
            }

            return totalDistance/numberOfDistances;
        }
    };

    public abstract Double getDistance(String[] cluster1, String[] cluster2, Map<String, Map<String, Double>> entryDistances);
}
