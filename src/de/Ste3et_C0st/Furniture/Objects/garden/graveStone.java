package de.Ste3et_C0st.Furniture.Objects.garden;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import de.Ste3et_C0st.Furniture.Main.main;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events.ProjectBreakEvent;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events.ProjectClickEvent;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureHelper;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class graveStone extends FurnitureHelper implements Listener{

	private Location signLoc;
	private Block sign;
	private String[] lines = new String[4];
	
	public graveStone(ObjectID id) {
		super(id);
		setBlock();
		Bukkit.getPluginManager().registerEvents(this, main.instance);
	}
	
	@SuppressWarnings("deprecation")
	private void setBlock(){
		Location location = getLocation().clone();
		if(getBlockFace().equals(BlockFace.WEST)){location = getLutil().getRelativ(location, getBlockFace(), .0, -1.02);}
		if(getBlockFace().equals(BlockFace.SOUTH)){location = getLutil().getRelativ(location, getBlockFace(), -1.0, -1.02);}
		if(getBlockFace().equals(BlockFace.EAST)){location = getLutil().getRelativ(location, getBlockFace(), -1.0, .0);}
		Location center = getLutil().getRelativ(location, getBlockFace(), .18D, .955D);
		center.setYaw(getLutil().FaceToYaw(getBlockFace().getOppositeFace()) + 90);
		Location kreutz2 = getLutil().getRelativ(center, getBlockFace(), -.23, -1.27);
		Location sign = getLutil().getRelativ(kreutz2.getBlock().getLocation(), getBlockFace(), 0D, 1D);
		this.signLoc = sign;
		
		if(!sign.getBlock().getType().equals(Material.WALL_SIGN)){
			sign.getBlock().setType(Material.WALL_SIGN);
			this.sign = sign.getBlock();
			BlockState state = this.sign.getState();
			LocationUtil util = getLutil();
			state.setRawData(util.getFacebyte(util.yawToFace(getYaw() + 90)));
			state.update();
		}else{
			this.sign = sign.getBlock();
		}
		this.lines = getText();
		getObjID().addBlock(Arrays.asList(this.sign));
	}
	
	@EventHandler
	private void onBlockBreak(ProjectBreakEvent e){
		if(e.getID() == null || getObjID() == null) return;
		if(!e.getID().equals(getObjID())){return;}
		if(!e.canBuild()){return;}
		if(sign!=null){
			sign.setType(Material.AIR);
		}
	}
	
	@EventHandler
	private void onBlockClick(ProjectClickEvent e){
		if(e.getID() == null || getObjID() == null) return;
		if(!e.getID().equals(getObjID())){return;}
		if(!e.canBuild()){return;}
		Player p = e.getPlayer();
		if(!e.getID().equals(getObjID())) return;
		if(!e.canBuild()){return;}
		ItemStack is = p.getInventory().getItemInMainHand();
		if (is == null) return;
		if (!is.getType().equals(Material.WRITTEN_BOOK)) return;
		readFromBook(is);
	}

	public void resetSign(){
		Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
			@Override
			public void run() {
				sign = getLutil().setSign(getBlockFace(), signLoc);
				placetext();
			}
		});
	}
	
	public Location getSignLocation(){return this.signLoc;}
	
	public void removeSign(){
		if(sign!=null){
			sign.setType(Material.AIR);
			sign = null;
			getManager().remove(getObjID());
			delete();
		}
	}
	
	public void readFromBook(ItemStack is){
		BookMeta bm = (BookMeta) is.getItemMeta();
		if(bm == null){return;}
		String side = bm.getPage(1);
		if(side==null){return;}
		String lines[] = side.split("\\r?\\n");
		
		Integer line = 0;
		for(String s : lines){
			if(s!=null && line<=3){
				Integer i = 15;
				if(s.length()>=15){i=15;}else{i=s.length();}
				String a = s.substring(0, i);
				if(a!=null){
					a = ChatColor.translateAlternateColorCodes('&', a);
					setText(line, a);
				}
				line++;
			}
		}
		
		if(line!=3){
			for(int i = line; i<=3; i++){
				setText(i, "");
			}
		}
		return;
	}
	
	public void placetext(){
		if ((this.sign.getState() instanceof Sign) && lines != null){
			Sign sign = (Sign) this.sign.getState();
			Integer i = 0;
			for(String s : lines){
				if(i>3){break;}
				sign.setLine(i, s);
				i++;
			}
			sign.update(true, false);
		}
	}
	
	public String[] getText(){
		if(sign==null || !sign.getType().equals(Material.WALL_SIGN)){return null;}
		Sign sign = (Sign) this.sign.getState();
		return sign.getLines();
	}
	
	public void setText(Integer line, String text){
		if(line==null || text == null){return;}
		if(sign==null || !sign.getType().equals(Material.WALL_SIGN)){return;}
		Sign sign = (Sign) this.sign.getState();
		sign.setLine(line, text);
		sign.update(true, false);
		lines[line] = text;
	}
}
