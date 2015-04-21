package de.voidplus.dollar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import de.voidplus.hdm.pia.Candidate;
import de.voidplus.hdm.pia.Pia;
import de.voidplus.hdm.pia.Point;
import de.voidplus.hdm.pia.Result;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * OneDollar class
 * 
 * @version 1.0.3
 * @author Darius Morawiec
 */
public class OneDollar {

	private final static String NAME = "OneDollar-Unistroke-Recognizer";
	private final static String VERSION = "1.0.3";
	private final static String REPOSITORY = "https://github.com/nok/onedollar-unistroke-recognizer";
	
	private PApplet parent;
	private HashMap<String,Callback> globalCallbacks;
	private HashMap<Integer,HashMap<String,Callback>> localCallbacks;
	
	private Pia pia;
	private boolean autoCheck;
	private boolean verbose;
	
	/**
	 * Constructor of the recognizer.
	 * 
	 * @param	parent		Reference of the processing sketch (this).
	 */
	public OneDollar(PApplet parent){
		PApplet.println("# "+OneDollar.NAME+" v"+OneDollar.VERSION+" - "+OneDollar.REPOSITORY);
		
		this.enableAutoCheck();
		parent.registerMethod("pre", this);
		parent.registerMethod("post", this);
		
		this.parent 			= parent;
		this.globalCallbacks 	= new HashMap<String,Callback>();
		this.localCallbacks		= new HashMap<Integer,HashMap<String,Callback>>();
		
		this.pia = new Pia(2);
		this.pia
			.disableAutoClean()
			.setMinSimilarity(80).enableMinSimilarity();
		
		// Algorithm settings
		this.pia
			.setAlgorithmFragmentationRate(64)
			.setAlgorithmSize(250)
			.setAlgorithmAngle(45)
			.setAlgorithmStep(2);

		// Data pre-processing
		this.pia
			.setMinDistance(50).enableMinDistance()
			.setMaxTime(1000).enableMaxTime()
			.setMinSpeed(2).disableMinSpeed();

		this.setVerbose(false);
	}

	// ------------------------------------------------------------------------------
	
	/**
	 * Add new template to recognizer.
	 * 
	 * @param 	name		Name of template.
	 * @param 	points		Points as array of template.
	 * @return
	 */
	public OneDollar learn(String name, int[] points) {
		this.pia.learn(name, points);
		return this;
	}
	
	/**
	 * Add new template to recognizer.
	 * 
	 * @param 	name		Name of template.
	 * @param 	points		Points as array of template.
	 * @return
	 */
	public OneDollar addGesture(String name, int[] points) {
		return this.learn(name, points);
	}

