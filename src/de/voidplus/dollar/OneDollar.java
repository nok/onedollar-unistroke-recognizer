package de.voidplus.dollar;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import processing.core.PApplet;
import processing.core.PVector;



public class OneDollar {
	
	private PApplet parent;
	private HashMap<Integer,Candidate> candidates;
	private HashMap<String,Gesture> templates;
	private HashMap<String,Callback> callbacks;
	private Recognizer recognizer;
	private Boolean verbose;
	private Integer maxLength, maxTime;	

	
	/**
	 * Constructor of the recognizer.
	 * 
	 * @param	parent		Reference of the processing sketch (this).
	 */
	public OneDollar(PApplet parent){

		System.out.println("# OneDollar-Unistroke-Recognizer - v"+this.getVersion()+" - https://github.com/voidplus/onedollar-unistroke-recognizer");
		
		parent.registerDispose(this);
		parent.registerPre(this);
		
		this.parent 		= parent;
		this.candidates 	= new HashMap<Integer,Candidate>();
		this.callbacks 		= new HashMap<String,Callback>();
		this.recognizer 	= new Recognizer( parent, 64, 250, 45, 2 );

		this.setMinLength(50);
		this.setMaxLength(2500);	
		this.setMaxTime(1000);

		this.setVerbose(false);
	}


	/**
	 * Add new template to recognizer.
	 * 
	 * @param 	name		Name of template.
	 * @param 	points		Points as array of template.
	 * @return
	 */
	public OneDollar addGesture( String name, Integer[] points ){
		if( (points.length%2)==0 && points.length>0 ){
			LinkedList<PVector> vectors = new LinkedList<PVector>();
			for( int i=0, l=points.length; i<l; i+=2 ){
				vectors.add( new PVector( points[i], points[i+1] ) );
			}
			if( this.templates==null ){
				this.templates = new HashMap<String,Gesture>();
			}
			templates.put( name, new Gesture( name, vectors, this.recognizer ) );
		} else {
			System.err.println("Error.");
		}
		return this;
	}

	/**
	 * Add new template to recognizer.
	 * 
	 * @param 	name		Name of template.
	 * @param 	points		Points as array of template.
	 * @return
	 */
	public OneDollar add( String name, Integer[] points ){
		return this.addGesture(name, points);
	}
	

	/**
	 * Remove specified template from recognizer.
	 * 
	 * @param 	name		Name of template.
	 * @return
	 */
	public OneDollar removeGesture( String name ){
		if( templates.containsKey( name ) ){
			templates.remove( name );
		}
		return this;
	}

	/**
	 * Remove specified template from recognizer.
	 * 
	 * @param 	name		Name of template.
	 * @return
	 */
	public OneDollar remove( String name ){
		return this.removeGesture(name);
	}


	/**
	 * Run the recognition and in case of success execute the binded callback.
	 * 
	 * @return
	 */
	private synchronized Result check(){
		Result result = null;
		
		if (this.hasTemplates()) {
			Candidate motion = null;
			
			if (this.hasCandidates()) {
				for( Integer id : this.candidates.keySet() ){
					motion = this.candidates.get( id );
					LinkedList<PVector> positions = this.convert( motion.getLine() );
					
					// binded templates
					if (motion.hasBinds()) {
						
						result = this.recognizer.check( positions, this.templates, motion.getBinds() );
						if (result != null) {
							this.log( id,  result );
							
							motion.fire( result.getName() );
							
							if( this.callbacks.containsKey( result.getName() ) ){
								this.callbacks.get( result.getName() ).fire( motion, result.getName() );
								this.log( id,  result );
							}
							this.candidates.get( id ).clear( positions.getLast() );
							
							return result;
						}
					}
					
					// all templates
					if (this.hasCallbacks()) {
						
						result = this.recognizer.check( positions, this.templates, this.callbacks );
						if (result != null) {
							this.log(id, result);
							
							this.callbacks.get( result.getName() ).fire( motion, result.getName() );
							this.candidates.get( id ).clear( positions.getLast() );
							
							return result;
						}
					}
					
				}
			}

		}
		
		return result;
	}
	public void pre(){
		this.check();
	}
	
