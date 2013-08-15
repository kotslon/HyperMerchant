package grokswell.hypermerchant;

//import static java.lang.System.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconAPI;
import regalowl.hyperconomy.HyperObjectAPI;
import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.ShopFactory;
import regalowl.hyperconomy.YamlFile;

import grokswell.hypermerchant.ShopMenu;

public class HyperMerchantPlugin extends JavaPlugin implements Listener {
	HyperEconAPI economyAPI = new HyperEconAPI();
	HyperObjectAPI objectAPI = new HyperObjectAPI();
	HyperAPI hyperAPI = new HyperAPI();
	FileConfiguration itemsyaml;
	YamlFile yaml1;
	//use "items_by_id" for reverse lookup of hyperconomy item names <id:data, name>
	HashMap<String,String> items_by_id = new HashMap<String, String>(); 
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("remotemenu")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use the command "+ChatColor.RED+"/remotemenu");
				return true;
			}
			
			Player player = (Player) sender;
			HyperConomy hc = HyperConomy.hc;
			ShopFactory sf = hc.getShopFactory();
			ArrayList<String> shoplist = sf.listShops();
			String name;
			if (args.length != 1) {
				sender.sendMessage(ChatColor.YELLOW+"You must specify one store name. Example: "+
									ChatColor.RED+"/remotemenu DonutShop");
				sender.sendMessage(ChatColor.YELLOW+"Use "+ChatColor.RED+"/remotestorelist or "+
									ChatColor.RED+"/rslist "+ChatColor.YELLOW+"for valid store names.");
				return true;
			} else {
				name=args[0];
				if (shoplist.contains(name)) {
					ShopStock shopstock = new ShopStock(args, sender, player, args[0],this);
					//shopstock.pages is ArrayList<ArrayList<String>> shopstock.items_count is int
					new ShopMenu(name, 54, this, shopstock.pages, player, shopstock.items_count);
					return true;
				} else {
					sender.sendMessage(ChatColor.YELLOW+"Store name not recognized. Use "+
										ChatColor.RED+"/remotestorelist "+ChatColor.YELLOW+ 
										"or "+ChatColor.RED+"/rslist "+ChatColor.YELLOW+
										"for valid store names. Use exact spelling.");
					return true;
				}
			}
		}
		
		else if (cmd.getName().equalsIgnoreCase("storemenu")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use the command "+ChatColor.RED+"/storemenu");
				return true;
			}
			Player player = (Player) sender;
			String name=hyperAPI.getPlayerShop(player);
			if (name.isEmpty()) {
				sender.sendMessage(ChatColor.YELLOW+"You must be standing inside " +
									"of a store to use the command "+ChatColor.RED+"/storemenu.");
				return true;
			} else {
				ShopStock shopstock = new ShopStock(args, sender, player, name, this);
				//shopstock.pages is ArrayList<ArrayList<String>> shopstock.items_count is int
				new ShopMenu(name, 54, this, shopstock.pages, player, shopstock.items_count);
				return true;
			}
		}
		
		else if (cmd.getName().equalsIgnoreCase("remotestorelist")) {
			sender.sendMessage(ChatColor.YELLOW+"Valid shop names to use with command /remotemenu:");
			sender.sendMessage(hyperAPI.listShops());
			return true;
		}
		
		else {
			return true;
		}
	}
	
	


	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		HyperConomy hc2 = HyperConomy.hc;
		yaml1 = hc2.getYaml();
		itemsyaml = yaml1.getItems();
		Iterator<String> it = itemsyaml.getKeys(false).iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			int id = itemsyaml.getInt(name + ".information.id");
			int data = itemsyaml.getInt(name + ".information.data");
			String newkey = Integer.toString(id) +":"+ Integer.toString(data);
			items_by_id.put(newkey,name);
		}

	}

}