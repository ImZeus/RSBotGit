package com.imzeus.zpastry.tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Bank;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Npc;

import com.imzeus.zpastry.zPastry;
import com.imzeus.zpastry.objects.Task;

public class OpenBankTask extends Task {

	//script
	private zPastry script = null;
	
	public OpenBankTask(ClientContext ctx, zPastry script) {
		super(ctx);
		this.script = script;
	}

	@Override
	public boolean activate() {
		//System.out.println("Distance:"+ctx.npcs.select().id(Bank.BANK_NPC_IDS).nearest().poll().getLocation().distanceTo(ctx.players.local().getLocation()));
		return (ctx.backpack.select().id(script.getFlourPotID()).isEmpty() 
					&& !ctx.bank.open()
						&& ctx.npcs.select().id(Bank.BANK_NPC_IDS).nearest().poll().tile().distanceTo(ctx.players.local().tile()) < 10);
	}

	@Override
	public void execute() {
		if(!ctx.npcs.select().id(Bank.BANK_NPC_IDS).isEmpty() && !ctx.bank.open()) {
			final Npc banker = ctx.npcs.select().id(Bank.BANK_NPC_IDS).nearest().poll();
			if(!banker.inViewport()) {
				script.t("Facing the banker");
				ctx.camera.turnTo(banker);
				if(banker.tile().distanceTo(ctx.players.local().tile()) < 10) {
					if(ctx.players.local().idle()) {
						script.t("Stepping to banker as a failsafe");
						System.out.println("Activated step to banker failsafe!");
						ctx.movement.step(banker);
					}
				}
			} else if(ctx.players.local().idle()){
				script.t("Interacting with banker");
				banker.interact("Bank");
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.bank.open();
					}
				}, Random.nextInt(1200, 1450), 2);
			}
		}
		
	}

}
