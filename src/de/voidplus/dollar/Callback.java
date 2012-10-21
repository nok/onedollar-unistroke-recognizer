package de.voidplus.dollar;

import processing.core.PApplet;


public class Callback {

	private Object object;
	private String callback;
	
	protected Callback( Object _object, String _callback ){
		this.object = _object;
		this.callback = _callback;
	}
	
	protected void fire( Candidate _motion ){
		if( this.object!=null ){
			try {
				this.object.getClass().getMethod(
					this.callback, float.class, float.class
				).invoke(
					this.object,
					_motion.getFirstPoint().x,
					_motion.getFirstPoint().y
				);
			} catch ( Exception e ) {
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
