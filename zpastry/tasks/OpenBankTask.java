package zpastry.tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.methods.Bank;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Npc;

import zpastry.zPastry;
import zpastry.objects.Task;

@SuppressWarnings("deprecation")
public class OpenBankTask extends Task {

	//script
	private zPastry script = null;
	
	public OpenBankTask(MethodContext ctx, zPastry script) {
		super(ctx);
		this.script = script;
	}

	@Override
	public boolean activate() {
		if(ctx.backpack.select().id(script.getFlourPotID()).isEmpty() && (script.getBankArea().contains(ctx.players.local().getLocation()) || ctx.players.local().getLocation().distanceTo(script.getBankArea().getClosestTo(ctx.players.local().getLocation())) < 6) && !ctx.bank.isOpen()) {
			return true;
		}
		return false;
	}

	@Override
	public void execute() {
		if(!ctx.npcs.select().id(Bank.BANK_NPC_IDS).isEmpty() && !ctx.bank.isOpen()) {
			final Npc banker = ctx.npcs.select().id(Bank.BANK_NPC_IDS).nearest().poll();
			if(!banker.isOnScreen()) {
				script.t("Facing the banker");
				ctx.movement.stepTowards(banker);
				ctx.camera.turnTo(banker);
			} else {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						script.t("Interacting with banker");
						return banker.interact("Bank") || ctx.bank.isOpen();
					}
				}, 2000, 2);
			}
		}
		
	}

}
