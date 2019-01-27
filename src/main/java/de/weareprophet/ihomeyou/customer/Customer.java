package de.weareprophet.ihomeyou.customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class Customer {

    private static final Logger LOG = LogManager.getLogger(Customer.class);

    private final NeedsFulfillment desire;

    private final int numberOfPpl;

    private final int budget;

    private final int prestige;

    private Customer(int numberOfPpl, int budget, int prestige, NeedsFulfillment desire) {
        this.numberOfPpl = numberOfPpl;
        this.budget = budget;
        this.prestige = prestige;
        this.desire = desire;
    }

    public static Customer easyCustomer() {
        return new Customer(1, 500, 2, NeedsFulfillment.builder()
                .add(NeedsType.Storage, 40)
                .add(NeedsType.Food, 40)
                .add(NeedsType.Rest, 40)
                .add(NeedsType.Personal, 20)
                .add(NeedsType.Space, 20)
                .build());
    }

    public static Customer rngCustomer(final int maxDifficulty) {
        final NeedsFulfillment.Builder needs = NeedsFulfillment.builder();
        final Random r = new Random();
        int overallNeeds = 0;

        int intensity = (maxDifficulty / 2) + r.nextInt(maxDifficulty / 2);
        overallNeeds += intensity;
        needs.add(NeedsType.Space, intensity);


        intensity = (maxDifficulty / 2) + r.nextInt(maxDifficulty / 2);
        overallNeeds += intensity;
        needs.add(NeedsType.Rest, intensity);

        intensity = (maxDifficulty / 2) + r.nextInt(maxDifficulty / 2);
        overallNeeds += intensity;
        needs.add(NeedsType.Storage, intensity);

        intensity = (maxDifficulty / 2) + r.nextInt(maxDifficulty / 2);
        overallNeeds += intensity;
        needs.add(NeedsType.Cleanliness, intensity);


        if (maxDifficulty > 60) {
            intensity = (maxDifficulty / 2) + r.nextInt(maxDifficulty / 2);
            overallNeeds += intensity;
            needs.add(NeedsType.Work, intensity);

            intensity = (maxDifficulty / 2) + r.nextInt(maxDifficulty / 2);
            overallNeeds += intensity;
            needs.add(NeedsType.Food, intensity);

            intensity = (maxDifficulty / 2) + r.nextInt(maxDifficulty / 2);
            overallNeeds += intensity;
            needs.add(NeedsType.Personal, intensity);
        }

        if (maxDifficulty > 80) {
            intensity = (maxDifficulty / 2) + r.nextInt(maxDifficulty / 2);
            overallNeeds += intensity;
            needs.add(NeedsType.Decoration, intensity);

            intensity = (maxDifficulty / 2) + r.nextInt(maxDifficulty / 2);
            overallNeeds += intensity;
            needs.add(NeedsType.Comfort, intensity);
        }

        if (maxDifficulty > 120) {
            intensity = (maxDifficulty / 2) + r.nextInt(maxDifficulty / 2);
            overallNeeds += intensity;
            needs.add(NeedsType.Luxury, intensity);
        }

        int numberOfPpl = Math.min(6, 1 + (overallNeeds / 200));
        int prestige = 1 + (maxDifficulty / 70);
        return new Customer(numberOfPpl, overallNeeds * 2, prestige, needs
                .build());
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
                satisfactionForType = Math.min(1.2, (fulfilmentForType * 3.0 / customerDesireForType.doubleValue()) - 2.0);
            } else {
                satisfactionForType = 1.0 + Math.min(0.2, fulfilmentForType / 1000.0);
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
