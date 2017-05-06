package samurai.audio;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;

public class AudioTrackR implements AudioTrack {
    private final AudioTrack track;
    private final String requester;

    public AudioTrackR(AudioTrack track, String requester) {
        this.requester = requester;
        this.track = track;
    }

    public String getRequester() {
        return requester;
    }

    @Override
    public AudioTrackInfo getInfo() {
        return track.getInfo();
    }

    @Override
    public String getIdentifier() {
        return track.getIdentifier();
    }

    @Override
    public AudioTrackState getState() {
        return track.getState();
    }

    @Override
    public void stop() {
        track.stop();
    }

    @Override
    public boolean isSeekable() {
        return track.isSeekable();
    }

    @Override
    public long getPosition() {
        return track.getPosition();
    }

    @Override
    public void setPosition(long position) {
        track.setPosition(position);
    }

    @Override
    public void setMarker(TrackMarker marker) {
        track.setMarker(marker);
    }

    @Override
    public long getDuration() {
        return track.getDuration();
    }

    @Override
    public AudioTrackR makeClone() {
        return new AudioTrackR(track.makeClone(), requester);
    }

    @Override
    public AudioSourceManager getSourceManager() {
        return track.getSourceManager();
    }

    public AudioTrack getTrack() {
        return track;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AudioTrackR that = (AudioTrackR) o;

        if (!track.equals(that.track)) return false;
        return requester != null ? requester.equals(that.requester) : that.requester == null;
    }

    @Override
    public int hashCode() {
        int result = track.hashCode();
        result = 31 * result + (requester != null ? requester.hashCode() : 0);
        return result;
    }
}
