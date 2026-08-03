package fr.skytasul.music.bloquinho;

import fr.skytasul.music.CommandMusic;
import fr.skytasul.music.JukeBox;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class BloquinhoListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType() == Material.JUKEBOX) {
            BloquinhoManager.registerJukebox(e.getBlock().getLocation(), e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
            Block block = e.getClickedBlock();
            if (block.getType() == Material.JUKEBOX) {
                Player player = e.getPlayer();

                if (!BloquinhoManager.isBloquinho(block.getLocation())) {
                    BloquinhoManager.registerJukebox(block.getLocation(), player);
                }

                BloquinhoInstance instance = BloquinhoManager.getBloquinho(block.getLocation());
                if (instance != null && instance.getCurrentSong() != null) {
                    JukeBox.sendMessage(player, "§eBloquinho tocando atualmente: §f" + JukeBox.getSongName(instance.getCurrentSong()));
                }

                BloquinhoManager.setSelectedJukebox(player, block.getLocation());
                e.setCancelled(true);
                CommandMusic.open(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.JUKEBOX) {
            BloquinhoManager.unregisterJukebox(e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        for (BloquinhoInstance instance : BloquinhoManager.getActiveBloquinhos().values()) {
            if (instance.getSongPlayer() != null) {
                instance.getSongPlayer().addPlayer(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onCloseGUI(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player player) {
            BloquinhoManager.clearSelectedJukebox(player);
        }
    }
}