/*
 * Copyright or © or Copr. Moribus (2013)
 * Copyright or © or Copr. ProkopyL <prokopylmc@gmail.com> (2015)
 * Copyright or © or Copr. Amaury Carrade <amaury@carrade.eu> (2016 – 2021)
 * Copyright or © or Copr. Vlammar <valentin.jabre@gmail.com> (2019 – 2021)
 *
 * This software is a computer program whose purpose is to allow insertion of
 * custom images in a Minecraft world.
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package fr.moribus.imageonmap.map;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.InvalidConfigurationException;

public class PosterMap extends ImageMap {
    protected final int[] mapsIDs;
    protected final int columnCount;
    protected final int rowCount;

    public PosterMap(UUID userUUID, int[] mapsIDs, String id, String name, int columnCount, int rowCount) {
        super(userUUID, Type.POSTER, id, name);
        this.mapsIDs = mapsIDs;
        this.columnCount = Math.max(columnCount, 0);
        this.rowCount = Math.max(rowCount, 0);
    }

    public PosterMap(UUID userUUID, int[] mapsIDs, int columnCount, int rowCount) {
        this(userUUID, mapsIDs, null, null, columnCount, rowCount);
    }

    public PosterMap(Map<String, Object> map, UUID userUUID) throws InvalidConfigurationException {
        super(map, userUUID, Type.POSTER);

        columnCount = getFieldValue(map, "columns");
        rowCount = getFieldValue(map, "rows");

        List<Integer> idList = getFieldValue(map, "mapsIDs");
        mapsIDs = new int[idList.size()];
        for (int i = 0, c = idList.size(); i < c; i++) {
            mapsIDs[i] = idList.get(i);
        }
    }

    @Override
    public int[] getMapsIDs() {
        return mapsIDs;
    }

    /* ====== Serialization methods ====== */

    @Override
    public boolean managesMap(int mapID) {
        for (int mapsID : mapsIDs) {
            if (mapsID == mapID) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void postSerialize(Map<String, Object> map) {
        map.put("columns", columnCount);
        map.put("rows", rowCount);
        map.put("mapsIDs", mapsIDs);
    }

    /* ====== Getters & Setters ====== */

    /**
     * Returns the amount of columns in the poster map
     *
     * @return The number of columns, or 0 if this data is missing
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * Returns the amount of rows in the poster map
     *
     * @return The number of rows, or 0 if this data is missing
     */
    public int getRowCount() {
        return rowCount;
    }

    public int getColumnAt(int i) {
        if (columnCount == 0) {
            return 0;
        }
        return (i % columnCount);
    }

    public int getRowAt(int i) {
        if (columnCount == 0) {
            return 0;
        }
        return (i / columnCount);
    }

    public int getIndexAt(int col, int row) {
        return columnCount * row + col;
    }

    /**
     * Returns the map id at the given column and line.
     *
     * @param x The x coordinate. Starts at 0.
     * @param y The y coordinate. Starts at 0.
     * @return The Minecraft map ID.
     * @throws ArrayIndexOutOfBoundsException if the given coordinates are too big (out of the poster).
     */
    public int getMapIdAt(int x, int y) {
        return mapsIDs[y * columnCount + x];
    }

    public int getMapIdAt(int index) {
        return mapsIDs[index];
    }

    public int getMapIdAtReverseY(int index) {
        int x = index % (columnCount);
        int y = index / (columnCount);
        return getMapIdAt(x, rowCount - y - 1);
    }


    public int getMapIdAtReverseZ(int index, BlockFace orientation, BlockFace bf) {
        int x = 0;
        int y = 0;
        switch (bf) {
            case UP:
                x = index % (columnCount);
                y = index / (columnCount);
                break;
            case DOWN:
                x = (columnCount - 1) - index % (columnCount);
                y = index / (columnCount);
                break;
            default:
        }

        return getMapIdAt(x, rowCount - y - 1);

    }


    public boolean hasColumnData() {
        return rowCount != 0 && columnCount != 0;
    }

    @Override
    public int getMapCount() {
        return mapsIDs.length;
    }

    public int getIndex(int mapID) {
        for (int i = 0; i < mapsIDs.length; i++) {
            if (mapsIDs[i] == mapID) {
                return i;
            }
        }

        throw new IllegalArgumentException("Invalid map ID");
    }

}
