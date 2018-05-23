package workshop;


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
import jv.vecmath.PdMatrix;
import jv.vecmath.PdVector;
import jvx.project.PjWorkshop;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

import jvx.project.PjWorkshop;

import java.awt.Color;
import java.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

//import com.sun.javafx.geom.PickRay;


//import sun.java2d.cmm.ProfileDataVerifier;


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
	// Arrays to save the P and Q points that match as well as the calculatetd distance between them
	PdVector[] matchingPointsP;
	PdVector[] matchingPointsQ;
	
	PdVector[] updatedMatchingPointsP;
	PdVector[] updatedMatchingPointsQ;
	double []  updatedDistance;

	
	//double distance[] = {1,3,3,6,7,8,15};
	double distance [];
	int maximumIteration = 300;

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
		int numberOfSamples=numberOfPoints/10;
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

	/*****Closest Distance*****/
	public void closestVertex(){
		int RandSelP = RandomSelectionP();
		int index;
		double[] distpts = new double[RandSelP];
		double[] results;
		
		distance = new double [m_surfQ.getNumVertices()];

		PdVector [] vertices_P = m_surfP.getVertices();
		PdVector [] vertices_Q = m_surfQ.getVertices();
		int lengthOfQ=vertices_Q.length - 1;
		for (int i = 0; i<RandSelP; i++){
			Arrays.fill(distpts,0.0);
			for(int j = 0; j < lengthOfQ; j++){
				distpts[j] = PdVector.dist(vertices_P[PointsOfSurfaceP.get(i)], vertices_Q[j]);
			}
			results = min_index(distpts);
			index = (int) results[0];
			matchingPointsP[i]=vertices_P[PointsOfSurfaceP.get(i)];
			matchingPointsQ[i]=vertices_Q[index];
			distance[i]=results[1];
		}
		//return matchingPointsQ.length; //made it void
	}

	public double[] min_index(double[] a){
		double min = a[0];
		int min_idx=0;
		double result[] ={0,0};
		for(int i=0;i<a.length-1;i++){
			if(a[i] < min){
				min = a[i];
				min_idx = i;
			}
		}
		result[0]=(double)min_idx;
		result[1]=min;
		return result;
	}

	public PdVector[] getMatchingQ(){
		return matchingPointsQ;
	}

	public PdVector[] getMatchingP(){
		return matchingPointsP;
	}

	public double[] getDistances(){
		return distance;
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

		return ctr;
	}

	/*****Calculate Median Distance*****/
	public double MedianDistance(){
		int size=distance.length;
		double median=0.0;
		double[] copy_distance=distance;
		Arrays.sort(copy_distance);
		if (size % 2 == 0){
			int i = size/2;
			int j = i-1;
			median = (copy_distance[j] + copy_distance[i])/2;
		}
		else{
			int k = (size-1)/2;
			median = copy_distance[k];
		}
		return median;
	}

	public int DiscardPoints(int k){
		int numberOfDiscardedPoints=0;
		double medianValue = MedianDistance();
		for (int i=0; i<distance.length; i++){
			if (distance[i] > k* medianValue){
				distance[i] = -1;
				matchingPointsP[i] = null;
				matchingPointsQ[i] = null;
				numberOfDiscardedPoints++;
			}
		}
		return numberOfDiscardedPoints;
	}


		// Median still to be calculated, not sure if point to point allocation works for centroid as well
		
	
	
	public PdMatrix calculateM(PdVector[] P, PdVector[] Q) {
        PdVector pBar = calcCentroid(P);
        PdVector qBar = calcCentroid(Q);

        PdMatrix M = new PdMatrix(3);
        for(int i = 0; i < P.length; i++) {
            PdMatrix matrixRow = new PdMatrix();
            
			PdVector pMinusPBar = PdVector.subNew(P[i], pBar);
            PdVector qMinusQBar = PdVector.subNew(Q[i], qBar);
            
            matrixRow.adjoint(pMinusPBar, qMinusQBar); // = v * w^T
            M.add(matrixRow);
        }
        M.multScalar(1 / P.length);
        return M;
    }
	
	public PdMatrix calculateRopt(SingularValueDecomposition svd) { //have to call this after declaring the singular value decomposition
        Matrix mat = Matrix.identity(3,3);
        Matrix V = svd.getV();
        Matrix UT = svd.getU();
		UT.transpose();
        Matrix VUT = V.times(UT);
		mat.set(2, 2, VUT.det());
        Matrix optR = V.times(mat).times(UT);
		PdMatrix Ropt = new PdMatrix(optR.getArrayCopy());

        return Ropt;
    }
	
	public PdVector calculateTopt(PdVector[] P, PdVector[] Q, PdMatrix Ropt) {
        PdVector centroidP = calcCentroid(P);
        PdVector centroidQ = calcCentroid(Q);
		PdVector out = new PdVector(); //Initialization was needed
		Ropt.leftMultMatrix(out, centroidP);
		PdVector tOpt = PdVector.subNew(centroidQ, out);
        return tOpt;
    }
	
	public void updatePointsArray(/*PdVector [] P, PdVector [] Q*/){
	/*	List <PdVector> psurf = new ArrayList<PdVector>();
		List <PdVector> qsurf = new ArrayList<PdVector>();
		
		for(int i =0; i< P.length; i++){
			if(P[i]!=null) psurf.add(P[i]); 
		}
		for(int i =0; i< Q.length; i++){
			if(Q[i]!=null) qsurf.add(Q[i]); 
		}
		
		updatedMatchingPointsP = psurf.toArray();
		updatedMatchingPointsQ = qsurf.toArray();*/
		int counter=0;
		for (int i=0; i<distance.length; i++){
			if (distance[i] != -1){
				updatedDistance[counter] = distance[i];
				updatedMatchingPointsP[counter] = matchingPointsP[i];
				updatedMatchingPointsQ[counter] = matchingPointsQ[i];
				counter++;
			}
		}


	}
	
	public void translation(PdVector tOpt, PgElementSet P){
		PdVector [] vertex = P.getVertices();
		for(int i =0; i< vertex.length; i++){
			vertex[i].add(tOpt);
		}
		
	}
	
	public void rotation(PdMatrix Ropt, PgElementSet P){
		PdVector [] vertex = P.getVertices();
		for(int i =0; i< vertex.length; i++){
			vertex[i].leftMultMatrix(Ropt);
		}
		
	}
	
	
	public void surfaceRegistration(){
		/* 	1. first select random vertices from P. 
			2. get closest vertices
			3. calculate distance and median;; distance is also calculated inside closestVertex()
			4. remove vetices based on median
			5. compute M , SVD
			6. rotate, translate and update mesh
		*/	
		
		for(int step =0; step< maximumIteration; step++){
			
			closestVertex(); //random selection is called inside closest vertex
			int noOfDiscardedPoints = DiscardPoints(3);
			updatePointsArray(/*matchingPointsP, matchingPointsQ*/);
			PdMatrix M = calculateM(updatedMatchingPointsP, updatedMatchingPointsP);
			SingularValueDecomposition SVD = new SingularValueDecomposition (new Jama.Matrix(M.getEntries()));
			PdMatrix Ropt = calculateRopt(SVD);
			PdVector tOpt = calculateTopt(matchingPointsP, matchingPointsQ, Ropt);
			rotation(Ropt, m_surfP);
			translation(tOpt, m_surfP);
			m_surfP.update(m_surfP);
			
		}
	}

}

