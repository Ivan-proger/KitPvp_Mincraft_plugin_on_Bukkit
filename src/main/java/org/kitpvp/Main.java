package org.kitpvp;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Logger;

public final class Main extends JavaPlugin implements @NotNull Listener {
    public static final Logger _log  = Logger.getLogger("Minecraft");
    private static Main instance;
    private Menu menu = new Menu(Bukkit.createInventory(null, 9 * 6, "Menu"));

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        try {
            PlayersDB.Conn();
            PlayersDB.CreateDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "-=-=-==-==-=-=-=-=-=- Запускаю KitPvp -=-=-==-==-=-=-=-=-=-");
        Bukkit.getPluginManager().registerEvents(this, this);
        stackGlass();


    }

    @Override
    public void onDisable() {
        try {
            PlayersDB.CloseDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void stackGlass(){
        for(int i = 0; i < 9; i++){
            menu.setSlotMenu(instance.getConfig().getString("Class.material"), " ",
                    i, (ArrayList) instance.getConfig().getList("Class.lore"));
            int j = i + 45;
            menu.setSlotMenu(instance.getConfig().getString("Class.material"), " ",
                    j, (ArrayList) instance.getConfig().getList("Class.lore"));
        }
        for(int x = 0; x < 44; x += 9){
            menu.setSlotMenu(instance.getConfig().getString("Class.material"), " ",
                    x, (ArrayList) instance.getConfig().getList("Class.lore"));
            int j = x + 8;
            menu.setSlotMenu(instance.getConfig().getString("Class.material"), " ",
                    j, (ArrayList) instance.getConfig().getList("Class.lore"));
        }
    }


    @EventHandler
    public void playerJoin(PlayerJoinEvent e) throws SQLException, ClassNotFoundException {
        Player p = (Player) e.getPlayer();
        PlayersDB.setJoinPlayerDB(p.getName());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws SQLException, ClassNotFoundException {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();


        if (event.getView().getTitle().equalsIgnoreCase("Menu")) {
            if (event.getCurrentItem() == null
                    || event.getCurrentItem().getType() == Material.AIR
                    || !event.getCurrentItem().hasItemMeta()) {
                player.closeInventory();
                event.setCancelled(true);
                return;
            }
            if(item.getLore().equals(instance.getConfig().getList("Class.lore"))){event.setCancelled(true); return;}
            ArrayList<String> kits = PlayersDB.getKitsPlayer(player.getName());

            for (String key : instance.getConfig().getConfigurationSection("Items").getKeys(false)) {
                if(item.getItemMeta().getDisplayName().equals(instance.getConfig().getString("Items." + key + ".name"))) {
                    if (kits.contains(instance.getConfig().getString("Items." + key + ".name"))) {
                        event.setCancelled(true);
                        player.getInventory().clear();
                        player.getInventory().addItem();
                        ItemStack[] stack_kit_start = new ItemStack[4];
                        stack_kit_start[3] = new ItemStack(Material.valueOf(instance.getConfig().getString("Items." + key + ".helmet")));
                        stack_kit_start[2] = new ItemStack(Material.valueOf(instance.getConfig().getString("Items." + key + ".chestplate")));
                        stack_kit_start[1] = new ItemStack(Material.valueOf(instance.getConfig().getString("Items." + key + ".leggings")));
                        stack_kit_start[0] = new ItemStack(Material.valueOf(instance.getConfig().getString("Items." + key + ".boots")));

                        player.getInventory().setArmorContents(stack_kit_start);
                        player.closeInventory();
                        player.sendMessage(ChatColor.AQUA + "Вы выбрали " + instance.getConfig().getString("Items." + key + ".name") + "!");
                    }
                    else {
                        break;
                    }
                }
            }
        }
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) throws SQLException, ClassNotFoundException {
        if (!(e.getRightClicked() instanceof Villager)) return;
        Entity ent = e.getRightClicked();
        if ((ent instanceof Villager)) {
            Player p = e.getPlayer();
            if (ent.getCustomName() == null) return;
            if (ent.getCustomName().equalsIgnoreCase("§4§lОружейник")) {
                e.setCancelled(true);
                ArrayList<String> kits = PlayersDB.getKitsPlayer(e.getPlayer().getName());

                for (String key : instance.getConfig().getConfigurationSection("Items").getKeys(false)) {
                    ArrayList<String> lore = (ArrayList<String>) instance.getConfig().getList("Items." + key + ".lore");

                    if (kits.contains(instance.getConfig().getString("Items." + key + ".name"))){lore.add(ChatColor.GREEN+ "Доступно!");}
                    else {lore.add(ChatColor.RED + "Недоступно!");}

                    menu.setSlotMenu(instance.getConfig().getString("Items." + key + ".material"), instance.getConfig().getString("Items." + key + ".name"),
                            instance.getConfig().getInt("Items." + key + ".size"), lore);
                    lore.remove(lore.size() - 1);
                }

                menu.openInventory(p);
            }
        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        if (!e.hasBlock()) return;

        if (e.getPlayer().isOp() || e.getPlayer().hasPermission("kitPvp.spawnNpc")) { //проверка на права
            ItemStack stack = e.getPlayer().getInventory().getItemInMainHand(); // получаем стак
            if (stack.getType() == Material.GOLDEN_AXE){
                ItemMeta meta = stack.getItemMeta(); // получаем меты стака
                if (meta.hasDisplayName() && meta.getDisplayName().equalsIgnoreCase("Axe spawn")) { //проверяем мету
                    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        Villager villager = (Villager) e.getPlayer().getWorld().spawnEntity(e.getClickedBlock().getLocation(), EntityType.VILLAGER); //спавн виладжера по блоку на который клинкнули пкм
                        villager.setCustomName("§4§lОружейник");
                        villager.setAI(false);
                        villager.setInvulnerable(true);
                        e.getPlayer().sendMessage("§dИнписюн заспавнен!");
                    }
                }
            }
        }
    }
}
