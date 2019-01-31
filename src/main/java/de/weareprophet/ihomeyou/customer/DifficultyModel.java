package de.weareprophet.ihomeyou.customer;

import org.frice.resource.graphics.ColorResource;

import java.util.*;

/**
 * Represents the difficulty settings for the game and produces {@link Customer} instances.
 */
public class DifficultyModel {

    public enum NeedIntensityLabel {
        None(0, ColorResource.LIGHT_GRAY),
        Minimal(10, new ColorResource(0, 213, 106)),
        Low(20, ColorResource.GREEN),
        Moderate(40, new ColorResource(156, 240, 0)),
        Medium(60, ColorResource.ORANGE),
        High(80, ColorResource.RED),
        Serious(100, new ColorResource(128, 0, 0));

        private final int intensityThresholdPercentage;

        private final ColorResource color;

        NeedIntensityLabel(final int intensityThresholdPercentage, final ColorResource color) {
            this.intensityThresholdPercentage = intensityThresholdPercentage;
            this.color = color;
        }

        public ColorResource getColor() {
            return color;
        }

        public String getName() {
            return name();
        }

        public static NeedIntensityLabel getForNeedIntensity(DifficultyModel difficultyModel, final NeedsType needsType, final int needIntensity) {
            for (int i = values().length - 1; i > 0; i--) {
                if (needIntensity <= (values()[i].intensityThresholdPercentage * difficultyModel.getMaxIntensityForType(difficultyModel.numberOfLevels, needsType) / 100)
                        && needIntensity > values()[i - 1].intensityThresholdPercentage * difficultyModel.getMaxIntensityForType(difficultyModel.numberOfLevels, needsType) / 100) {
                    return values()[i];
                }
            }
            return None;
        }
    }

    /**
     * Maximum supported difficulty.
     */
    private final int maxDifficulty;
    private final int initialDifficulty;

    private final int numberOfLevels;
    /**
     * The difficulty at which the max number of inhabitants unlocked.
     */
    private final int maxInhabitantsThreshold;
    /**
     * The difficulty at which end game needs are unlocked.
     */
    private final int endGameThreshold;
    /**
     * The difficulty at which late game needs are unlocked.
     */
    private final int lateGameThreshold;
    /**
     * The difficulty at which mid game needs are unlocked.
     */
    private final int midGameThreshold;
    /**
     * The maximum number of inhabitants.
     */
    private final int maxInhabitants;
    /**
     * Factor to adjust the rolled needs.
     */
    private final float needAdjustmentFactor;
    /**
     * A factor which makes the game easier.
     */
    private final float rewardAdjustmentFactor;


    private int currentLevelNumber = -1;

    public DifficultyModel(int maxDifficulty, int initialDifficulty, int numberOfLevels,
                           final float needAdjustmentFactor, final float rewardAdjustmentFactor,
                           final int maxInhabitants, final int maxInhabitantsThresholdPercentage,
                           final int midGameThresholdPercentage, final int lateGameThresholdPercentage,
                           final int endGameThresholdPercentage) {
        this.maxDifficulty = maxDifficulty;
        this.initialDifficulty = initialDifficulty;
        this.numberOfLevels = numberOfLevels;
        this.needAdjustmentFactor = needAdjustmentFactor;
        this.rewardAdjustmentFactor = rewardAdjustmentFactor;
        this.maxInhabitants = maxInhabitants;
        this.maxInhabitantsThreshold = maxDifficulty * maxInhabitantsThresholdPercentage / 100;
        this.midGameThreshold = maxDifficulty * midGameThresholdPercentage / 100;
        this.lateGameThreshold = maxDifficulty * lateGameThresholdPercentage / 100;
        this.endGameThreshold = maxDifficulty * endGameThresholdPercentage / 100;
    }

    /**
     * Creates the customer for the next level.
     *
     * @return The created customer or <strong>null</strong> if the game has been won.
     */
    public Customer nextCustomer() {
        currentLevelNumber++;
        if (currentLevelNumber == numberOfLevels) {
            return null;
        }
        final int difficulty = getCurrentDifficulty();
        final NeedsFulfillment.Builder needs = NeedsFulfillment.builder();
        final Random r = new Random();
        int overallNeeds = 0;

        final Set<NeedsType> possibleNeeds = EnumSet.allOf(NeedsType.class);
        possibleNeeds.remove(NeedsType.Space); // Space need is handled separately

        if (difficulty < midGameThreshold) { // mid-game needs
            possibleNeeds.remove(NeedsType.Comfort);
            possibleNeeds.remove(NeedsType.Food);
        }
        if (difficulty < lateGameThreshold) { // late-game need
            possibleNeeds.remove(NeedsType.Decoration);
        }
        if (difficulty < endGameThreshold) { // final need
            possibleNeeds.remove(NeedsType.Luxury);
        }

        List<NeedsType> actualNeeds = Arrays.asList(possibleNeeds.toArray(new NeedsType[0]));
        Collections.shuffle(actualNeeds);
        final int numNeeds = Math.min(possibleNeeds.size(), difficulty * NeedsType.values().length / maxDifficulty);
        for (int i = 0; i < numNeeds; i++) {
            NeedsType needsType = actualNeeds.get(i);
            int intensity = r.nextInt(getMaxIntensityForType(currentLevelNumber, needsType));
            overallNeeds += intensity;
            needs.add(needsType, intensity);
        }

        int numberOfPpl = Math.max(1, Math.min(maxInhabitants, difficulty * maxInhabitants / maxInhabitantsThreshold));
        needs.add(NeedsType.Space, (int) ((numberOfPpl * 15 + r.nextInt(difficulty)) * needAdjustmentFactor));
        int prestige = (int) (1 + difficulty * 4.4 / maxDifficulty);
        return new Customer(numberOfPpl, (int) (overallNeeds * rewardAdjustmentFactor + 100), prestige, needs
                .build());
    }

    private int getDifficultyAtLevel(final int levelNumber) {
        return initialDifficulty + (levelNumber > 0 ? (int) ((maxDifficulty - initialDifficulty) * ((float) levelNumber / numberOfLevels)) : 0);
    }

    private int getCurrentDifficulty() {
        return getDifficultyAtLevel(currentLevelNumber);
    }

    private int getMaxIntensityForType(final int levelNumber, final NeedsType needsType) {
        return (int) (getDifficultyAtLevel(levelNumber) * needAdjustmentFactor * needsType.getPriceFactor());
    }

    public float getNeedAdjustmentFactor() {
        return needAdjustmentFactor;
    }
}
