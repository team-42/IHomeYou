package de.weareprophet.ihomeyou;

import de.weareprophet.ihomeyou.asset.WallType;
import de.weareprophet.ihomeyou.customer.Customer;
import de.weareprophet.ihomeyou.customer.NeedsFulfillment;
import de.weareprophet.ihomeyou.customer.NeedsType;
import de.weareprophet.ihomeyou.datastructure.FurnitureObject;
import de.weareprophet.ihomeyou.datastructure.room.Room;
import de.weareprophet.ihomeyou.datastructure.room.RoomTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

class ScoringHelper {
    private static final Logger LOG = LogManager.getLogger(ScoringHelper.class);

    private final GameGrid grid;

    ScoringHelper(GameGrid grid) {
        this.grid = grid;
    }

    void calculateRoomFulfilment(NeedsFulfillment.Builder fulfilment) {
        List<Room> rooms = grid.getRoomManager().getRooms();
        fulfilment.add(NeedsType.Space, (int) (Math.max(0, (rooms.size() - 1)) * 20 * Customer.NEED_ADJUSTMENT_FACTOR));
        for (int i = 1; i < rooms.size(); i++) { // skip the first element bc that's the outside
            final Room r = rooms.get(i);
            Collection<FurnitureObject> assetsInRoom = grid.getAssetsInRoom(r);
            final Map<WallType, Integer> wallCount = new EnumMap<>(WallType.class);
            for (final WallType w : WallType.values()) {
                wallCount.put(w, grid.getNumWallTypeInRoom(w, r));
            }
            int roomSize = r.getTiles().size();
            int assetCount = assetsInRoom.size();
            LOG.debug("Room with {} tiles, {} assets, accessibility {} and walls: {}", roomSize, assetCount, r.isAccessible(), wallCount);
            if (!r.isAccessible()) {
                continue; // ignore inaccessible rooms
            }
            for (final FurnitureObject furniture : assetsInRoom) {
                fulfilment.add(furniture.getType().getNeedsFulfillment());
            }
            rewardRoomConsistency(fulfilment, assetsInRoom, roomSize, assetCount);
            int roomComfort = Math.max(0, wallCount.get(WallType.Solid) * WallType.Solid.getPrice() + wallCount.get(WallType.Window) * WallType.Window.getPrice() - roomSize - wallCount.get(WallType.Door) * 4);
            LOG.debug("Room comfort: {}", roomComfort);
            fulfilment.add(NeedsType.Comfort, roomComfort);
        }
    }


    private void rewardRoomConsistency(NeedsFulfillment.Builder fulfilment, Collection<FurnitureObject> assetsInRoom, int roomSize, int assetCount) {
        final Map<RoomTypes, Integer> typeCount = new EnumMap<>(RoomTypes.class);
        RoomTypes bestType = RoomTypes.HALLWAY;
        int bestCount = 0;
        for (final RoomTypes roomType : RoomTypes.values()) {
            int validAssetCount = 0;
            for (final FurnitureObject asset : assetsInRoom) {
                if (roomType.validRoomAsset(asset.getType())) {
                    validAssetCount++;
                }
            }
            typeCount.put(roomType, validAssetCount);
            if (validAssetCount > bestCount) {
                bestType = roomType;
                bestCount = validAssetCount;
            }
        }

        boolean roomConsistency = true;
        for (final RoomTypes roomType : RoomTypes.values()) {
            if (roomType == bestType) {
                continue;
            }
            if (typeCount.get(roomType) > bestCount) {
                roomConsistency = false;
            }
        }
        LOG.debug("Room has type {} and is consistent: {}", bestType, roomConsistency);
        if (roomConsistency) {
            switch (bestType) {
                case BATH:
                    applyRoomPerk(fulfilment, assetsInRoom, roomSize, assetCount, NeedsType.Cleanliness);
                    break;
                case OFFICE:
                    applyRoomPerk(fulfilment, assetsInRoom, roomSize, assetCount, NeedsType.Work);
                    break;
                case KITCHEN:
                    applyRoomPerk(fulfilment, assetsInRoom, roomSize, assetCount, NeedsType.Food);
                    break;
                case LIVING_ROOM:
                    applyRoomPerk(fulfilment, assetsInRoom, roomSize, assetCount, NeedsType.Comfort);
                    break;
                case BED_ROOM:
                    applyRoomPerk(fulfilment, assetsInRoom, roomSize, assetCount, NeedsType.Personal);
                    break;
                case HALLWAY:
                    fulfilment.add(NeedsType.Space, (roomSize - assetCount) * 5);
                    break;
                default:
            }
        }
    }

    private void applyRoomPerk(NeedsFulfillment.Builder fulfilment, Collection<FurnitureObject> assetsInRoom, int roomSize, int assetCount, NeedsType needsType) {
        for (final FurnitureObject assetInRoom : assetsInRoom) {
            fulfilment.add(needsType, assetInRoom.getType().getNeedsFulfillment().getNeeds().getOrDefault(needsType, 0) * (roomSize - assetCount) / 30);
        }
        fulfilment.add(NeedsType.Space, (roomSize - assetCount) * 5);
    }
}
