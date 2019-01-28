package de.weareprophet.ihomeyou.customer;

import de.weareprophet.ihomeyou.IHomeYouGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Customer {

    public static final float NEED_ADJUSTMENT_FACTOR = 1.6f;

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

    public static Customer rngCustomer(final int difficulty) {
        final NeedsFulfillment.Builder needs = NeedsFulfillment.builder();
        final Random r = new Random();
        int overallNeeds = 0;

        final Set<NeedsType> possibleNeeds = EnumSet.allOf(NeedsType.class);
        possibleNeeds.remove(NeedsType.Space);

        if (difficulty < 120) {
            possibleNeeds.remove(NeedsType.Comfort);
            possibleNeeds.remove(NeedsType.Food);
        }
        if (difficulty < 180) {
            possibleNeeds.remove(NeedsType.Decoration);
        }
        if (difficulty < 240) {
            possibleNeeds.remove(NeedsType.Luxury);
        }

        List<NeedsType> actualNeeds = Arrays.asList(possibleNeeds.toArray(new NeedsType[0]));
        Collections.shuffle(actualNeeds);
        final int numNeeds = Math.min(possibleNeeds.size(), difficulty / 40);
        for (int i = 0; i < numNeeds; i++) {
            NeedsType needsType = actualNeeds.get(i);
            int intensity = r.nextInt((int) (difficulty * NEED_ADJUSTMENT_FACTOR));
            overallNeeds += intensity;
            needs.add(needsType, intensity);
        }

        int numberOfPpl = Math.max(1, Math.min(6, difficulty / 60));
        needs.add(NeedsType.Space, numberOfPpl * 30 + r.nextInt(difficulty));
        int prestige = 1 + (int) (difficulty / (IHomeYouGame.MAX_DIFFICULTY / 3.3));
        return new Customer(numberOfPpl, (int) (overallNeeds * 1.8 + 100), prestige, needs
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
