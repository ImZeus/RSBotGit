package com.imzeus.zpastry.tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Hud;

import com.imzeus.zpastry.zPastry;
import com.imzeus.zpastry.objects.Task;

public class MixTask extends Task {
	
	private final Area fountain_debug = new Area(new Tile(3164, 3492), new Tile(3164, 3491), 
												 new Tile(3165, 3491), new Tile(3165, 3492));
	
	//script
	private zPastry script = null;

	public MixTask(ClientContext ctx, zPastry script) {
		super(ctx);
		this.script = script;
	}

	@Override
	public boolean activate() {
		return !ctx.backpack.select().id(script.getFlourPotID()).isEmpty()
					&& ctx.objects.id(script.getFountainID()).select().within(fountain_debug).select().poll().valid();
	}

	@Override
	public void execute() {
		final GameObject fountain = ctx.objects.id(script.getFountainID()).select().within(fountain_debug).nearest().poll();
		if(!ctx.hud.opened(Hud.Window.BACKPACK)) {
			ctx.hud.open(Hud.Window.BACKPACK);
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.hud.open(Hud.Window.BACKPACK);
				}
			}, Random.nextInt(250,500), 1);
		}
		//attempted fix for confusion in inventory
		if(fountain.valid() && fountain.inViewport() && ctx.players.local().idle()) {
			script.t("Interacting with pot of flour");
			ctx.backpack.select().id(script.getFlourPotID()).first().poll().interact("Use");
			if(ctx.backpack.itemAt(ctx.backpack.selectedItemIndex()).id() == script.getFlourPotID()) {
				script.t("Using pot of flour with fountain");
				fountain.interact("Use");
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.widgets.component(1370, 20).inViewport();
					}
				}, Random.nextInt(1650, 2200), 2);
			}
		}
		if(ctx.widgets.component(1370, 20).visible()) {
			script.t("Clicking Make button");
			ctx.widgets.component(1370, 20).interact("Make");
			script.t("Mixing dough");
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					System.out.println("MixDoughBool:"+!ctx.widgets.component(1251, 11).inViewport());
					return !ctx.widgets.component(1251, 11).inViewport();
				}
			}, Random.nextInt(16000, 18000), 1);
		} else if(!ctx.widgets.component(1370, 20).visible() && !ctx.widgets.component(1370, 20).inViewport()) {
			if(fountain.valid() && !fountain.inViewport()) {
				script.t("Facing fountain");
				ctx.camera.turnTo(fountain);
				if(fountain.tile().distanceTo(ctx.players.local().tile()) < 10) {
					if(ctx.players.local().idle()) {
						script.t("Stepping to fountain as failsafe");
						System.out.println("Activated failsafe step to fountain!");
						ctx.movement.step(fountain);
					}
				}
			}
			
		}
	}
}
