package samurai.audio;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.User;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioTestHandler implements AudioSendHandler, AudioReceiveHandler
{
    private Queue<byte[]> pkg = new LinkedBlockingQueue<>();
    private final long userId;

    public AudioTestHandler(User user)
    {
        this.userId = user.getIdLong();
    }

    public boolean canReceiveCombined() { return false; }
    public boolean canReceiveUser() { return true; }
    public void handleCombinedAudio(CombinedAudio combinedAudio) { }

    @Override
    public void handleUserAudio(UserAudio userAudio)
    {
        if (userAudio.getUser().getIdLong() == userId)
            pkg.add(userAudio.getAudioData(1.0f));
    }

    @Override
    public boolean canProvide()
    {
        return !pkg.isEmpty();
    }

    @Override
    public byte[] provide20MsAudio()
    {
        return pkg.poll();
    }
}