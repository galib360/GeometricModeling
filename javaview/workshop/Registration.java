package workshop;

import java.awt.Color;
import java.util.Vector;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import jv.geom.PgBndPolygon;
import jv.geom.PgElementSet;
import jv.geom.PgPolygonSet;
import jv.geom.PgVectorField;
import jv.geom.PuCleanMesh;
import jv.number.PdColor;
import jv.object.PsConfig;
import jv.object.PsDebug;
import jv.object.PsObject;
import jv.project.PgGeometry;
import jv.vecmath.PdVector;
import jv.vecmath.PiVector;
import jv.vecmath.PuMath;
import jv.viewer.PvDisplay;
import jv.project.PvGeometryIf;

import jvx.project.PjWorkshop;


/**
 *  Workshop for surface registration
 */

public class Registration extends PjWorkshop {
	
	/** First surface to be registered. */	
	PgElementSet	m_surfP;	
	/** Second surface to be registered. */
	PgElementSet	m_surfQ;	
	
	// Vectors that contain the indices of randomly selected points from surface P and Q	
	List<Integer> PointsOfSurfaceP = new ArrayList<Integer>();
	List<Integer> PointsOfSurfaceQ = new ArrayList<Integer>();

	/** Constructor */
	public Registration() {
		super("Surface Registration");
		if (getClass() == Registration.class) {
			init();
		}
	}
	
	/** Initialization */
	public void init() {
		super.init();
	}
	
	
	/** Set two Geometries. */
	public void setGeometries(PgElementSet surfP, PgElementSet surfQ) {
		m_surfP = surfP;
		m_surfQ = surfQ;
	}
	
	/*****Random selection of points from mesh P*****/
	public int RandomSelectionP(){
		int numberOfPoints=m_surfP.getNumVertices();
		int[] SelectedP=new int [numberOfPoints];
		for (int i=0; i<numberOfPoints;i++){
			SelectedP[i]=-1;
		}
		int counter=1;
		int numberOfSamples=numberOfPoints/3;
		while (counter<=numberOfSamples){
			int x = ThreadLocalRandom.current().nextInt(0, numberOfPoints + 1);
			if (SelectedP[x]==-1){
				counter++;
				SelectedP[x]=0;
				PointsOfSurfaceP.add(x);
			}
		}
		return PointsOfSurfaceP.size();
	}
	
	/*****Random selection of points from mesh Q*****/
	public int RandomSelectionQ(){
		int numOfPoints=m_surfQ.getNumVertices();
		int[] SelectedQ = new int [numOfPoints];
		for (int j=0; j<numOfPoints; j++){
			SelectedQ[j]=-1;
		}
		int count=1;
		int numOfSamples = numOfPoints/3;
		while (count<=numOfSamples){
			int y = ThreadLocalRandom.current().nextInt(0, numOfPoints + 1);
			if (SelectedQ[y] == -1){
				count++;
				SelectedQ[y]=0;
				PointsOfSurfaceQ.add(y);
			}
		}
		return PointsOfSurfaceQ.size();
	}

}
