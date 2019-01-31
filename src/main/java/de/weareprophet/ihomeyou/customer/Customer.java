package de.weareprophet.ihomeyou.customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Customer {

    private static final Logger LOG = LogManager.getLogger(Customer.class);
    /**
     * The maximum satisfaction which can be reached per need.
     */
    private static final double MAX_SATISFACTION_PER_NEED = 1.2;
    /**
     * The maximum dissatisfaction which can be reached per need.
     */
    private static final double MIN_SATISFACTION_PER_NEED = -2.0;

    private final NeedsFulfillment desire;

    private final int numberOfPpl;

    private final int budget;

    private final int prestige;

    Customer(int numberOfPpl, int budget, int prestige, NeedsFulfillment desire) {
        this.numberOfPpl = numberOfPpl;
        this.budget = budget;
        this.prestige = prestige;
        this.desire = desire;
    }

    public int getNumberOfPpl() {
        return numberOfPpl;
    }

    public NeedsFulfillment getDesire() {
        return desire;
    }

    public int getBudget() {
        return budget;
    }

    public int getPrestige() {
        return prestige;
    }

    /**
     * Measures the satisfaction of this customer with a provided flat.
     *
     * @param fulfillment The need fulfilment of the created flat.
     * @return The customer satisfaction between 0 (not satisfied) and 1 (entirely satisfied).
     */
    public double measureSatisfaction(final NeedsFulfillment fulfillment) {
        double satisfaction = 0.0;

        for (final NeedsType t : NeedsType.values()) {
            final Integer customerDesireForType = this.desire.getNeeds().getOrDefault(t, 0);
            final double satisfactionForType;
            final double fulfilmentForType = fulfillment.getNeeds().getOrDefault(t, 0).doubleValue();
            if (customerDesireForType > 0) {
                satisfactionForType = Math.min(MAX_SATISFACTION_PER_NEED, (fulfilmentForType * ((-MIN_SATISFACTION_PER_NEED) + 1) / customerDesireForType.doubleValue()) + MIN_SATISFACTION_PER_NEED);
            } else {
                satisfactionForType = 1.0 + Math.min(MAX_SATISFACTION_PER_NEED - 1, fulfilmentForType / 1000.0);
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
