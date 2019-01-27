package de.weareprophet.ihomeyou.customer;

import java.util.EnumMap;
import java.util.Map;

public class NeedsFulfillment {
    private final Map<NeedsType, Integer> needs;

    private NeedsFulfillment(Map<NeedsType, Integer> needs) {
        this.needs = needs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<NeedsType, Integer> needs = new EnumMap<>(NeedsType.class);

        Builder() {
        }

        public Builder add(final NeedsType type, final int intensity) {
            this.needs.put(type, this.needs.getOrDefault(type, 0) + intensity);
            return this;
        }

        public Builder add(final NeedsFulfillment fulfillment) {
            Map<NeedsType, Integer> other = fulfillment.getNeeds();
            for (final NeedsType t : other.keySet()) {
                this.add(t, other.get(t));
            }
            return this;
        }

        public NeedsFulfillment build() {
            return new NeedsFulfillment(needs);
        }
    }

    public Map<NeedsType, Integer> getNeeds() {
        return needs;
    }
}
