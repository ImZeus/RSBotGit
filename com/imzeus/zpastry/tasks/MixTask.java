package com.imzeus.zpastry.tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;

import com.imzeus.zpastry.zPastry;
import com.imzeus.zpastry.objects.Task;

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
		return !ctx.backpack.select().id(script.getFlourPotID()).isEmpty()
					&& ctx.objects.id(script.getFountainID()).select().within(fountain_debug).select().poll().isValid();
	}

	@Override
	public void execute() {
		final GameObject fountain = ctx.objects.id(script.getFountainID()).select().within(fountain_debug).nearest().poll();
		if(ctx.widgets.get(1370, 20).isVisible()) {
			script.t("Clicking Make button");
			ctx.widgets.get(1370, 20).interact("Make");
			script.t("Mixing dough");
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !ctx.widgets.get(1251, 11).isInViewport();
				}
			}, Random.nextInt(13000, 15000), 1);
		} else if(!ctx.widgets.get(1370, 20).isVisible() && !ctx.widgets.get(1370, 20).isInViewport()) {
			if(fountain.isValid() && !fountain.isInViewport()) {
				script.t("Facing fountain");
				ctx.camera.turnTo(fountain);
				if(fountain.getLocation().distanceTo(ctx.players.local().getLocation()) < 10) {
					script.t("Stepping to fountain as failsafe");
					ctx.movement.stepTowards(fountain);
				}
			}
			if(fountain.isValid() && fountain.isInViewport() && ctx.players.local().isIdle()) {
				script.t("Interacting with pot of flour");
				ctx.backpack.select().id(script.getFlourPotID()).first().poll().interact("Use");
				if(ctx.backpack.getSelectedItem().getId() == script.getFlourPotID()) {
					script.t("Using pot of flour with fountain");
					fountain.interact("Use");
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.widgets.get(1370, 20).isInViewport();
						}
					}, Random.nextInt(1750, 2500), 2);
				}
			}
		}
	}
}