	/**
	 * Add new template to recognizer.
	 * 
	 * @param 	name		Name of template.
	 * @param 	points		Points as array of template.
	 * @return
	 */
	public OneDollar add(String name, int[] points) {
		return this.learn(name, points);
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * Remove specified template from recognizer.
	 * 
	 * @param 	name		Name of template.
	 * @return
	 */
	public OneDollar forget(String name) {
		this.pia.forget(name);
		return this;
	}
	
	/**
	 * Remove specified template from recognizer.
	 * 
	 * @param 	name		Name of template.
	 * @return
	 */
	public OneDollar removeGesture(String name) {
		return this.forget(name);
	}

	/**
	 * Remove specified template from recognizer.
	 * 
	 * @param 	name		Name of template.
	 * @return
	 */
	public OneDollar remove(String name) {
		return this.forget(name);
	}
	
	/**
	 * Remove all learned templates.
	 * 
	 * @return
	 */
	public OneDollar forgetAll() {
		this.pia.forgetAll();
		return this;
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * Run the recognition and in case of success execute the binded callback.
	 * 
	 * @return
	 */
	public void check(){
		if(this.hasTemplates()){
			if(this.hasCandidates()){
				this.checkGlobalCallbacks();
				this.checkLocalCallbacks();			
			}
		}
	}
	
	/**
	 * Run the recognition with specific candidate and in case of success execute the binded callback.
	 * 
	 * @param candidate
	 */
	public void check(int[] candidate){
		if(this.hasTemplates() && this.hasGlobalCallbacks()){
			ArrayList<Result> results = this.pia.check(candidate);
			if (results.size() > 0) {
				Result result = results.get(0);
				String template = result.getName();
				if (this.globalCallbacks.containsKey(template)) {
					this.log(result.getTrackingId(), result);
					this.globalCallbacks.get(template).fire(template, result);
				}				
			}
		}
	}
	
	/**
	 * Run gesture-recognition and check global callbacks.
	 */
	private void checkGlobalCallbacks() {
		if (this.hasGlobalCallbacks()) {
			ArrayList<Result> results = this.pia.check();
			if (results.size() > 0) {
				Result result = results.get(0);
				String template = result.getName();
				if (this.globalCallbacks.containsKey(template)) {
					this.log(result.getTrackingId(), result);
					this.globalCallbacks.get(template).fire(template, result);
				}
			}
		}
	}
	
	/**
	 * Run gesture-recognition and check local callbacks.
	 */
	private void checkLocalCallbacks() {
		if (this.hasLocalCallbacks()) {
			
			Iterator<Integer> candidatesIterator = this.localCallbacks.keySet().iterator();
			while (candidatesIterator.hasNext()) {
				Integer candidateId = candidatesIterator.next();
				HashMap<String, Callback> callbacks = this.localCallbacks.get(candidateId);
				
				// concatenation
				Iterator<String> templatesIterator = callbacks.keySet().iterator();
				String templates = "";
				while (templatesIterator.hasNext()) {
					String template = templatesIterator.next(); 
					templates += " "+template;
				}
				templates = templates.toLowerCase().trim();
				
				// comparison
				ArrayList<Result> results = this.pia.check(candidateId, templates);
				
				// callback
				if (results.size() > 0) {
					Result result = results.get(0);
					String template = result.getName();
					if (this.localCallbacks.get(candidateId).containsKey(template)) {
						this.log(result.getTrackingId(), result, false);
						this.localCallbacks.get(candidateId).get(template).fire(template, result);
					}
				}				
			}
			
		}
	}
	
	/**
	 * Manually cleanup of all inactive candidates.
	 */
	public void clean(){
		this.pia.clean();
	}
	
	/**
	 * Enable auto clean of inactive candidates.
	 * 
	 * @return
	 */
	public OneDollar enableAutoClean() {
		this.pia.enableAutoClean();
		return this;
	}
	
	/**
	 * Enable auto recognition of gestures.
	 * 
	 * @return
	 */
	public OneDollar enableAutoCheck() {
		this.autoCheck = true;
		return this;
	}
	
	/**
	 * Disable auto recognition of gestures.
	 * 
	 * @return
	 */
	public OneDollar disableAutoCheck() {
		this.autoCheck = false;
		return this;
	}	

	/**
	 * Disable auto clean of inactive candidates.
	 * 
	 * @return
	 */
	public OneDollar disableAutoClean() {
		this.pia.disableAutoClean();
		return this;
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * The pre() method will be execute before the draw() methode.
	 */
	public void pre() {
		if (this.autoCheck) {
			this.check();
		}
	}
	
	/**
	 * The post() method will be execute after the draw() method.
	 */
	public void post(){
		this.clean();
	}

	// ------------------------------------------------------------------------------
	
	/**
	 * Print the result to the console.
	 * 
	 * @param id
	 * @param result
	 */
	private void log(Integer id, Result result, boolean global) {
		if (this.verbose) {
			String object; String method;
			if(global){
				object = this.globalCallbacks.get(result.getName()).getObjectClass();
				method = this.globalCallbacks.get(result.getName()).getCallbackString();
			} else {
				object = this.localCallbacks.get(id).get(result.getName()).getObjectClass();
				method = this.localCallbacks.get(id).get(result.getName()).getCallbackString();				
			}
			PApplet.println(
					"# Candidate: "+id+
					" # Template: "+result.getName()+
					" ("+String.format("%2.2f %%", result.getPercent())+")"+
					" # Object: "+object+
					" # Method: "+method
			);
		}
	}
	
	private void log(Integer id, Result result) {
		this.log(id, result, true);
	}

	// ------------------------------------------------------------------------------
		
	/**
	 * Internal helper method.
	 * 
	 * @return
	 */
	private boolean hasTemplates() {
		return this.pia.hasTemplates();
	}
	
	/**
	 * Internal helper method.
	 * 
	 * @return
	 */
	private boolean hasCandidates() {
		return this.pia.hasCandidates();
	}
	
	/**
	 * Any global callbacks available?
	 * Internal helper method.
	 * 
	 * @return
	 */
	private boolean hasGlobalCallbacks() {
		return (this.globalCallbacks.size() > 0);
	}
	
	/**
	 * Any local callbacks available?
	 * Internal helper method.
	 * 
	 * @return
	 */
	private boolean hasLocalCallbacks() {
		return (this.localCallbacks.size() > 0);
	}
	
	// ------------------------------------------------------------------------------
	// GLOBAL CALLBACKS

	/**
	 * Bind object callback to candidate.
	 * 
	 * @param 	templates	Name of added templates.
	 * @param 	object		Object, which implemented the callback.
	 * @param 	callback	Name of callback.
	 * @return
	 */
	public OneDollar on(String templates, Object object, String callback) {
		for (String template : templates.split("\\s+")) {
			if (this.pia.hasTemplate(template)) {
				if (!this.globalCallbacks.containsKey(template)) {
					this.globalCallbacks.put(template, new Callback(object, callback));
				}
			}
		}
		return this;
	}
	
	/**
	 * Bind object callback to candidate.
	 * 
	 * @param 	templates	Name of added templates.
	 * @param 	object		Object, which implemented the callback.
	 * @param 	callback	Name of callback.
	 * @return
	 */
	public OneDollar bind(String templates, Object object, String callback) {
		return this.on(templates, object, callback);
	}
	
	/**
	 * Bind sketch callback to candidate.
	 * 
	 * @param 	templates	Name of added templates.
	 * @param 	callback	Name of callback in current sketch.
	 * @return
	 */
	public OneDollar on(String templates, String callback) {
		return this.on(templates, this.parent, callback);
	}

	/**
	 * Bind sketch callback to candidate.
	 * 
	 * @param 	templates	Name of added templates.
	 * @param 	callback	Name of callback in current sketch.
	 * @return
	 */
	public OneDollar bind(String templates, String callback) {
		return this.on(templates, callback);
	}

	/**
	 * Unbind callback from template.
	 * 
	 * @param template
	 * @return
	 */
	public OneDollar off(String template){
		if(this.globalCallbacks.containsKey(template)){
			this.globalCallbacks.remove(template);
		}
		return this;
	}

	/**
	 * Unbind callback from template.
	 * 
	 * @param template
	 * @return
	 */
	public OneDollar unbind(String template){
		return this.off(template);
	}

	// ------------------------------------------------------------------------------
	// LOCAL CALLBACKS

	/**
	 * Bind local callback on candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @param 	templates	Name of added templates.
	 * @param 	object		Object, which implemented the callback.
	 * @param 	callback	Name of callback.
	 * @return
	 */
	public OneDollar on(Integer id, String templates, Object object, String callback) {
		for (String template : templates.split("\\s+")) {
			if (this.pia.hasTemplate(template)) {
				if(!this.localCallbacks.containsKey(id)){
					this.localCallbacks.put(id, new HashMap<String, Callback>());
				}
				this.localCallbacks.get(id).put(template, new Callback(object, callback));
			}
		}
		return this;
	}
	
	/**
	 * Bind local callback on candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @param 	templates	Name of added templates.
	 * @param 	object		Object, which implemented the callback.
	 * @param 	callback	Name of callback.
	 * @return
	 */
	public OneDollar bind(Integer id, String templates, Object object, String callback) {
		return this.on(id, templates, object, callback);
	}

	/**
	 * Bind local callback on candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @param 	templates	Name of added templates.
	 * @param 	callback	Name of callback in current sketch.
	 * @return
	 */
	public OneDollar on(Integer id, String templates, String callback) {
		return this.on(id, templates, this.parent, callback);
	}

	/**
	 * Bind local callback on candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @param 	templates	Name of added templates.
	 * @param 	callback	Name of callback in current sketch.
	 * @return
	 */
	public OneDollar bind(Integer id, String templates, String callback) {
		return this.on(id, templates, callback);
	}
	
	/**
	 * Unbind local callback of specific candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @param 	templates	Name of the added templates.
	 * @return
	 */
	public OneDollar off(Integer id, String templates) {
		for (String template : templates.split("\\s+")) {
			if (this.pia.hasTemplate(template)) {
				if (this.localCallbacks.containsKey(id)) {
					if (this.localCallbacks.get(id).containsKey(template)) {
						this.localCallbacks.get(id).remove(template);
					}
				}
			}
		}
		return this;
	}
	
	/**
	 * Unbind local callback of specific candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @param 	templates	Name of the added templates.
	 * @return
	 */
	public OneDollar unbind(Integer id, String templates) {
		return this.off(id, templates);
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * Draw all candidates points as lines.
	 * 
	 * @return
	 */
	public synchronized OneDollar draw() {
		if(this.pia.hasCandidates()){
			HashMap<Integer, Candidate> candidates = this.pia.getCandidates();
			for(Integer id: candidates.keySet()){
				LinkedList<Point> path = candidates.get(id).getConvertedPath();
				
				// lines
				this.parent.noFill();
				this.parent.stroke(0, 50);
				this.parent.beginShape();
					for (Point point : path) {
						this.parent.vertex((int) point.getPosition().getX(), (int) point.getPosition().getY());
					}
				this.parent.endShape();
				
				// circles
				this.parent.fill(0);
				this.parent.noStroke();
				for (Point point : path) {
					this.parent.ellipse((int) point.getPosition().getX(), (int) point.getPosition().getY(), 2f, 2f);
				}
			}
		}
		return this;
	}
	
	// ------------------------------------------------------------------------------
	
	public OneDollar track(int id, PVector position){
		this.pia.track(id, new int[] { (int)position.x, (int)position.y });
		return this;
	}
	
	public OneDollar track(int id, int[] positions){
		this.pia.track(id, positions);
		return this;
	}
	
	public OneDollar track(int id, int x, int y){
		this.pia.track(id, new int[] { x, y });
		return this;
	}
	
	public OneDollar track(int id, float x, float y){
		this.pia.track(id, new int[] { (int)x, (int)y });
		return this;
	}

	public OneDollar track(int x, int y){
		this.pia.track(new int[] { x, y });
		return this;
	}
	
	public OneDollar track(float x, float y){
		this.pia.track(new int[] { (int)x, (int)y });
		return this;
	}
	
	public OneDollar track(PVector position){
		this.pia.track(new int[] { (int) position.x, (int) position.y });
		return this;
	}

	public OneDollar track(int[] positions){
		this.pia.track(positions);
		return this;
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * Start new candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @deprecated 			That method is deprecated.
	 * @return
	 */
	public OneDollar start(Integer id) {
		PApplet.println("# "+OneDollar.NAME+": start() is deprecated, please use track() instead of it.");
		return this;
	}

	/**
	 * Add a new point to specified candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @param 	position	New x and y position of the candidate.
	 * @deprecated 			Please use the {@link #track(int, PVector) track} method.
	 * @return			
	 */
	public OneDollar update(Integer id, PVector position) {
		PApplet.println("# " + OneDollar.NAME + ": update() is deprecated, please use track() instead of it.");
		return this.track(id, position);
	}

	/**
	 * Add a new point to specified candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @param 	x			New x position of the candidate.
	 * @param 	y			New y position of the candidate.
	 * @deprecated 			Please use the {@link #track(int, PVector) track} method.
	 */
	public synchronized void update(Integer id, float x, float y) {
		this.update(id, new PVector(x, y));
	}

	/**
	 * Stop and delete a candidate.
	 * 
	 * @param 	id			Unique id of candidate.
	 * @deprecated 			That method is deprecated.
	 */
	public OneDollar end(Integer id) {
		PApplet.println("# "+OneDollar.NAME+": end() is deprecated, please use track() instead of it.");
		return this;
	}

	// ------------------------------------------------------------------------------

	/**
	 * Set the minimum equality in percent between candidate and template.
	 * 
	 * @param 	percent		Integer between 0 and 100.
	 * @return
	 */
	public OneDollar setMinSimilarity(int percent) {
		this.pia.setMinSimilarity(percent);
		return this;
	}
	
	/**
	 * Enable minimum similarity check.
	 * 
	 * @return
	 */
	public OneDollar enableMinSimilarity() {
		this.pia.enableMinSimilarity();
		return this;
	}

	/**
	 * Disable minimum similarity check.
	 * 
	 * @return
	 */
	public OneDollar disableMinSimilarity() {
		this.pia.disableMinSimilarity();
		return this;
	}
	
	/**
	 * Set the minimum equality in percent between candidate and template.
	 * 
	 * @param 	percent		Integer between 0 and 100.
	 * @deprecated 			Please use the {@link #setMinSimilarity(int) setMinSimilarity} method, it's more semantic.
	 * @return
	 */
	public OneDollar setMinScore( Integer percent ){
		PApplet.println("# "+OneDollar.NAME+": setMinScore(int percent) is deprecated, please use setMinSimilarity(int percent); instead of it.");
		this.pia.setMinSimilarity(percent);
		return this;
	}

	/**
	 * Set the time to live of candidates points.
	 * 
	 * @param 	ms			Time in millisecond.		
	 * @return
	 */
	public OneDollar setMaxTime(int ms) {
		this.pia.setMaxTime(ms);
		return this;
	}

	/**
	 * Enable maximum duration check.
	 * 
	 * @return
	 */
	public OneDollar enableMaxTime() {
		this.pia.enableMaxTime();
		return this;
	}

	/**
	 * Disable maximum duration check.
	 * 
	 * @return
	 */
	public OneDollar disableMaxTime() {
		this.pia.disableMaxTime();
		return this;
	}
	
	/**
	 * Set the minimum length of a candidate.
	 * 
	 * @param 	length		Length in pixel.
	 * @deprecated 			Please use the {@link #setMinDistance(int) setMinDistance} method.
	 * @return
	 */
	public OneDollar setMinLength(Integer length) {
		this.setMinDistance(length);
		return this;
	}

	/**
	 * Set the minimum distance of a candidate.
	 * 
	 * @param length
	 * @return
	 */
	public OneDollar setMinDistance(int length) {
		this.pia.setMinDistance(length);
		return this;
	}

	/**
	 * Enable minimum distance check.
	 * 
	 * @return
	 */
	public OneDollar enableMinDistance() {
		this.pia.enableMinDistance();
		return this;
	}

	/**
	 * Disable minimum distance check.
	 * 
	 * @return
	 */
	public OneDollar disableMinDistance() {
		this.pia.disableMinSimilarity();
		return this;
	}
	
	/**
	 * Set the maximum length of a candidate.
	 * 
	 * @param 	length		Length in pixel.
	 * @deprecated There is no use for that preprocessing, it's better to use the {@link #setMaxTime(int) setMaxTime} method.
	 * @return
	 */
	public OneDollar setMaxLength(Integer length) {
		PApplet.println("# "+OneDollar.NAME+": setMaxLength() is deprecated, please remove it.");
		return this;
	}

	/**
	 * Set the minimum speed.
	 * 
	 * @param pxms
	 * @return
	 */
	public OneDollar setMinSpeed(float pxms) {
		this.pia.setMinSpeed(pxms);
		return this;
	}
	
	/**
	 * Get the minimum speed.
	 * 
	 * @param pxms
	 * @return
	 */
	public float getMinSpeed() {
		return this.pia.getMinSpeed();
	}

	/**
	 * Enable minimum speed.
	 * 
	 * @return
	 */
	public OneDollar enableMinSpeed() {
		this.pia.enableMinSpeed();
		return this;
	}

	/**
	 * Disable minimum speed.
	 * 
	 * @return
	 */
	public OneDollar disableMinSpeed() {
		this.pia.disableMinSpeed();
		return this;
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * Set the rotation angle of the 1$ Unistroke Recognition algorithm.
	 * 
	 * @param 	degrees		Angle in degree.
	 * @return
	 */
	public OneDollar setRotationAngle(Integer degrees) {
		this.pia.setAlgorithmAngle(degrees);
		return this;
	}
	
	/**
	 * Set the rotation step of the 1$ Unistroke Recognition algorithm.
	 * 
	 * @param 	degrees		Angle in degree.
	 * @return
	 */
	public OneDollar setRotationStep(Integer degrees) {
		this.pia.setAlgorithmStep(degrees);
		return this;
	}
	
	/**
	 * Set the size of bounding box of the 1$ Unistroke Recognition algorithm.
	 * 
	 * @param 	px
	 * @return
	 */
	public OneDollar setBoundingBox(Integer px) {
		this.pia.setAlgorithmSize(px);
		return this;
	}
	
	/**
	 * Set the fragmentation rate of the 1$ Unistroke Recognition algorithm.
	 * 
	 * @param 	parts		Number of resampling.
	 * @return
	 */
	public OneDollar setFragmentationRate(Integer parts) {
		this.pia.setAlgorithmFragmentationRate(parts);
		return this;
	}
	
	// ------------------------------------------------------------------------------
	
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
	 * Print all settings.
	 */
	public String toString(){
		String feedback = "# "+OneDollar.NAME+"\n"
						+ "#    Gesture Recognition Settings:\n"
						+ "#       ["+((this.pia.getMinSimilarityFlag())?"x":" ")+"] Minimum percent of similarity:    "+this.pia.getMinSimilarity()+" %\n"
						+ "#    Data pre-processing:\n"
						+ "#       ["+((this.pia.getMinDistanceFlag())?"x":" ")+"] Minimum distance (px) of move:    "+this.pia.getMinDistance()+"\n"
						+ "#       ["+((this.pia.getMaxTimeFlag())?"x":"")+"] Maximum duration (ms) of move:    "+this.pia.getMaxTime()+"\n"
						+ "#       ["+((this.pia.getMinSpeedFlag())?"x":" ")+"] Minimum speed of move:            "+this.pia.getMinSpeed()+"\n"
						+ "#    Unistroke Algorithm Settings:\n"
						+ "#       Fragmentation/Resampling rate:        "+this.pia.getAlgorithmFragmentationRate()+"\n"
						+ "#       Size of bounding box:                 "+this.pia.getAlgorithmSize()+"\n"
						+ "#       Rotation angle step:                  "+this.pia.getAlgorithmStep()+"\n"
						+ "#       Rotation angle:                       "+this.pia.getAlgorithmAngle()+"\n";
		return feedback;
	}
	
}