	/**
	 * Convert Deque to LinkedList
	 * 
	 * @param line
	 * @return
	 */
	private LinkedList<PVector> convert(Deque<PointInTime> line){
		LinkedList<PVector> positions = new LinkedList<PVector>();
		ListIterator<PointInTime> iterator = (ListIterator<PointInTime>)line.iterator();
		while(iterator.hasNext()){
			PointInTime point = (PointInTime)iterator.next();
			PVector position = point.getPosition();
			positions.add( position );
		}
		return positions;
	}
	
	
	/**
	 * Print the result to the console.
	 * @param id
	 * @param result
	 */
	private void log(Integer id, Result result) {
		if (this.verbose) {
			String object = this.callbacks.get( result.getName() ).getObjectClass();
			String method = this.callbacks.get( result.getName() ).getCallbackString();
			System.out.println(
					"# Candidate: "+id+
					" # Template: "+result.getName()+" ("+result.getScore()+"%)"+
					" # Object: "+object+
					" # Method: "+method
			);
		}
	}

	
	/**
	 * Internal helper method.
	 * 
	 * @return
	 */
	private boolean hasTemplates() {
		if (this.templates != null) {
			if (this.templates.size() > 0) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Internal helper method.
	 * 
	 * @return
	 */
	private boolean hasCandidates() {
		if (this.candidates != null) {
			if (this.candidates.size() > 0) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Internal helper method.
	 * 
	 * @return
	 */
	private boolean hasCallbacks(){
		if (this.callbacks != null) {
			if (this.callbacks.size() > 0) {
				return true;
			}
		}
		return false;		
	}
	
	
	/**
	 * Draw all candidates points as lines.
	 * 
	 * @return
	 */
	public synchronized OneDollar draw() {
		for( Integer id : candidates.keySet() ){
			candidates.get( id ).draw();
		}
		return this;
	}
	
	
	/**
	 * Bind sketch callback to candidate.
	 * 
	 * @param 	template	Name of added template.
	 * @param 	callback	Name of callback in current sketch.
	 * @return
	 */
	public OneDollar bind( String template, String callback ){
		this.bind(template, this.parent, callback);
		return this;
	}
	
	/**
	 * Bind object callback to candidate.
	 * 
	 * @param 	template	Name of added template.
	 * @param 	object		Object, which implemented the callback.
	 * @param 	callback	Name of callback.
	 * @return
	 */
	public OneDollar bind( String template, Object object, String callback ){
		String[] templates = template.split("\\s+");
		for( String _template : templates ){
			if( !this.callbacks.containsKey( _template ) ){
				this.callbacks.put( _template, new Callback( object, callback ) );
			}
		}
		return this;
	}
	

	/**
	 * Bind sketch callback to candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @param 	template	Name of added template.
	 * @param 	callback	Name of callback in current sketch.
	 * @return
	 */
	public OneDollar bind( Integer id, String template, String callback ){
		this.bind(id, template, this.parent, callback);
		return this;
	}


	/**
	 * Bind object callback to candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @param 	template	Name of added template.
	 * @param 	object		Object, which implemented the callback.
	 * @param 	callback	Name of callback.
	 * @return
	 */
	public OneDollar bind( Integer id, String template, Object object, String callback ){
		String[] templates = template.split("\\s+");
		for( String _template : templates ){
			if( candidates.containsKey( id ) && this.templates.containsKey( _template ) ){
				candidates.get( id ).addBind( _template, object, callback );
			}
		}
		return this;
	}
	

	/**
	 * Unbind callback from candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @param 	template	Name of the added template.
	 * @return
	 */
	public OneDollar unbind( Integer id, String template ){
		if( candidates.containsKey( id ) ){
			candidates.get( id ).removeBind( template );
		}		
		return this;
	}


	/**
	 * Start new candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @return
	 */
	public synchronized OneDollar start( Integer id ){
		if( !candidates.containsKey( id ) ){
			candidates.put( id, new Candidate( this.parent, id, this.maxLength, this.maxTime ) );
		}
		return this;
	}


	/**
	 * Add a new point to specified candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @param 	point		New x and y position of the candidate.
	 * @return			
	 */
	public synchronized void update( Integer id, PVector point ){
		if( candidates.containsKey( id ) ){
			candidates.get( id ).addPosition( point );
		} else {
			this.start( id );
		}
	}


	/**
	 * Add a new point to specified candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @param 	x			New x position of the candidate.
	 * @param 	y			New y position of the candidate.
	 */
	public synchronized void update( Integer id, float x, float y ){
		this.update( id, new PVector( x, y ) );
	}


	/**
	 * Stop and delete a candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 */
	public synchronized void end( Integer id ){
		if( candidates.containsKey( id ) ){
			candidates.remove( id );
		}
	}


	/**
	 * Show result messages.
	 * 
	 * @param 	value		Show or hide.
	 * @return
	 */
	public OneDollar setVerbose(Boolean value){
		this.verbose = value;
		return this;
	}


	/**
	 * Set the minimum equality in percent between candidate and template.
	 * 
	 * @param 	percent		Integer between 0 and 100.
	 * @return
	 */
	public OneDollar setMinScore( Integer percent ){
		this.recognizer.setMinScore( (float)percent );
		return this;
	}


	/**
	 * Set the time to live of candidates points.
	 * 
	 * @param 	ms			Time in millisecond.		
	 * @return
	 */
	public OneDollar setMaxTime( Integer ms ){
		if( ms>0 ){
			this.maxTime = ms;
		}
		return this;
	}


	/**
	 * Set the minimum length of a candidate.
	 * 
	 * @param 	length		Length in pixel.
	 * @return
	 */
	public OneDollar setMinLength( Integer length ){
		if( length>0 ){
			this.recognizer.setMinLength( length );
		}
		return this;
	}


	/**
	 * Set the maximum length of a candidate.
	 * 
	 * @param 	length		Length in pixel.
	 * @return
	 */
	public OneDollar setMaxLength( Integer length ){
		if( length>0 ){
			this.maxLength = length;
		}		
		return this;
	}


	/**
	 * Set the rotation angle of the Unistroke Recognition algorithm.
	 * 
	 * @param 	angle		Angle in degree.
	 * @return
	 */
	public OneDollar setRotationAngle( Integer degree ){
		if( degree>0 ){
			this.recognizer.setRotationAngle( degree );
		}
		return this;
	}
	
	
	/**
	 * Set the fragmentation rate of the Unistroke Recognition algorithm.
	 * 
	 * @param 	number
	 * @return
	 */
	public OneDollar setFragmentationRate( Integer number ){
		if( number>0 ){
			this.recognizer.setFragmentationRate( number );			
		}
		return this;
	}	

	
	/**
	 * Print all settings.
	 */
	public String toString(){
		String feedback = "# OneDollar-Unistroke-Recognizer\n"
						+ "#    Gesture Recognition Settings:\n"
						+ "#       Minimum Score:                  "+this.recognizer.getScore()+" %\n"
						+ "#       Minimum Path Length:            "+this.recognizer.getMinLength()+"\n"
						+ "#       Maximum Path Length:            "+this.maxLength+"\n"
						+ "#       Maximum Time Length:            "+this.maxTime+"\n"
						+ "#    Unistroke Algorithm Settings:\n"
						+ "#       Fragmentation/Resampling Rate:  "+this.recognizer.getFragmentationRate()+"\n"
						+ "#       Rotation Angle:                 "+this.recognizer.getRotationAngle()+"\n";
		return feedback;
	}
	
	
	/**
	 * Delete references.
	 */
	public void dispose(){
		this.parent = null;
		this.candidates = null;
		this.templates = null;
		this.recognizer = null;
		this.verbose = null;
	}

	
	/**
	 * Return the version of the library.
	 * 
	 * @return String
	 */
	public static String getVersion() {
		return VERSION;
	}
	public final static String VERSION = "0.2.3";
	
}