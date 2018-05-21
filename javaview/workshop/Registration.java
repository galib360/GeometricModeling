package workshop;

import java.awt.Color;
import java.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.sun.javafx.geom.PickRay;

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
import sun.java2d.cmm.ProfileDataVerifier;


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
	List<Integer> PmatchQidx = new ArrayList<Integer>();

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

	/*****Closest Distance*****/
	public int closestVertex(){
		int RandSelP = RandomSelectionP();
		int RandSelQ = RandomSelectionQ();
		double[] distpts = new double[RandSelQ];

		PdVector [] vertices_P = m_surfP.getVertices();
		PdVector [] vertices_Q = m_surfQ.getVertices();

		for (int i = 0; i<RandSelP; i++){
			Arrays.fill(distpts,0.0);
			for(int j = 0; j<RandSelQ; j++){
				distpts[j] = PdVector.dist(vertices_P[PointsOfSurfaceP.get(i)], vertices_Q[PointsOfSurfaceQ.get(j)]);
			}
			PmatchQidx.add(min_index(distpts)); //contains corresponding indices of Q which have min. distance with P
		}
		return PmatchQidx.size();
	}

	public int min_index(double[] a){
		double min = a[0];
		int min_idx = 0;
		for(int i=0;i<a.length;i++){
			if(a[i] < min){
				min = a[i];
				min_idx = i;
			}
		}
		return min_idx;
	}

	/*****Calculate Centroid for a given PdVector([x,y,z])*****/
	public PdVector calcCentroid(PdVector[] a){
		PdVector ctr = new PdVector();
		double x = 0.0;
		double y = 0.0;
		double z = 0.0;

		for(int i=0;i<a.length;i++){
			x += a[i].getEntry(0);
			y += a[i].getEntry(1);
			z += a[i].getEntry(2);
		}
		x /= a.length;
		y /= a.length;
		z /= a.length;

		ctr.addEntry(x);
		ctr.addEntry(y);
		ctr.addEntry(z);
	}

	/*****Calculate Median Distance*****/
	public int MedianDistance(){
		PdVector CentroidP = new PdVector();
		PdVector CentroidQ = new PdVector();

		PdVector [] vertices_P = m_surfP.getVertices();
		PdVector [] vertices_Q = m_surfQ.getVertices();

		PdVector[] P_points = new PdVector(PointsOfSurfaceP.size());
		PdVector[] Q_points = new PdVector(PmatchQidx.size());

		for(int i=0;i<P_points.length;i++){
			P_points[i] = vertices_P[PointsOfSurfaceP.getEntry(i)];
		}

		for(int i=0;i<Q_points.length;i++){
			Q_points[i] = vertices_Q[PmatchQidx.getEntry(i)];
		}

		CentroidP = calcCentroid(P_points);
		CentroidQ = calcCentroid(Q_points);
	
		// Median still to be calculated, not sure if point to point allocation works for centroid as well
		
	}
}
