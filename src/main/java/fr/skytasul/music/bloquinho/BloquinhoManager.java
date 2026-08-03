package fr.skytasul.music.bloquinho;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BloquinhoManager {

    private static final Map<Location, BloquinhoInstance> activeBloquinhos = new ConcurrentHashMap<>();
    private static final Map<Player, Location> selectedJukeboxes = new ConcurrentHashMap<>();

    public static void registerJukebox(Location loc, Player owner) {
        activeBloquinhos.putIfAbsent(loc, new BloquinhoInstance(loc, owner));
    }

    public static void unregisterJukebox(Location loc) {
        BloquinhoInstance instance = activeBloquinhos.remove(loc);
        if (instance != null) {
            instance.stop();
        }
    }

    public static BloquinhoInstance getBloquinho(Location loc) {
        return activeBloquinhos.get(loc);
    }

    public static boolean isBloquinho(Location loc) {
        return activeBloquinhos.containsKey(loc);
    }

    public static void setSelectedJukebox(Player player, Location loc) {
        selectedJukeboxes.put(player, loc);
    }

    public static Location getSelectedJukebox(Player player) {
        return selectedJukeboxes.get(player);
    }

    public static void clearSelectedJukebox(Player player) {
        selectedJukeboxes.remove(player);
    }

    public static void playSongAtJukebox(Location loc, Song song, Player owner, int radius) {
        BloquinhoInstance instance = activeBloquinhos.computeIfAbsent(loc, l -> new BloquinhoInstance(l, owner));
        instance.playSong(song, radius);
    }

    public static void stopJukebox(Location loc) {
        BloquinhoInstance instance = activeBloquinhos.get(loc);
        if (instance != null) {
            instance.stop();
        }
    }

    public static Map<Location, BloquinhoInstance> getActiveBloquinhos() {
        return activeBloquinhos;
    }

    public static void stopAll() {
        for (BloquinhoInstance instance : activeBloquinhos.values()) {
            instance.stop();
        }
        activeBloquinhos.clear();
        selectedJukeboxes.clear();
    }
}