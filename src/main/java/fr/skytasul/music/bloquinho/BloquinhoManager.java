package fr.skytasul.music.bloquinho;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BloquinhoManager {

    private static final Map<Location, BloquinhoInstance> activeBloquinhos = new ConcurrentHashMap<>();
    private static final Map<UUID, Location> playerBloquinhos = new ConcurrentHashMap<>();
    private static final Map<Player, Location> selectedJukeboxes = new ConcurrentHashMap<>();

    public static void registerJukebox(Location loc, Player owner) {
        unregisterJukebox(loc);
        BloquinhoInstance instance = new BloquinhoInstance(loc, owner);
        activeBloquinhos.put(loc, instance);
        if (owner != null) {
            playerBloquinhos.put(owner.getUniqueId(), loc);
        }
    }

    public static void unregisterJukebox(Location loc) {
        BloquinhoInstance instance = activeBloquinhos.remove(loc);
        if (instance != null) {
            instance.stop();
            if (instance.getOwner() != null) {
                playerBloquinhos.remove(instance.getOwner().getUniqueId());
            }
        }
    }

    public static boolean hasBloquinho(UUID uuid) {
        Location loc = playerBloquinhos.get(uuid);
        if (loc == null) return false;
        if (!isBloquinho(loc)) {
            playerBloquinhos.remove(uuid);
            return false;
        }
        return true;
    }

    public static Location getPlayerBloquinho(UUID uuid) {
        return playerBloquinhos.get(uuid);
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
        Location loc = selectedJukeboxes.get(player);
        if (loc == null) return null;
        if (loc.getBlock().getType() != org.bukkit.Material.JUKEBOX ||
                !loc.getWorld().equals(player.getWorld()) ||
                loc.distanceSquared(player.getLocation()) > 100) {
            selectedJukeboxes.remove(player);
            return null;
        }
        return loc;
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
        playerBloquinhos.clear();
        selectedJukeboxes.clear();
    }
}