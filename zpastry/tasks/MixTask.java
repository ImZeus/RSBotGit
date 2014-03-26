package zpastry.tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Tile;

import zpastry.zPastry;
import zpastry.objects.Task;

@SuppressWarnings("deprecation")
public class MixTask extends Task {
	
	private final Area fountain_debug = new Area(new Tile(3164, 3492), new Tile(3164, 3491), 
												 new Tile(3165, 3491), new Tile(3165, 3492));
	
	//script
	private zPastry script = null;

	public MixTask(MethodContext ctx, zPastry script) {
		super(ctx);
		this.script = script;
	}

	@Override
	public boolean activate() {
		if((script.getFountainArea().contains(ctx.players.local().getLocation()) || ctx.players.local().getLocation().distanceTo(script.getFountainArea().getClosestTo(ctx.players.local().getLocation())) < 6) && !ctx.backpack.select().id(script.getFlourPotID()).isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public void execute() {
		if(script.getFountainArea().contains(ctx.players.local().getLocation()) || ctx.players.local().getLocation().distanceTo(script.getFountainArea().getClosestTo(ctx.players.local().getLocation())) < 6) {
			if(!ctx.objects.id(script.getFountainID()).select().isEmpty()) {
				final GameObject fountain = ctx.objects.id(script.getFountainID()).select().within(fountain_debug).nearest().poll();
				if(!fountain.isOnScreen() || !fountain.isInViewport()) {
					script.t("Facing fountain");
					ctx.movement.stepTowards(fountain);
					ctx.camera.turnTo(fountain);
				}
				if(fountain.isOnScreen() && fountain.isValid()) {
					if(!ctx.widgets.get(1370, 12).isVisible()) {
						script.t("Clicking pot of flour");
						for(Item i : ctx.backpack.getAllItems()) {
							if(i.getId() == script.getFlourPotID()) {
								i.interact("Use");
								break;
							}
						}
						script.t("Using flour on fountain");
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return fountain.interact("Use", "Fountain") || ctx.widgets.get(1370, 12).isVisible();
							}
						}, 2500, 2);
					} 
					if(ctx.widgets.get(1370, 12).isVisible()) {
						script.t("Clicking Make button");
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.widgets.get(1370, 12).interact("Make");
							}
						}, 2000, 2);
						script.t("Sleeping while mixing dough");
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.backpack.select().count() == 28 || ctx.backpack.select().id(script.getFlourPotID()).isEmpty();
							}
						}, 11000, 1);
					}
				}
			}
		} else {
			System.out.println("[zPastry]: Couldn't find fountain!");
		}
	}

}
