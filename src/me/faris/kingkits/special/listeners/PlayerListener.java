package me.faris.kingkits.special.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.faris.kingkits.special.Specials;
import me.faris.kingkits.special.listeners.events.PlayerDamageByPlayerEvent;
import net.bukkit.faris.kingkits.hooks.PvPKits;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerListener implements Listener {
	private Specials plugin = null;
	private static final float STRENGTH_MULTIPLIER = 0.2F;

	public PlayerListener(Specials instance) {
		this.plugin = instance;
	}

	// Register the PlayerDamageByPlayerEvent event.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
		try {
			if (event.getEntity() == null || event.getDamager() == null) return;
			if (event.getEntity() instanceof Player) {
				if (event.getDamager() instanceof Player) {
					event.getEntity().getServer().getPluginManager().callEvent(new PlayerDamageByPlayerEvent(event));
				}
			}
		} catch (Exception ex) {
		}
	}

	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent event) {
		try {
			if (this.ghostPlayers.contains(event.getPlayer().getName())) {
				event.getPlayer().setAllowFlight(false);
				this.ghostPlayers.remove(event.getPlayer().getName());
			}
		} catch (Exception ex) {
		}
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		try {
			if (this.ghostPlayers.contains(event.getPlayer().getName())) {
				event.getPlayer().setAllowFlight(false);
				this.ghostPlayers.remove(event.getPlayer().getName());
			}
		} catch (Exception ex) {
		}
	}

	// Blind the target if hit by a snowman with a snowball.
	@EventHandler
	public void snowmanKit(EntityDamageByEntityEvent event) {
		try {
			if (this.plugin.configValues.snowman) {
				if (event.getEntity() instanceof Player) {
					if (event.getDamager() instanceof Snowball) {
						Snowball snowball = (Snowball) event.getDamager();
						if (snowball.getShooter() instanceof Player) {
							Player player = (Player) event.getEntity();
							Player damager = (Player) snowball.getShooter();
							if (PvPKits.hasKit(damager.getName())) {
								if (PvPKits.getKit(damager.getName()).equalsIgnoreCase(this.plugin.configValues.strSnowmanKit)) {
									if (PvPKits.hasKit(player.getName(), false)) {
										if (PvPKits.getKit(player.getName()).equalsIgnoreCase(this.plugin.configValues.strSnowmanKit)) {
											player.sendMessage(ChatColor.RED + "You cannot blind other snowmen!");
											return;
										}
									}
									damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.plugin.configValues.snowmanBlindnessTime * 20, 1));
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	// Fish players towards the player.
	@EventHandler
	public void fisherKit(PlayerFishEvent event) {
		try {
			if (this.plugin.configValues.fisher) {
				if (event.getCaught() != null) {
					if (event.getCaught() instanceof Player) {
						if (PvPKits.hasKit(event.getPlayer().getName())) {
							if (PvPKits.getKit(event.getPlayer().getName()).equalsIgnoreCase(this.plugin.configValues.strFisherKit)) {
								Player player = event.getPlayer();
								Player fishedPlayer = (Player) event.getCaught();
								Vector velo = player.getLocation().toVector().subtract(fishedPlayer.getLocation().toVector()).multiply(STRENGTH_MULTIPLIER);
								fishedPlayer.setVelocity(velo);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	// There's a 3% chance of healing the player if they are standing on a grass block.
	@EventHandler
	public void naturalistKit(PlayerMoveEvent event) {
		try {
			if (this.plugin.configValues.naturalist) {
				if (this.hasMovedHorziontally(event.getFrom(), event.getTo())) {
					if (this.getBlockUnder(event.getPlayer().getLocation()).getType() == Material.GRASS) {
						Player player = event.getPlayer();
						if (PvPKits.hasKit(player.getName(), false)) {
							if (PvPKits.getKit(player.getName()).equalsIgnoreCase(this.plugin.configValues.strNaturalistKit)) {
								double r = Math.random();
								if (r < this.plugin.configValues.naturalistHealChance) {
									int healAmount = this.plugin.configValues.naturalistHealAmount;
									if (healAmount < 1) healAmount = 2;
									player.setHealth(player.getHealth() + healAmount);
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	// Give night vision to those that eat a carrot!
	@EventHandler
	public void nightKit(PlayerItemConsumeEvent event) {
		try {
			if (this.plugin.configValues.carrotMan) {
				if (event.getPlayer() != null) {
					if (event.getItem() != null) {
						if (PvPKits.hasKit(event.getPlayer().getName(), false)) {
							if (PvPKits.getKit(event.getPlayer().getName()).equalsIgnoreCase(this.plugin.configValues.strCarrotManKit)) {
								if (event.getItem().getType() == Material.CARROT_ITEM) event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, this.plugin.configValues.nightVisionTime * 20, 1));
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	// Stop zombies from attacking the player.
	@EventHandler
	public void zombieKitTarget(EntityTargetEvent event) {
		try {
			if (this.plugin.configValues.zombie) {
				if (event.getEntity() != null) {
					if (event.getEntityType() == EntityType.ZOMBIE) {
						Zombie entity = (Zombie) event.getEntity();
						if (entity.getTarget() != null) {
							if (entity.getTarget() instanceof Player) {
								Player player = (Player) entity.getTarget();
								if (PvPKits.hasKit(player.getName())) {
									if (PvPKits.getKit(player.getName()).equalsIgnoreCase(this.plugin.configValues.strZombieKit)) event.setTarget(null);
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	@EventHandler
	public void zombieKitTargetEntity(EntityTargetLivingEntityEvent event) {
		try {
			if (this.plugin.configValues.zombie) {
				if (event.getEntityType() == EntityType.ZOMBIE) {
					Zombie entity = (Zombie) event.getEntity();
					if (entity.getTarget() != null) {
						if (entity.getTarget() instanceof Player) {
							Player player = (Player) entity.getTarget();
							if (PvPKits.hasKit(player.getName())) {
								if (PvPKits.getKit(player.getName()).equalsIgnoreCase(this.plugin.configValues.strZombieKit)) event.setTarget(null);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	@EventHandler
	public void zombieKitAttack(EntityDamageByEntityEvent event) {
		try {
			if (this.plugin.configValues.zombie) {
				if (event.getDamager() != null) {
					if (event.getEntityType() == EntityType.ZOMBIE) {
						if (event.getDamager() instanceof Player) {
							Player player = (Player) event.getDamager();
							if (PvPKits.hasKit(player.getName())) {
								if (PvPKits.getKit(player.getName()).equalsIgnoreCase(this.plugin.configValues.strZombieKit)) {
									((Zombie) event.getEntity()).setTarget(null);
									event.setCancelled(true);
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	// Poison the target when a player hits another player with a stone/wooden sword.
	@EventHandler
	public void viperKit(EntityDamageByEntityEvent event) {
		try {
			if (this.plugin.configValues.viper) {
				if (event.getDamager() != null) {
					if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
						Player damaged = (Player) event.getEntity();
						Player damager = (Player) event.getDamager();
						if (damager.getInventory().getItemInHand() != null) {
							if (damager.getInventory().getItemInHand().getType() == Material.WOOD_SWORD || damager.getInventory().getItemInHand().getType() == Material.STONE_SWORD) {
								if (PvPKits.hasKit(damager.getName())) {
									if (PvPKits.getKit(damager.getName()).equalsIgnoreCase(this.plugin.configValues.strViperKit)) {
										if (PvPKits.hasKit(damaged.getName())) {
											if (PvPKits.getKit(damaged.getName()).equalsIgnoreCase(this.plugin.configValues.strViperKit)) return;
										}
										if (Math.random() > 0.4 && Math.random() < 0.6) damaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, this.plugin.configValues.viperPoisonTime * 20, 0));
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	public List<String> stomperKilled = new ArrayList<String>();

	// Kill anyone in a 2 block radius of the player who fell.
	@EventHandler
	public void stomperKit(EntityDamageEvent event) {
		try {
			if (this.plugin.configValues.stomper) {
				if (event.getEntity() != null) {
					if (event.getEntity() instanceof Player) {
						if (event.getCause() == DamageCause.FALL) {
							Player p = (Player) event.getEntity();
							if (p.getFallDistance() >= 3F) {
								if (PvPKits.hasKit(p.getName())) {
									if (PvPKits.getKit(p.getName()).equalsIgnoreCase(this.plugin.configValues.strStomperKit)) {
										boolean hurtPlayer = false;
										Location pLoc = p.getLocation().clone();
										pLoc.setY(0);
										for (int i = 0; i < p.getWorld().getPlayers().size(); i++) {
											Player t = p.getWorld().getPlayers().get(i);
											if (t != null) {
												if (t.isOnline()) {
													if (!p.getName().equals(t.getName())) {
														Location tLoc = t.getLocation();
														tLoc.setY(0);
														if (t.getGameMode() != GameMode.CREATIVE) {
															if (pLoc.distance(tLoc) <= this.plugin.configValues.stomperStompRadius) {
																if (!t.isSneaking() && !t.isBlocking()) {
																	hurtPlayer = true;
																	t.setHealth(0);
																	this.stomperKilled.add(t.getName());
																} else if (!t.isSneaking() || !t.isBlocking()) {
																	hurtPlayer = true;
																	if (t.getHealth() - 6 > 0) t.setHealth(t.getHealth() - 6);
																	else {
																		t.setHealth(0);
																		this.stomperKilled.add(t.getName());
																	}
																}
															}
														}
													}
												}
											}
										}
										if (hurtPlayer) event.setDamage(event.getDamage() / 5);
										else event.setDamage(event.getDamage() / 4);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	@EventHandler
	public void stomperDeath(PlayerDeathEvent event) {
		try {
			if (event.getEntity() != null) {
				if (this.stomperKilled.contains(event.getEntity().getName())) {
					event.setDeathMessage(event.getEntity().getName() + " was stomped to death.");
					this.stomperKilled.remove(event.getEntity().getName());
				}
			}
		} catch (Exception ex) {
		}
	}

	public List<String> monkDelay = new ArrayList<String>();

	// Move the player's hand item to another slot in their inventory when another player right clicks them with a blaze rod.
	@EventHandler
	public void monkKit(PlayerInteractEntityEvent event) {
		try {
			if (this.plugin.configValues.monk) {
				if (event.getPlayer() != null) {
					if (event.getRightClicked() != null) {
						if (event.getRightClicked().getType() == EntityType.PLAYER) {
							Player clicker = event.getPlayer();
							Player clicked = (Player) event.getRightClicked();
							if (PvPKits.hasKit(clicker.getName())) {
								if (PvPKits.getKit(clicker.getName()).equalsIgnoreCase(this.plugin.configValues.strMonkKit)) {
									if (clicker.getInventory().getItemInHand() != null) {
										if (clicker.getInventory().getItemInHand().getType() == Material.BLAZE_ROD) {
											if (!this.monkDelay.contains(clicker.getName())) {
												if (clicked.getInventory().getItemInHand() != null) {
													if (clicked.getInventory().getItemInHand().getType() != Material.AIR) {
														int freeSlot = clicked.getInventory().firstEmpty();
														Random rand = new Random();
														if (freeSlot > 9) {
															ItemStack itemInHand = clicked.getInventory().getItemInHand();
															clicked.getInventory().setItem(freeSlot, itemInHand);
															clicked.getInventory().setItemInHand(new ItemStack(Material.AIR));
														} else {
															int r = rand.nextInt(clicked.getInventory().getSize() + 1);
															while (r == clicked.getInventory().getHeldItemSlot())
																r = rand.nextInt(clicked.getInventory().getSize() + 1);
															ItemStack randomItem = null;
															if (clicked.getInventory().getItem(r) != null) randomItem = clicked.getInventory().getItem(r);
															else randomItem = new ItemStack(Material.AIR);
															ItemStack itemInHand = clicked.getInventory().getItemInHand();
															clicked.getInventory().setItem(r, itemInHand);
															clicked.getInventory().setItemInHand(randomItem);
														}
														if (this.plugin.configValues.monkCooldown > 0) {
															final String playerName = clicker.getName();
															this.monkDelay.add(playerName);
															clicked.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
																public void run() {
																	monkDelay.remove(playerName);
																}
															}, this.plugin.configValues.monkCooldown * 20L);
														}
													}
												}
											} else {
												int monkDelayTime = this.plugin.configValues.monkCooldown;
												if (monkDelayTime != 1) clicker.sendMessage(ChatColor.RED + "You must wait " + this.plugin.configValues.monkCooldown + " seconds before using your blaze rod again.");
												else clicker.sendMessage(ChatColor.RED + "You must wait " + this.plugin.configValues.monkCooldown + " second before using your blaze rod again.");
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	public List<String> thorDelay = new ArrayList<String>();

	// Cast down lightning when a player right clicks with a wooden axe on the floor.
	@EventHandler
	public void thorKit(PlayerInteractEvent event) {
		try {
			if (this.plugin.configValues.thor) {
				if (event.getPlayer() != null && event.getAction() != null) {
					if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
						if (PvPKits.hasKit(event.getPlayer().getName())) {
							if (PvPKits.getKit(event.getPlayer().getName()).equalsIgnoreCase(this.plugin.configValues.strThorKit)) {
								if (event.getItem() != null) {
									if (event.getItem().getType() == Material.WOOD_AXE) {
										if (!this.thorDelay.contains(event.getPlayer().getName())) {
											event.getPlayer().getWorld().strikeLightning(event.getClickedBlock().getWorld().getHighestBlockAt(event.getClickedBlock().getLocation()).getLocation().clone().add(0, 1, 0)).setFireTicks(0);

											if (this.plugin.configValues.thorCooldown > 0) {
												this.thorDelay.add(event.getPlayer().getName());
												final Player thor = event.getPlayer();
												event.getPlayer().getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
													public void run() {
														thorDelay.remove(thor.getName());
													}
												}, this.plugin.configValues.thorCooldown * 20L);
											}
										} else {
											int thorDelayTime = this.plugin.configValues.thorCooldown;
											if (thorDelayTime != 1) event.getPlayer().sendMessage(ChatColor.RED + "You must wait " + this.plugin.configValues.thorCooldown + " seconds before casting down thor again.");
											else event.getPlayer().sendMessage(ChatColor.RED + "You must wait " + this.plugin.configValues.thorCooldown + " second before casting down thor again.");
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	public List<String> endermageKit = new ArrayList<String>();
	public List<String> endermageInvincible = new ArrayList<String>();

	// If a player places a portal, teleport players under them to them.
	@EventHandler
	public void endermageKit(BlockPlaceEvent event) {
		try {
			if (this.plugin.configValues.endermage) {
				if (event.getBlock() != null) {
					if (event.getBlock().getType() == Material.PORTAL) {
						if (PvPKits.hasKit(event.getPlayer().getName())) {
							if (PvPKits.getKit(event.getPlayer().getName()).equalsIgnoreCase(this.plugin.configValues.strEndermageKit)) {
								Player endermager = event.getPlayer();
								if (!this.endermageKit.contains(endermager.getName())) {
									if (endermager.getServer().getOnlinePlayers().length > 0) {
										List<String> invinciblePlayers = new ArrayList<String>();
										Location bLoc = event.getBlock().getLocation().clone();
										bLoc.setY(0);
										for (Player onlineP : endermager.getServer().getOnlinePlayers()) {
											if (PvPKits.hasKit(onlineP.getName())) {
												if (PvPKits.getKit(onlineP.getName()).equalsIgnoreCase(this.plugin.configValues.strEndermageKit)) continue;
											}
											Location oLoc = onlineP.getLocation().clone();
											oLoc.setY(0);
											if (onlineP.getLocation().getWorld().getName().equals(endermager.getLocation().getWorld().getName())) {
												if (bLoc.distance(oLoc) <= 2.5F) {
													int yDiff = 0;
													if (event.getBlock().getLocation().getY() > onlineP.getLocation().getY()) yDiff = (int) (event.getBlock().getLocation().getY() - onlineP.getLocation().getY());
													else if (event.getBlock().getLocation().getY() < onlineP.getLocation().getY()) yDiff = (int) (onlineP.getLocation().getY() - event.getBlock().getLocation().getY());
													if (yDiff >= 4 && yDiff <= 60) {
														onlineP.teleport(event.getBlock().getLocation(), TeleportCause.PLUGIN);
														invinciblePlayers.add(onlineP.getName());
														onlineP.sendMessage(ChatColor.RED + "Warning: You are being teleported by an endermage. You are invincible for 5 seconds.");
													}
												}
											}
										}
										if (!invinciblePlayers.isEmpty()) invinciblePlayers.add(endermager.getName());

										if (this.plugin.configValues.endermageCooldown > 0) {
											this.endermageKit.add(endermager.getName());
											final String pName = endermager.getName();
											endermager.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
												public void run() {
													if (endermageKit.contains(pName)) endermageKit.remove(pName);
												}
											}, this.plugin.configValues.endermageCooldown * 20L);
										}

										for (final String invinciblePlayer : invinciblePlayers) {
											this.endermageInvincible.add(invinciblePlayer);
											endermager.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
												public void run() {
													if (endermageInvincible.contains(invinciblePlayer)) {
														endermageInvincible.remove(invinciblePlayer);
														if (Bukkit.getPlayer(invinciblePlayer) != null) Bukkit.getPlayer(invinciblePlayer).sendMessage(ChatColor.RED + "You are no longer invincible.");
													}
												}
											}, 100L);
										}
									}
									event.setCancelled(true);
								} else {
									endermager.sendMessage(ChatColor.RED + "You must wait a while before teleporting players again.");
									event.setCancelled(true);
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	@EventHandler
	public void endermageKitDamaged(EntityDamageEvent event) {
		try {
			if (event.getCause() == DamageCause.CUSTOM) return;
			if (event.getEntity() instanceof Player) {
				Player p = (Player) event.getEntity();
				if (this.endermageInvincible.contains(p.getName())) event.setCancelled(true);
			}
		} catch (Exception ex) {
		}
	}

	@EventHandler
	public void endermageKitDamageEntity(EntityDamageByEntityEvent event) {
		try {
			if (event.getDamager() instanceof Player) {
				Player p = (Player) event.getDamager();
				if (this.endermageInvincible.contains(p.getName())) event.setCancelled(true);
			}
		} catch (Exception ex) {
		}
	}

	public List<String> switcherDelay = new ArrayList<String>();

	// Switch locations with the target when a snowball hits them
	@EventHandler
	public void switcherKit(EntityDamageByEntityEvent event) {
		try {
			if (this.plugin.configValues.switcher) {
				if (event.getEntity() instanceof Player && event.getDamager() instanceof Snowball) {
					Snowball snowball = (Snowball) event.getDamager();
					if (snowball.getShooter() instanceof Player) {
						Player entity = (Player) event.getEntity();
						Player shooter = (Player) snowball.getShooter();
						if (PvPKits.hasKit(shooter.getName())) {
							if (PvPKits.getKit(shooter.getName()).equalsIgnoreCase(this.plugin.configValues.strSwitcherKit)) {
								if (PvPKits.hasKit(entity.getName())) {
									if (PvPKits.getKit(entity.getName()).equalsIgnoreCase(this.plugin.configValues.strSwitcherKit)) return;
								}
								if (!this.switcherDelay.contains(shooter.getName())) {
									Location tLocation = entity.getLocation();
									Location pLocation = shooter.getLocation();
									shooter.teleport(tLocation, TeleportCause.PLUGIN);
									shooter.sendMessage(ChatColor.RED + "You have swapped locations with " + entity.getName() + ".");
									entity.teleport(pLocation, TeleportCause.PLUGIN);
									entity.sendMessage(ChatColor.RED + "You have swapped locations with " + shooter.getName() + ".");

									final String pName = shooter.getName();
									if (this.plugin.configValues.switcherCooldown > 0) {
										this.switcherDelay.add(pName);
										shooter.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
											public void run() {
												switcherDelay.remove(pName);
											}
										}, this.plugin.configValues.switcherCooldown * 20L);
									}
								} else {
									shooter.sendMessage(ChatColor.RED + "There is a " + this.plugin.configValues.switcherCooldown + " second cooldown between each switch.");
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	public List<String> ghostPlayers = new ArrayList<String>();
	public List<String> ghostDelay = new ArrayList<String>();

	// Give players fly mode for a specific amount of time.
	@EventHandler
	public void ghostKit(PlayerInteractEvent event) {
		try {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					if (event.getItem().getType() == Material.FEATHER) {
						if (PvPKits.hasKit(event.getPlayer().getName())) {
							if (PvPKits.getKit(event.getPlayer().getName()).equals(this.plugin.configValues.strGhostKit)) {
								if (!this.ghostDelay.contains(event.getPlayer().getName())) {
									if (!event.getPlayer().getAllowFlight()) {
										final Player p = event.getPlayer();
										p.setAllowFlight(true);
										p.sendMessage(ChatColor.RED + "You can now fly for " + this.plugin.configValues.ghostFlyTime + " second" + (this.plugin.configValues.ghostFlyTime != 1 ? "s" : "") + ".");

										p.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
											public void run() {
												if (p != null) {
													p.setAllowFlight(false);
													p.sendMessage(ChatColor.RED + "Fly effects have worn off.");
												}
											}
										}, this.plugin.configValues.ghostFlyTime * 20L);

										if (this.plugin.configValues.ghostFlyTime > 3) {
											p.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
												public void run() {
													if (p != null) {
														p.sendMessage(ChatColor.RED + "You have 3 seconds of flight remaining.");
													}
												}
											}, this.plugin.configValues.ghostFlyTime * 20L - 60L);
											p.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
												public void run() {
													if (p != null) {
														p.sendMessage(ChatColor.RED + "You have 2 seconds of flight remaining.");
													}
												}
											}, this.plugin.configValues.ghostFlyTime * 20L - 40L);
											p.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
												public void run() {
													if (p != null) {
														p.sendMessage(ChatColor.RED + "You have 1 second of flight remaining.");
													}
												}
											}, this.plugin.configValues.ghostFlyTime * 20L - 20L);
										}

										final String pName = p.getName();
										this.ghostPlayers.add(pName);
										if (this.plugin.configValues.ghostCooldown > 0) {
											this.ghostDelay.add(pName);
											p.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
												public void run() {
													ghostDelay.remove(pName);
													ghostPlayers.remove(pName);
												}
											}, this.plugin.configValues.ghostCooldown * 20L);
										}
									} else {
										event.getPlayer().sendMessage(ChatColor.RED + "You can already fly.");
									}
								} else {
									event.getPlayer().sendMessage(ChatColor.RED + "There is a " + this.plugin.configValues.ghostCooldown + " second cooldown before each flight.");
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	public List<String> haltedPlayers = new ArrayList<String>();

	// Switch locations with the target when a snowball hits them
	@EventHandler
	public void halterKit(EntityDamageByEntityEvent event) {
		try {
			if (this.plugin.configValues.halter) {
				if (event.getEntity() instanceof Player && event.getDamager() instanceof Snowball) {
					Snowball snowball = (Snowball) event.getDamager();
					if (snowball.getShooter() instanceof Player) {
						Player entity = (Player) event.getEntity();
						Player shooter = (Player) snowball.getShooter();
						if (PvPKits.hasKit(shooter.getName())) {
							if (PvPKits.getKit(shooter.getName()).equalsIgnoreCase(this.plugin.configValues.strHalterKit)) {
								if (PvPKits.hasKit(entity.getName())) {
									if (PvPKits.getKit(entity.getName()).equalsIgnoreCase(this.plugin.configValues.strHalterKit)) return;
								}

								final Location tLoc = entity.getLocation();
								final Location tLoc2 = entity.getLocation().add(0, 1, 0);
								final Material oldBlock = tLoc.getBlock().getType();
								final byte oldBlockData = tLoc.getBlock().getData();
								final Material oldBlock2 = tLoc2.getBlock().getType();
								final byte oldBlock2Data = tLoc.getBlock().getData();
								tLoc.getWorld().getBlockAt(tLoc).setType(Material.ICE);
								tLoc2.getWorld().getBlockAt(tLoc2).setType(Material.ICE);

								final String pName = entity.getName();
								this.haltedPlayers.add(pName);
								shooter.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
									public void run() {
										haltedPlayers.remove(pName);

										if (tLoc != null) {
											if (tLoc.getWorld() != null) {
												if (tLoc.getWorld().getBlockAt(tLoc).getType() == Material.ICE) {
													if (oldBlock != Material.ICE) {
														tLoc.getWorld().getBlockAt(tLoc).setType(oldBlock);
														tLoc.getWorld().getBlockAt(tLoc).setData(oldBlockData);
													}
												}
											}
										}
										if (tLoc2 != null) {
											if (tLoc2.getWorld() != null) {
												if (tLoc2.getWorld().getBlockAt(tLoc).getType() == Material.ICE) {
													if (oldBlock2 != Material.ICE) {
														tLoc2.getWorld().getBlockAt(tLoc2).setType(oldBlock2);
														tLoc2.getWorld().getBlockAt(tLoc2).setData(oldBlock2Data);
													}
												}
											}
										}
									}
								}, this.plugin.configValues.halterFreezeTime * 20L);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	// Slow the target when a player hits another player with a stone/wooden sword.
	@EventHandler
	public void snailKit(EntityDamageByEntityEvent event) {
		try {
			if (this.plugin.configValues.snail) {
				if (event.getDamager() != null) {
					if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
						Player damaged = (Player) event.getEntity();
						Player damager = (Player) event.getDamager();
						if (damager.getInventory().getItemInHand() != null) {
							if (damager.getInventory().getItemInHand().getType() == Material.WOOD_SWORD || damager.getInventory().getItemInHand().getType() == Material.STONE_SWORD) {
								if (PvPKits.hasKit(damager.getName())) {
									if (PvPKits.getKit(damager.getName()).equalsIgnoreCase(this.plugin.configValues.strSnailKit)) {
										if (PvPKits.hasKit(damaged.getName())) {
											if (PvPKits.getKit(damaged.getName()).equalsIgnoreCase(this.plugin.configValues.strSnailKit)) return;
										}
										if (Math.random() > 0.4 && Math.random() < 0.6) damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, this.plugin.configValues.snailSlownessTime * 20, 0));
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}

	// If a string is numeric, return true else return false.
	@SuppressWarnings("unused")
	private boolean isNumeric(String val) {
		try {
			int i = Integer.parseInt(val);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	// If a player has moved from a block to another horizontally, return true else return false.
	private boolean hasMovedHorziontally(Location from, Location to) {
		if ((int) from.getX() != (int) to.getX() || (int) from.getZ() != (int) to.getZ()) return true;
		else return false;
	}

	// Return the block under the player.
	private Block getBlockUnder(Location loc) {
		return loc.getWorld().getBlockAt((int) loc.getX(), (int) loc.getY() - 1, (int) loc.getZ());
	}

}
