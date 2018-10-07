/*
 * Murder Mystery is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Murder Mystery is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Murder Mystery.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.murdermystery.utils;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI;
import org.golde.bukkit.corpsereborn.nms.Corpses;

import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.ArenaCorpse;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.arena.ArenaState;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * @author Plajer
 * <p>
 * Created at 06.10.2018
 */
public class Utils {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);

  public static void applyActionBarCooldown(Player p, int seconds) {
    new BukkitRunnable() {
      int ticks = 0;

      @Override
      public void run() {
        if (!ArenaRegistry.isInArena(p) || ArenaRegistry.getArena(p).getArenaState() != ArenaState.IN_GAME) {
          this.cancel();
        }
        if (ticks >= seconds * 20) {
          this.cancel();
        }
        String progress = MinigameUtils.getProgressBar(ticks, 5 * 20, 10, "■", "&a", "&c");
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatManager.colorMessage("In-Game.Cooldown-Format")
            .replace("%progress%", progress).replace("%time%", String.valueOf((double) (100 - ticks) / 20))));
        ticks += 10;
      }
    }.runTaskTimer(plugin, 10, 10);
  }

  public static void spawnCorpse(Player p, Arena arena) {
    try {
      Corpses.CorpseData corpse = CorpseAPI.spawnCorpse(p, p.getLocation());
      Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().clone().add(0, 1.5, 0));
      hologram.appendTextLine(ChatManager.colorMessage("In-Game.Messages.Corpse-Last-Words").replace("%player%", p.getName()));
      //todo priority note to wiki
      if(p.hasPermission("murdermystery.lastwords.meme")) {
        hologram.appendTextLine(ChatManager.colorMessage("In-Game.Messages.Last-Words.Meme"));
      } else if(p.hasPermission("murdermystery.lastwords.rage")) {
        hologram.appendTextLine(ChatManager.colorMessage("In-Game.Messages.Last-Words.Rage"));
      } else if(p.hasPermission("murdermystery.lastwords.pro")) {
        hologram.appendTextLine(ChatManager.colorMessage("In-Game.Messages.Last-Words.Pro"));
      } else {
        hologram.appendTextLine(ChatManager.colorMessage("In-Game.Messages.Last-Words.Default"));
      }
      arena.addCorpse(new ArenaCorpse(hologram, corpse));
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

}