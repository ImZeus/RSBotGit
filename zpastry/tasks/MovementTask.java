package zpastry.tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Condition;

import zpastry.zPastry;
import zpastry.objects.Task;

public class MovementTask extends Task {

	//script
	private zPastry script = null;
	
	public MovementTask(MethodContext ctx, zPastry script) {
		super(ctx);
		this.script = script;
	}

	@Override
	public boolean activate() {
		if(script.getFountainArea().contains(ctx.players.local().getLocation()) && ctx.backpack.select().id(script.getFlourPotID()).isEmpty()) {
			return true;
		} else if(script.getBankArea().contains(ctx.players.local().getLocation()) && !ctx.backpack.select().id(script.getFlourPotID()).isEmpty()) {
			return true;
		} else {
		}
		return false;
	}

	@Override
	public void execute() {
		if(script.getFountainArea().contains(ctx.players.local().getLocation())) {
			script.t("Moving to the bank");
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return script.getWalkPath().reverse().randomize(2, 2).traverse();
				}
			}, 5500, 1);
		} else if(script.getBankArea().contains(ctx.players.local().getLocation())) {
			script.t("Moving to the fountain");
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return script.getWalkPath().randomize(2, 2).traverse();
				}
			}, 5500, 1);
		}
	}
	
	

}
