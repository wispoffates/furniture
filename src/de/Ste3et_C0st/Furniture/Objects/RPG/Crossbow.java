package de.Ste3et_C0st.Furniture.Objects.RPG;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.Ste3et_C0st.Furniture.Main.main;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events.ProjectClickEvent;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureHelper;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class Crossbow extends FurnitureHelper implements Listener  {

	public Crossbow(ObjectID id){
		super(id);
		Bukkit.getPluginManager().registerEvents(this, main.instance);
	}
	
	@EventHandler
	public void onFurnitureClick(ProjectClickEvent e) {
		if(e.getID() == null || getObjID() == null) return;
		if(getObjID().getSQLAction().equals(SQLAction.REMOVE)){return;}
		if(!e.getID().equals(getObjID())) return;
		if(!e.canBuild()){return;}
		fEntity stand = getArmorStand();
		if(stand==null){return;}
		ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
		if(is!=null&& (is.getType().equals(Material.ARROW) || is.getType().equals(Material.SPECTRAL_ARROW) || is.getType().equals(Material.TIPPED_ARROW)) ){
			if(!hasArrow()){
				fEntity entity = getArmorStand();
				entity.setItemInMainHand(is.clone());
				update();
				consumeItem(e.getPlayer());
				return;
			}
		}
		if(hasArrow()){
			spawnArrow(getArrow().getType(), e.getPlayer());
		}
	}
	
	private void spawnArrow(Material mat, Player p){
		Location loc = getRelative(getCenter(), getBlockFace(), 0,18);
		loc.setYaw(getYaw());
		Vector v= getLaunchVector(getBlockFace());
		if(v == null) return;
		getWorld().playSound(getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
		Location start = getRelative(getCenter(), getBlockFace(), 0,0);
		start.setYaw(getYaw());
		start = start.add(0, 1.8, 0);
		Arrow a = null;
		if(mat.equals(Material.ARROW)){
			a = (Arrow) getWorld().spawnEntity(start, EntityType.ARROW);
		}else if(mat.equals(Material.SPECTRAL_ARROW)){
			a = (SpectralArrow) getWorld().spawnEntity(start, EntityType.SPECTRAL_ARROW);
		}else if(mat.equals(Material.TIPPED_ARROW)){
			a = (TippedArrow) getWorld().spawnEntity(start, EntityType.TIPPED_ARROW);
		}
		a.setCritical(true);
		a.setVelocity(v);
		a.setShooter(p);
		fEntity entity = getArmorStand();
		entity.setItemInMainHand(null);
		update();
		
	}
	
	public Vector getLaunchVector(BlockFace face){
		int l =random(300, 150);
		double h = ((double) l / 100);
		
		Vector v = new Vector(0, 0, h);
		switch (face) {
			case SOUTH:v= new Vector(-v.getY(), v.getY(), -v.getZ());break;
			case EAST: v= new Vector(-v.getZ(), v.getY(), -v.getY());break;
			case WEST: v= new Vector(v.getZ(), v.getY(), v.getY());break;
			default:break;
		}
		return v;
	}
	
	public int random(int max, int min){
		Random r = new Random();
		int randInt = r.nextInt(max-min) + min;
		return randInt;
	}
	
	private fEntity getArmorStand(){
		for(fEntity stand : getfAsList()){
			if(stand.getName().equalsIgnoreCase("#ARROW#")){
				return stand;
			}
		}
		return null;
	}
	
	private ItemStack getArrow(){
		for(fEntity stand : getfAsList()){
			if(stand.getName().equalsIgnoreCase("#ARROW#")){
				if(!(stand.getItemInMainHand()==null||stand.getItemInMainHand().getType()==null||stand.getItemInMainHand().getType().equals(Material.AIR))){
					return stand.getItemInMainHand();
				}
			}
		}
		return null;
	}

	private boolean hasArrow(){
		for(fEntity stand : getfAsList()){
			if(stand.getName().equalsIgnoreCase("#ARROW#")){
				if(stand.getItemInMainHand()==null||stand.getItemInMainHand().getType()==null||stand.getItemInMainHand().getType().equals(Material.AIR)){
					return false;
				}else{
					return true;
				}
			}
		}
		return false;
	}
}
