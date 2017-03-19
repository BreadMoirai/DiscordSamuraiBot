package samurai.entities;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

/**
 * @author TonTL
 * @version 4.x - 2/16/2017
 */
public class Chart implements Externalizable {

    private transient static final long serialVersionUID = 756680962858925830L;

    private int chartId;
    private String chartName;
    private ArrayList<Integer> beatmapIds;

    public Chart() {
    }

    public Chart(int chartId, String chartName) {
        this.chartId = chartId;
        this.chartName = chartName;
        this.beatmapIds = new ArrayList<>();
    }

    Chart(int chartId, String chartName, ArrayList<Integer> beatmapIds) {
        this.chartId = chartId;
        this.chartName = chartName;
        this.beatmapIds = beatmapIds;
    }

    int getChartId() {
        return chartId;
    }

    String getChartName() {
        return chartName;
    }

    public boolean addBeatmap(int id) {
        return !beatmapIds.contains(id) && beatmapIds.add(id);
    }

    ArrayList<Integer> getBeatmapIds() {
        return beatmapIds;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(chartId);
        out.writeUTF(chartName);
        out.writeByte(beatmapIds.size());
        for (Integer i : beatmapIds)
            out.writeInt(i);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        chartId = in.readInt();
        chartName = in.readUTF();
        int size = in.readByte();
        beatmapIds = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            beatmapIds.add(in.readInt());
    }


}
