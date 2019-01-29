package de.weareprophet.ihomeyou.customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Customer {

    /**
     * Factor to adjust the rolled needs.
     */
    public static final float NEED_ADJUSTMENT_FACTOR = 1.6f;

    /**
     * Maximum supported difficulty.
     */
    public static final int MAX_DIFFICULTY = 400;

    private static final Logger LOG = LogManager.getLogger(Customer.class);
    /**
     * The difficulty at which mid game needs are unlocked.
     */
    private static final int MID_GAME_THRESHOLD = MAX_DIFFICULTY * 7 / 20;
    /**
     * The difficulty at which late game needs are unlocked.
     */
    private static final int LATE_GAME_THRESHOLD = MAX_DIFFICULTY * 11 / 20;
    /**
     * The difficulty at which end game needs are unlocked.
     */
    private static final int END_GAME_THRESHOLD = MAX_DIFFICULTY * 15 / 20;
    /**
     * The difficulty at which the max number of inhabitants unlocked.
     */
    private static final int MAX_INHABITANTS_THRESHOLD = MAX_DIFFICULTY * 17 / 20;
    /**
     * The maximum number of inhabitants.
     */
    private static final int MAX_INHABITANTS = 6;
    /**
     * A factor which makes the game easier.
     */
    private static final double REWARD_ADJUSTMENT_FACTOR = 1.8;
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

    private Customer(int numberOfPpl, int budget, int prestige, NeedsFulfillment desire) {
        this.numberOfPpl = numberOfPpl;
        this.budget = budget;
        this.prestige = prestige;
        this.desire = desire;
    }

    /**
     * Creates a new random customer with the specified difficulty.
     *
     * @param difficulty A value above 0 and lower or equal to {@value MAX_DIFFICULTY}
     * @return The created customer
     */
    public static Customer rngCustomer(final int difficulty) {
        final NeedsFulfillment.Builder needs = NeedsFulfillment.builder();
        final Random r = new Random();
        int overallNeeds = 0;

        final Set<NeedsType> possibleNeeds = EnumSet.allOf(NeedsType.class);
        possibleNeeds.remove(NeedsType.Space); // Space need is handled separately

        if (difficulty < MID_GAME_THRESHOLD) { // mid-game needs
            possibleNeeds.remove(NeedsType.Comfort);
            possibleNeeds.remove(NeedsType.Food);
        }
        if (difficulty < LATE_GAME_THRESHOLD) { // late-game need
            possibleNeeds.remove(NeedsType.Decoration);
        }
        if (difficulty < END_GAME_THRESHOLD) { // final need
            possibleNeeds.remove(NeedsType.Luxury);
        }

        List<NeedsType> actualNeeds = Arrays.asList(possibleNeeds.toArray(new NeedsType[0]));
        Collections.shuffle(actualNeeds);
        final int numNeeds = Math.min(possibleNeeds.size(), difficulty * NeedsType.values().length / MAX_DIFFICULTY);
        for (int i = 0; i < numNeeds; i++) {
            NeedsType needsType = actualNeeds.get(i);
            int intensity = r.nextInt((int) (difficulty * NEED_ADJUSTMENT_FACTOR * needsType.getPriceFactor()));
            overallNeeds += intensity;
            needs.add(needsType, intensity);
        }

        int numberOfPpl = Math.max(1, Math.min(MAX_INHABITANTS, difficulty * MAX_INHABITANTS / MAX_INHABITANTS_THRESHOLD));
        needs.add(NeedsType.Space, (int) ((numberOfPpl * 15 + r.nextInt(difficulty)) * NEED_ADJUSTMENT_FACTOR));
        int prestige = (int) (1 + difficulty * 4.4 / MAX_DIFFICULTY);
        return new Customer(numberOfPpl, (int) (overallNeeds * REWARD_ADJUSTMENT_FACTOR + 100), prestige, needs
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
