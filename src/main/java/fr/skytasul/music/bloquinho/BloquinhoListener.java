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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class BloquinhoListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType() == Material.JUKEBOX) {
            Player player = e.getPlayer();

            if (!player.hasPermission("music.bloquinho.create")) {
                JukeBox.sendMessage(player, "§cVocê não tem permissão para colocar um Bloquinho!");
                e.setCancelled(true);
                return;
            }

            if (BloquinhoManager.hasBloquinho(player.getUniqueId())) {
                JukeBox.sendMessage(player, "§cVocê já possui 1 Bloquinho ativo! Quebre o seu Bloquinho anterior para colocar outro.");
                e.setCancelled(true);
                return;
            }

            BloquinhoManager.registerJukebox(e.getBlock().getLocation(), player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
            Block block = e.getClickedBlock();
            if (block.getType() == Material.JUKEBOX) {
                Player player = e.getPlayer();


                e.setCancelled(true);

                if (!player.hasPermission("music.bloquinho.use")) {
                    JukeBox.sendMessage(player, "§cVocê não tem permissão para usar este Bloquinho!");
                    return;
                }

                if (!BloquinhoManager.isBloquinho(block.getLocation())) {
                    BloquinhoManager.registerJukebox(block.getLocation(), player);
                }

                BloquinhoInstance instance = BloquinhoManager.getBloquinho(block.getLocation());
                if (instance != null && instance.getCurrentSong() != null) {
                    JukeBox.sendMessage(player, "§eBloquinho tocando atualmente: §f" + JukeBox.getSongName(instance.getCurrentSong()));
                }

                BloquinhoManager.setSelectedJukebox(player, block.getLocation());
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
}