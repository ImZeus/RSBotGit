package com.imzeus.zpastry;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.script.Area;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Random;
import org.powerbot.script.Script.Manifest;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.TilePath;
import org.powerbot.script.rt6.GeItem;

import com.imzeus.zpastry.objects.Task;
import com.imzeus.zpastry.tasks.BankTask;
import com.imzeus.zpastry.tasks.MixTask;
import com.imzeus.zpastry.tasks.MovementTask;
import com.imzeus.zpastry.tasks.OpenBankTask;

@SuppressWarnings("all")
@Manifest(name = "zPastry", description = "Mixes pastry dough from pots of flour for 250k+/hr")
public class zPastry extends PollingScript<ClientContext> implements MessageListener, PaintListener {
	
	//script constants
	private final Color background = new Color(64, 64, 64, 128);
	private final Color text = new Color(255, 255, 102, 156);
	//item ids
	private int FLOURPOT_ID = 1933;
	private int PASTRY_ID = 1953;
	//object ids
	private int FOUNTAIN_ID = 47150;
	//walk paths
	private final Tile[] walkpath = new Tile[] {new Tile(3178, 3478, 0), new Tile(3175, 3490, 0),
												new Tile(3168, 3491, 0)};
	//areas
	private final Area FOUNTAIN_AREA = new Area(new Tile(3158, 3497, 0), new Tile(3158, 3488, 0),
										  		new Tile(3172, 3488, 0), new Tile(3172, 3497, 0));
	private final Area BANK_AREA = new Area(new Tile(3175, 3483, 0), new Tile(3175, 3472, 0),
									   		new Tile(3185, 3472, 0), new Tile(3185, 3483, 0));
	//items
	private Item FLOURPOT_ITEM;
	
	//variables
	private ArrayList<Task> tasklist = new ArrayList<Task>();
	
	private int cooking_exp;
	private int total_exp = 0;
	private int pastries_made = 0;
	private int pastries_price = 0;
	private int runs_made = 0;
	
	private String current = "";

	public void start() {
		current = "Starting up.";
		cooking_exp = ctx.skills.experience(ctx.skills.COOKING);
		pastries_price = GeItem.price(PASTRY_ID);
		tasklist.add(new OpenBankTask(ctx, this));
		tasklist.add(new BankTask(ctx, this));
		tasklist.add(new MovementTask(ctx, this));
		tasklist.add(new MixTask(ctx, this));
	}

	@Override
	public void poll() {
		for(Task task : tasklist) {
		    if(task.activate()) {
		    	System.out.println("Runtime:"+ this.getRuntime() + " " + task.getClass().getName());
		        task.execute();
		    }
		}
	}
	
	@Override
	public void repaint(Graphics g) {
		//paint background rectangle
		g.setColor(background);
		g.fillRect(4, 4, 300, 80);
				
		//set font and color text mode
		g.setFont(new Font("Tahoma", Font.PLAIN, 12));
		g.setColor(text);
				
		//calculate seconds, minutes, hours, days runtime
		long runtime = this.getRuntime();
		long hours = runtime / (1000 * 60 * 60);
		runtime -= (hours * (1000 * 60 * 60));
		long minutes = runtime / (1000 * 60);
		runtime -= (minutes * (1000 * 60));
		long seconds = runtime / 1000;
		g.drawString(("Runtime: " + hours + "h " + minutes +  "m "+seconds+"s"), 10, 15);
		
		//calculate exp/hr
		total_exp = cooking_exp - ctx.skills.experience(ctx.skills.COOKING);
		g.drawString(("Experience: " + (int)((((float) ctx.skills.experience(ctx.skills.COOKING))/(float)(seconds + (minutes * 60) + (hours * 60 * 60))*60)*60)) + "xp/hr", 10, 25);
		g.drawString("Experience: " + (int)(total_exp) + "exp", 10, 35);
		
		//calculate gp/hr
		g.drawString(("Gold: " + (int)(((float)(pastries_made*pastries_price)/(float)(seconds +(minutes*60) + (hours * 60 * 60))*60)*60)) + "gp/hr", 10, 45);
		g.drawString("Gold: " + (int)(pastries_made*pastries_price) + "gp", 10, 55);
		g.drawString("Runs: " + runs_made, 10, 65);
		g.drawString("Currently: " + current, 10, 75);
	}

	@Override
	public void messaged(MessageEvent e) {
		if(e.getMessage().equals("You mix the water and flour to make some Pastry dough.")) {
			pastries_made++;
		}
	}
	
	//methods
	public void t(String t) {
		current = t;
	}
	
	public String getCurrent() {
		return current;
	}
	
	public void addRun() {
		runs_made++;
	}
	
	public TilePath getWalkPath() {
		return new TilePath(ctx, walkpath);
	}
	
	public Area getBankArea() {
		return BANK_AREA;
	}
	
	public Area getFountainArea() {
		return FOUNTAIN_AREA;
	}

	public int getFlourPotID() {
		return FLOURPOT_ID;
	}

	public int getPastryID() {
		return PASTRY_ID;
	}
	
	public int getFountainID() {
		return FOUNTAIN_ID;
	}
}
