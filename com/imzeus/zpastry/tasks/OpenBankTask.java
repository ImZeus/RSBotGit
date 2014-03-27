package com.imzeus.zpastry.tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.methods.Bank;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Npc;

import com.imzeus.zpastry.zPastry;
import com.imzeus.zpastry.objects.Task;

public class OpenBankTask extends Task {

	//script
	private zPastry script = null;
	
	public OpenBankTask(MethodContext ctx, zPastry script) {
		super(ctx);
		this.script = script;
	}

	@Override
	public boolean activate() {
		return (ctx.backpack.select().id(script.getFlourPotID()).isEmpty() 
					&& !ctx.bank.isOpen()
						&& ctx.npcs.select().id(Bank.BANK_NPC_IDS).nearest().poll().getLocation().distanceTo(ctx.players.local().getLocation()) < 10);
	}

	@Override
	public void execute() {
		if(!ctx.npcs.select().id(Bank.BANK_NPC_IDS).isEmpty() && !ctx.bank.isOpen()) {
			final Npc banker = ctx.npcs.select().id(Bank.BANK_NPC_IDS).nearest().poll();
			if(!banker.isInViewport()) {
				script.t("Facing the banker");
				ctx.camera.turnTo(banker);
			} else if(ctx.players.local().isIdle()){
				script.t("Interacting with banker");
				banker.interact("Bank");
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.bank.isOpen();
					}
				}, Random.nextInt(750, 1250), 2);
			}
		}
		
	}

}
