package de.voidplus.dollar;

import java.lang.reflect.InvocationTargetException;

import de.voidplus.hdm.pia.Point;
import de.voidplus.hdm.pia.Result;
import de.voidplus.hdm.pia.Vector;
import processing.core.PApplet;

/**
 * Callback class
 * 
 * @author Darius Morawiec
 */
public class Callback {

	private Object object;
	private String callback;
	
	protected Callback(Object object, String callback) {
		this.object = object;
		this.callback = callback;
	}
	
	/**
	 * Execute the binded callback.
	 * 
	 * @param template
	 * @param result
	 */
	protected void fire(String template, Result result) {
		if (this.object != null) {
			
			Point start = result.getFirstPoint();
			Point end = result.getLastPoint();
			Vector centroid = result.getCentroid();
			float percent = (float)result.getPercent();
			
			try {
				this.object.getClass().getMethod(
					this.callback,
					String.class, float.class,
					int.class, int.class,
					int.class, int.class,
					int.class, int.class
				).invoke(
					this.object,
					(String)template, (float)percent,
					(int)start.getPosition().getX(), (int)start.getPosition().getY(),
					(int)centroid.getX(), (int)centroid.getY(),
					(int)end.getPosition().getX(), (int)end.getPosition().getY()
				);
			} catch (IllegalAccessException e) {
				PApplet.println(e.getMessage());
			} catch (IllegalArgumentException e) {
				PApplet.println(e.getMessage());
			} catch (InvocationTargetException e) {
				PApplet.println(e.getMessage());
			} catch (NoSuchMethodException e) {
				PApplet.println(e.getMessage());
			} catch (SecurityException e) {
				PApplet.println(e.getMessage());
			}

		}
	}
	
	protected String getObjectClass(){
		return this.object.getClass().getName();
	}
	
	protected String getCallbackString(){
		return this.callback;
	}
	
}
