package me.faris.kingkits.special.listeners.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageByPlayerEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private EntityDamageByEntityEvent damageEvent = null;

	public PlayerDamageByPlayerEvent(EntityDamageByEntityEvent entityDamageEvent) {
		this.damageEvent = entityDamageEvent;
	}

	public EntityDamageByEntityEvent getDamageEvent() {
		return this.damageEvent;
	}
	
	public Player getDamager() {
		return (Player) this.damageEvent.getDamager();
	}

	public Player getPlayer() {
		return (Player) this.damageEvent.getEntity();
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
