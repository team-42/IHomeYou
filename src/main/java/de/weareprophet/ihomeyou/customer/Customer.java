package de.weareprophet.ihomeyou.customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Customer {

    private static final Logger LOG = LogManager.getLogger(Customer.class);

    private final NeedsFulfillment desire;

    private final int numberOfPpl;

    private Customer(int numberOfPpl, NeedsFulfillment desire) {
        this.numberOfPpl = numberOfPpl;
        this.desire = desire;
    }

    public static Customer easyCustomer() {
        return new Customer(1, NeedsFulfillment.builder()
                .add(NeedsType.Storage, 40)
                .add(NeedsType.Food, 40)
                .add(NeedsType.Rest, 40)
                .add(NeedsType.Personal, 20)
                .add(NeedsType.Space, 20)
                .build());
    }

    public int getNumberOfPpl() {
        return numberOfPpl;
    }

    public NeedsFulfillment getDesire() {
        return desire;
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
            final double satisfactionForType;
            final double fulfilmentForType = fulfillment.getNeeds().getOrDefault(t, 0).doubleValue();
            if (customerDesireForType > 0) {
                satisfactionForType = Math.min(2.0, (fulfilmentForType * 3.0 / customerDesireForType.doubleValue()) - 2.0);
            } else {
                satisfactionForType = Math.min(2.0, fulfilmentForType / 200.0) / 2.0;
            }
            LOG.debug("Need for type {} is {}. Delivered have been {}. Resulting score is {}.", t, customerDesireForType, fulfilmentForType, satisfactionForType);
            satisfaction += satisfactionForType / NeedsType.values().length;
        }
        LOG.debug("Overall satisfaction before normalization: {}", satisfaction);
        if (satisfaction < 0) {
            satisfaction = 0;
        } else if (satisfaction > 1) {
            satisfaction = 1;
        }
        return satisfaction;
    }

}
