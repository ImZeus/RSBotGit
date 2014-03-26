package zpastry.tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Condition;

import zpastry.zPastry;
import zpastry.objects.Task;

public class BankTask extends Task {
	
	//script object
	private zPastry script = null;

	public BankTask(MethodContext ctx, zPastry script) {
		super(ctx);
		this.script = script;
	}

	@Override
	public boolean activate() {
		if(ctx.bank.isOpen() && ctx.backpack.select().id(script.getFlourPotID()).isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public void execute() {
		//start banking
		if(!ctx.backpack.select().isEmpty()) {
			script.t("Depositing inventory");
			if(!ctx.backpack.select().id(script.getPastryID()).isEmpty()) {
				script.addRun();
			}
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.bank.depositInventory();
				}
			}, 1000, 2);
		}
		Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				script.t("Withdrawing 14 pots of flour");
				return ctx.bank.withdraw(script.getFlourPotID(), 14);
			}
		}, 1000, 2);
		Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				script.t("Closing bank");
				return ctx.bank.close();
			}
		}, 1000, 2);
	}

}
