package com.imzeus.zpastry.tasks;

import org.powerbot.script.methods.MethodContext;

import com.imzeus.zpastry.zPastry;
import com.imzeus.zpastry.objects.Task;

public class BankTask extends Task {
	
	//script object
	private zPastry script = null;

	public BankTask(MethodContext ctx, zPastry script) {
		super(ctx);
		this.script = script;
	}

	@Override
	public boolean activate() {
		return (ctx.bank.isOpen() 
					&& ctx.backpack.select().id(script.getFlourPotID()).isEmpty());
	}

	@Override
	public void execute() {
		//start banking
		if(!ctx.backpack.select().isEmpty()) {
			script.t("Depositing inventory");
			if(!ctx.backpack.select().id(script.getPastryID()).isEmpty()) {
				script.addRun();
			}
			ctx.bank.depositInventory();
		}
		if(ctx.bank.select().id(script.getFlourPotID()).count() == 0) {
			script.getController().stop();
		} else {
			script.t("Withdrawing 14 pots of flour");
			ctx.bank.withdraw(script.getFlourPotID(), 14);
		}
		script.t("Closing bank");
		ctx.bank.close();
	}

}
