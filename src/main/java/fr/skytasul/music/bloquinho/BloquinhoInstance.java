package fr.skytasul.music.bloquinho;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.model.SoundCategory;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import fr.skytasul.music.JukeBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.Random;

public class BloquinhoInstance {

    private final Location location;
    private final Player owner;
    private PositionSongPlayer songPlayer;
    private Song currentSong;
    private Object particleTask;

    public BloquinhoInstance(Location location, Player owner) {
        this.location = location;
        this.owner = owner;
    }

    public void playSong(Song song, int distance) {
        stop();

        this.currentSong = song;
        this.songPlayer = new PositionSongPlayer(song);
        this.songPlayer.setTargetLocation(location);
        this.songPlayer.setDistance(distance);

        this.songPlayer.setCategory(SoundCategory.RECORDS);

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.songPlayer.addPlayer(player);
        }

        this.songPlayer.setPlaying(true);
        startParticles();
    }

    public void stop() {
        stopParticles();
        if (songPlayer != null) {
            songPlayer.setPlaying(false);
            songPlayer.destroy();
            songPlayer = null;
        }
        currentSong = null;
    }

    private void startParticles() {
        stopParticles();
        Location particleLoc = location.clone().add(0.5, 1.2, 0.5);
        Random random = new Random();

        Runnable taskLogic = () -> {
            if (location.getWorld() != null && songPlayer != null && songPlayer.isPlaying()) {
                double noteColor = random.nextInt(24) / 24.0;
                location.getWorld().spawnParticle(Particle.NOTE, particleLoc, 0, noteColor, 0.0, 0.0, 1.0);
            } else {
                stopParticles();
            }
        };

        if (JukeBox.isFolia()) {
            particleTask = Bukkit.getRegionScheduler().runAtFixedRate(
                    JukeBox.getInstance(),
                    location,
                    task -> taskLogic.run(),
                    10L,
                    10L
            );
        } else {
            particleTask = Bukkit.getScheduler().runTaskTimer(
                    JukeBox.getInstance(),
                    taskLogic,
                    10L,
                    10L
            );
        }
    }

    private void stopParticles() {
        if (particleTask != null) {
            if (particleTask instanceof io.papermc.paper.threadedregions.scheduler.ScheduledTask foliaTask) {
                foliaTask.cancel();
            } else if (particleTask instanceof org.bukkit.scheduler.BukkitTask bukkitTask) {
                bukkitTask.cancel();
            }
            particleTask = null;
        }
    }

    public Location getLocation() {
        return location;
    }

    public Player getOwner() {
        return owner;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public PositionSongPlayer getSongPlayer() {
        return songPlayer;
    }
}