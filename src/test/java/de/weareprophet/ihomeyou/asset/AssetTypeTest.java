package de.weareprophet.ihomeyou.asset;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AssetTypeTest {

    @Test
    public void getImageUrl() {
        for (final AssetType t : AssetType.values()) {
            assertThat(t.getImageUrl()).isNotNull();
        }
    }
}
