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
		//added in failsafe to keep it from sticking in between 2 areas
		return ((script.getFountainArea().contains(ctx.players.local().getLocation()) 
				&& ctx.backpack.select().id(script.getFlourPotID()).isEmpty()) ||
					(script.getBankArea().contains(ctx.players.local().getLocation()) 
						&& !ctx.backpack.select().id(script.getFlourPotID()).isEmpty())
							|| (!script.getFountainArea().contains(ctx.players.local())
									&& !script.getBankArea().contains(ctx.players.local())))
										&& ctx.players.local().isIdle();
	}

	@Override
	public void execute() {
		if(ctx.backpack.select().id(script.getFlourPotID()).isEmpty()) {
			script.t("Moving to the bank");
			System.out.println("Activated move to bank!");
			script.getWalkPath().reverse().randomize(2, 2).traverse();
		} else if(!ctx.backpack.select().id(script.getFlourPotID()).isEmpty()) {
			script.t("Moving to the fountain");
			System.out.println("Activated move to fountain!");
			script.getWalkPath().randomize(2, 2).traverse();
		}
	}
	
	

}
