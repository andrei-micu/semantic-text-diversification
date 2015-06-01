package semantic.diversification.clustering;

import java.util.Set;
import java.util.stream.Collectors;

public enum EntryDistance {
    DIFFERENT_ITEMS {
        @Override
        public Double getDistance(Set<String> entry1, Set<String> entry2) {
            int entry1Size = entry1.size();
            int entry2Size = entry2.size();
            int commonElementsSize = entry1.stream().collect(Collectors.summingInt(x -> entry2.contains(x) ? 1 : 0));

            return (double) (entry1Size + entry2Size - 2 * commonElementsSize);
        }
    },

    DIFFERENT_ITEMS_PERCENTAGE {
        @Override
        public Double getDistance(Set<String> entry1, Set<String> entry2) {
            int entry1Size = entry1.size();
            int entry2Size = entry2.size();
            int commonElementsSize = entry1.stream().collect(Collectors.summingInt(x -> entry2.contains(x) ? 1 : 0));

            return ((double) (entry1Size + entry2Size - 2 * commonElementsSize)) /
                    ((double) (entry1Size + entry2Size - commonElementsSize)) * 100d;
        }
    };

    public abstract Double getDistance(Set<String> entry1, Set<String> entry2);
}
