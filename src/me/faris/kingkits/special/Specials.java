package me.faris.kingkits.special;

import java.util.logging.Logger;

import me.faris.kingkits.special.listeners.PlayerListener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Specials extends JavaPlugin {
	private Logger log = Logger.getLogger("Minecraft");

	public PlayerListener pListener = new PlayerListener(this);
	public Values configValues = new Values();

	public void onEnable() {
		this.log = this.getLogger();
		this.log.info(this.getDescription().getFullName() + " by KingFaris10 is now enabled.");
		this.loadConfiguration();

		this.getServer().getPluginManager().registerEvents(this.pListener, this);
	}

	public void onDisable() {
		this.log = this.getLogger();
		this.log.info(this.getDescription().getFullName() + " by KingFaris10 is now disabled.");

		for (String playerName : this.pListener.ghostPlayers) {
			Player player = Bukkit.getPlayerExact(playerName);
			if (player != null) {
				player.setAllowFlight(false);
			}
		}

		this.getServer().getScheduler().cancelTasks(this);
	}

	private void loadConfiguration() {
		this.getConfig().options().header("KingKits Special Abilities");
		this.getConfig().addDefault("Enable snowball kit", true);
		this.getConfig().addDefault("Enable fisher kit", true);
		this.getConfig().addDefault("Enable naturalist kit", true);
		this.getConfig().addDefault("Enable carrotman kit", true);
		this.getConfig().addDefault("Enable zombie kit", true);
		this.getConfig().addDefault("Enable viper kit", true);
		this.getConfig().addDefault("Enable stomper kit", true);
		this.getConfig().addDefault("Enable monk kit", true);
		this.getConfig().addDefault("Enable thor kit", true);
		this.getConfig().addDefault("Enable endermage kit", true);
		this.getConfig().addDefault("Enable switcher kit", true);
		this.getConfig().addDefault("Enable ghost kit", true);
		this.getConfig().addDefault("Enable halter kit", true);
		this.getConfig().addDefault("Enable snail kit", true);
		this.getConfig().addDefault("Snowball kit name", "Snowman");
		this.getConfig().addDefault("Fisher kit name", "Fisher");
		this.getConfig().addDefault("Naturalist kit name", "Naturalist");
		this.getConfig().addDefault("Carrotman kit name", "Carrotman");
		this.getConfig().addDefault("Zombie kit name", "Zombie");
		this.getConfig().addDefault("Viper kit name", "Viper");
		this.getConfig().addDefault("Stomper kit name", "Stomper");
		this.getConfig().addDefault("Monk kit name", "Monk");
		this.getConfig().addDefault("Thor kit name", "Thor");
		this.getConfig().addDefault("Endermage kit name", "Endermage");
		this.getConfig().addDefault("Switcher kit name", "Switcher");
		this.getConfig().addDefault("Ghost kit name", "Ghost");
		this.getConfig().addDefault("Halter kit name", "Halter");
		this.getConfig().addDefault("Snail kit name", "Snail");
		this.getConfig().addDefault("Snowball potion effect time", 3);
		this.getConfig().addDefault("Naturalist heal amount", 2);
		this.getConfig().addDefault("Carrotman potion effect time", 30);
		this.getConfig().addDefault("Viper potion effect time", 5);
		this.getConfig().addDefault("Ghost fly time", 5);
		this.getConfig().addDefault("Halter freeze time", 5);
		this.getConfig().addDefault("Snail potion effect time", 5);
		this.getConfig().addDefault("Stomper stomp radius", 2D);
		this.getConfig().addDefault("Monk cooldown", 5);
		this.getConfig().addDefault("Thor cooldown", 5);
		this.getConfig().addDefault("Endermage cooldown", 10);
		this.getConfig().addDefault("Switcher cooldown", 3);
		this.getConfig().addDefault("Ghost cooldown", 30);
		this.getConfig().options().copyDefaults(true);
		this.getConfig().options().copyHeader(true);
		this.saveConfig();

		this.configValues.snowman = this.getConfig().getBoolean("Enable snowman kit");
		this.configValues.strSnowmanKit = this.getConfig().getString("Snowball kit name");
		this.configValues.snowmanBlindnessTime = this.getConfig().getInt("Snowball potion effect time");

		this.configValues.fisher = this.getConfig().getBoolean("Enable fisher kit");
		this.configValues.strFisherKit = this.getConfig().getString("Fisher kit name");

		this.configValues.naturalist = this.getConfig().getBoolean("Enable naturalist kit");
		this.configValues.strNaturalistKit = this.getConfig().getString("Naturalist kit name");
		this.configValues.naturalistHealChance = this.getConfig().getDouble("Naturalist heal chance") / 100;
		this.configValues.naturalistHealAmount = this.getConfig().getInt("Naturalist heal amount");

		this.configValues.carrotMan = this.getConfig().getBoolean("Enable carrotman kit");
		this.configValues.strCarrotManKit = this.getConfig().getString("Carrotman kit name");
		this.configValues.nightVisionTime = this.getConfig().getInt("Carrotman potion effect time");

		this.configValues.zombie = this.getConfig().getBoolean("Enable zombie kit");
		this.configValues.strZombieKit = this.getConfig().getString("Zombie kit name");

		this.configValues.viper = this.getConfig().getBoolean("Enable viper kit");
		this.configValues.strZombieKit = this.getConfig().getString("Viper kit name");
		this.configValues.viperPoisonTime = this.getConfig().getInt("Viper potion effect time");

		this.configValues.stomper = this.getConfig().getBoolean("Enable stomper kit");
		this.configValues.strStomperKit = this.getConfig().getString("Stomper kit name");
		this.configValues.stomperStompRadius = this.getConfig().getDouble("Stomper stomp radius");

		this.configValues.monk = this.getConfig().getBoolean("Enable monk kit");
		this.configValues.strMonkKit = this.getConfig().getString("Monk kit name");
		this.configValues.monkCooldown = this.getConfig().getInt("Monk cooldown");

		this.configValues.thor = this.getConfig().getBoolean("Enable thor kit");
		this.configValues.strThorKit = this.getConfig().getString("Thor kit name");
		this.configValues.thorCooldown = this.getConfig().getInt("Thor cooldown");

		this.configValues.endermage = this.getConfig().getBoolean("Enable endermage kit");
		this.configValues.strEndermageKit = this.getConfig().getString("Endermage kit name");
		this.configValues.endermageCooldown = this.getConfig().getInt("Endermage cooldown");

		this.configValues.switcher = this.getConfig().getBoolean("Enable switcher kit");
		this.configValues.strSwitcherKit = this.getConfig().getString("Switcher kit name");
		this.configValues.switcherCooldown = this.getConfig().getInt("Switcher cooldown");

		this.configValues.ghost = this.getConfig().getBoolean("Enable ghost kit");
		this.configValues.strGhostKit = this.getConfig().getString("Ghost kit name");
		this.configValues.ghostCooldown = this.getConfig().getInt("Ghost cooldown");
		this.configValues.ghostFlyTime = this.getConfig().getInt("Ghost fly time");

		this.configValues.halter = this.getConfig().getBoolean("Enable halter kit");
		this.configValues.strHalterKit = this.getConfig().getString("Halter kit name");
		this.configValues.halterFreezeTime = this.getConfig().getInt("Halter freeze time");

		this.configValues.snail = this.getConfig().getBoolean("Enable snail kit");
		this.configValues.strSnailKit = this.getConfig().getString("Snail kit name");
		this.configValues.snailSlownessTime = this.getConfig().getInt("Snail potion effect time");
	}

}
