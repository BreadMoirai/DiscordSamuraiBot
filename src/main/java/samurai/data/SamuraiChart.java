package samurai.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * @author TonTL
 * @version 4.x - 2/16/2017
 */
public class SamuraiChart {
    private int chartId;
    private String chartName;
    private ArrayList<Integer> beatmapIds;

    public SamuraiChart(int chartId, String chartName) {
        this.chartId = chartId;
        this.chartName = chartName;
        this.beatmapIds = new ArrayList<>();
    }

    public SamuraiChart(int chartId, String chartName, ArrayList<Integer> beatmapIds) {
        this.chartId = chartId;
        this.chartName = chartName;
        this.beatmapIds = beatmapIds;
    }

    public int getChartId() {
        return chartId;
    }

    public String getChartName() {
        return chartName;
    }

    public boolean addBeatmap(int id) {
        return !beatmapIds.contains(id) && beatmapIds.add(id);
    }

    public ArrayList<Integer> getBeatmapIds() {
        return beatmapIds;
    }

    private void writeObject(ObjectOutputStream o) throws IOException {
        o.writeInt(chartId);
        o.writeUTF(chartName);
        o.writeByte(beatmapIds.size());
        for (Integer i : beatmapIds)
            o.writeInt(i);

    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        chartId = o.readInt();
        chartName = o.readUTF();
        int size = o.readByte();
        beatmapIds = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            beatmapIds.add(o.readInt());
    }


}
