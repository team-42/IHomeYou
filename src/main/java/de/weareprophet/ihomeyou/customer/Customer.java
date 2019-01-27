package de.weareprophet.ihomeyou.customer;

public class Customer {

    private final NeedsFulfillment desire;

    private Customer(NeedsFulfillment desire) {
        this.desire = desire;
    }

    public static Customer easyCustomer() {
        return new Customer(NeedsFulfillment.builder()
                .add(NeedsType.Storage, 40)
                .add(NeedsType.Food, 40)
                .add(NeedsType.Rest, 40)
                .add(NeedsType.PersonalNeeds, 20)
                .build());
    }

    /**
     * Measures the satisfaction of this customer with a provided flat.
     *
     * @param fulfillment The need fulfilment of the created flat.
     * @return The customer satisfaction between 0 (not satisfied) and 1 (entirely satisfied).
     */
    public double measureSatisfaction(final NeedsFulfillment fulfillment) {
        double satisfaction = 0.5;

        for (final NeedsType t : NeedsType.values()) {
            final Integer customerDesireForType = this.desire.getNeeds().getOrDefault(t, 0);
            if (customerDesireForType > 0) {
                satisfaction += (fulfillment.getNeeds().getOrDefault(t, 0).doubleValue() / customerDesireForType.doubleValue()) - 1;
            } else {
                satisfaction += fulfillment.getNeeds().getOrDefault(t, 0).doubleValue() / 1000;
            }
        }
        if (satisfaction < 0) {
            satisfaction = 0;
        } else if (satisfaction > 1) {
            satisfaction = 1;
        }
        return satisfaction;
    }

}
