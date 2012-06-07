package team.ApiPlus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.sound.SoundManager;

import team.ApiPlus.API.PluginPlus;

import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class Util {
	
	public static boolean containsCustomItems(List<ItemStack> items){
		for(ItemStack i : items){
			if(isCustomItem(i)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isCustomItem(ItemStack item){
		return new SpoutItemStack(item).isCustomItem();
	}
	
	public static Block getBlockInSight(Location l, int blockIndex, int maxradius){
		BlockIterator bi = new BlockIterator(l.getWorld(), l.toVector(), l.getDirection(), 0d, maxradius);
		Block b = null;
		for(int i = 0; i<blockIndex; i++){
			if(bi.hasNext()){
				b =  bi.next(); 
			}else break;
		}
		return b;
	}

	public static Projectile launchProjectile(Class<? extends Projectile> c,
			Location from, Location to, float speed) {
		Projectile e = from.getWorld().spawn(from, c);
		e.setVelocity(to.toVector().multiply(speed));
		Bukkit.getPluginManager().callEvent(new ProjectileLaunchEvent(e));
		return e;
	}

	public static boolean canSee(PluginPlus p, final Location observer, final Location observed, int range) {
		Location o = observer.clone();
		Location w = observed.clone();
		if(o.toVector().distance(w.toVector())>range) return false;
		BlockIterator bitr = new BlockIterator(setLookingAt(o, w), 0, range);
		while (bitr.hasNext()) {
			Block b = bitr.next();
			if(b.equals(w.getBlock())) return true;
			if (!Util.isTransparent(p, b)) {
				break;
			}
		}
		return false;
	}

	public static Location getMiddle(Location l, float YShift) {
 		Location loc = l;
		loc = loc.getBlock().getLocation();
		Vector vec = loc.toVector();
		vec.add(new Vector(0.5, YShift, 0.5));
		loc = vec.toLocation(loc.getWorld());
		return loc;
	}

	
	public static void playCustomSound(Plugin plugin, Location l, String url,
			int volume) {
		SoundManager SM = SpoutManager.getSoundManager();
		SM.playGlobalCustomSoundEffect(plugin, url, false, l, 40, volume);
	}

	public static boolean isTransparent(PluginPlus p, Block block) {
		Material m = block.getType();
		if (p.getTransparentMaterials().contains(m)) {
			return true;
		}
		return false;
	}

	public static List<Entity> getNearbyEntities(Location loc, double radiusX,
			double radiusY, double radiusZ) {
		Entity e = loc.getWorld().spawn(loc, ExperienceOrb.class);
		@SuppressWarnings("unchecked")
		List<Entity> entities = (List<Entity>) ((ArrayList<Entity>) e.getNearbyEntities(radiusX, radiusY, radiusZ)).clone();
		e.remove();
		return entities;
	}

	public static int getRandomInteger(int start, int end) {
		Random rand = new Random();
		return start + rand.nextInt(end + 1);
	}

	public static Vector getDirection(Location l) {
		Vector vector = new Vector();

		double rotX = l.getYaw();
		double rotY = l.getPitch();

		vector.setY(-Math.sin(Math.toRadians(rotY)));

		double h = Math.cos(Math.toRadians(rotY));

		vector.setX(-h * Math.sin(Math.toRadians(rotX)));
		vector.setZ(h * Math.cos(Math.toRadians(rotX)));

		return vector;
	}

	public static Location setLookingAt(final Location loc, final Location lookat) {
		Location location = loc.clone();
		double dx = lookat.getX() - location.getX();
		double dy = lookat.getY() - location.getY();
		double dz = lookat.getZ() - location.getZ();

		if (dx != 0) {
			if (dx < 0) {
				location.setYaw((float) (1.5 * Math.PI));
			} else {
				location.setYaw((float) (0.5 * Math.PI));
			}
			location.setYaw((float) location.getYaw() - (float) Math.atan(dz / dx));
		} else if (dz < 0) {
			location.setYaw((float) Math.PI);
		}
		double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));
		location.setPitch((float) - Math.atan(dy / dxz));
		location.setYaw(-location.getYaw() * 180f / (float) Math.PI);
		location.setPitch(location.getPitch() * 180f / (float) Math.PI);

		return location;
	}

	public static Location getHandLocation(Player p) {
		Location loc = p.getLocation().clone();

		double a = loc.getYaw() / 180D * Math.PI + Math.PI / 2;
		double l = Math.sqrt(0.8D * 0.8D + 0.4D * 0.4D);

		loc.setX(loc.getX() + l * Math.cos(a) - 0.8D * Math.sin(a));
		loc.setY(loc.getY() + p.getEyeHeight() - 0.2D);
		loc.setZ(loc.getZ() + l * Math.sin(a) + 0.8D * Math.cos(a));
		return loc;
	}
	
	public static List<Block> getSphere(Location center, double radius) {
		List<Block> blockList = new ArrayList<Block>();
	    radius += 0.5;
	    final double radSquare = Math.pow(2, radius);
	    final int radCeil = (int) Math.ceil(radius);
	    final double centerX = center.getX();
	    final double centerY = center.getY();
	    final double centerZ = center.getZ();
	 
	    for(double x = centerX - radCeil; x <= centerX + radCeil; x++) {
	        for(double y = centerY - radCeil; y <= centerY + radCeil; y++) {
	            for(double z = centerZ - radCeil; z <= centerZ + radCeil; z++) {
	                double distSquare = Math.pow(2, x - centerX) + Math.pow(2,y - centerY) + Math.pow(2,z - centerZ);
	                if (distSquare > radSquare)
	                    continue;
	                Location currPoint = new Location(center.getWorld(), x, y, z);
	                blockList.add(currPoint.getBlock());
	            }
	        }
	    }
	    return blockList;
	}
	
	public static boolean tntIsAllowedInRegion(Location loc) {
		if (ApiPlus.wg != null) {
			if (!ApiPlus.wg.getGlobalRegionManager().allows(DefaultFlag.TNT,
					loc)) {
				return false;
			} else
				return true;
		} else
			return true;
	}
}