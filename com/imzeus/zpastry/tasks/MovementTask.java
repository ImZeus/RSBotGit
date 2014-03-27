package com.imzeus.zpastry.tasks;

import org.powerbot.script.methods.MethodContext;

import com.imzeus.zpastry.zPastry;
import com.imzeus.zpastry.objects.Task;

public class MovementTask extends Task {

	//script
	private zPastry script = null;
	
	public MovementTask(MethodContext ctx, zPastry script) {
		super(ctx);
		this.script = script;
	}

	@Override
	public boolean activate() {
		return (script.getFountainArea().contains(ctx.players.local().getLocation()) 
				&& ctx.backpack.select().id(script.getFlourPotID()).isEmpty()) ||
					(script.getBankArea().contains(ctx.players.local().getLocation()) 
						&& !ctx.backpack.select().id(script.getFlourPotID()).isEmpty())
							&& ctx.players.local().isIdle();
	}

	@Override
	public void execute() {
		if(script.getFountainArea().contains(ctx.players.local().getLocation())
				 && ctx.backpack.select().id(script.getFlourPotID()).isEmpty()) {
			script.t("Moving to the bank");
			script.getWalkPath().reverse().randomize(2, 2).traverse();
		} else if(script.getBankArea().contains(ctx.players.local().getLocation())
						&& !ctx.backpack.select().id(script.getFlourPotID()).isEmpty()) {
			script.t("Moving to the fountain");
			script.getWalkPath().randomize(2, 2).traverse();
		}
	}
	
	

}
