package workshop;

import java.lang.Math;
import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import jv.geom.PgEdgeStar;
import jv.geom.PgElementSet;
import jv.object.PsDebug;
import jv.project.PgGeometry;
import jv.vecmath.PdVector;
import jv.vecmath.PiVector;
import jvx.project.PjWorkshop;

public class Task1 extends PjWorkshop {

	PgElementSet m_geom;
	PgElementSet m_geomSave;
	boolean traversedAll = false; //for checking if we traversed all the vertices in the geometry
	
	public Task1() {
		super("Task1 of the geometric model");
		init();
	}
	
	@Override
	public void setGeometry(PgGeometry geom) {
		super.setGeometry(geom);
		m_geom 		= (PgElementSet)super.m_geom;
		m_geomSave 	= (PgElementSet)super.m_geomSave;
	}
	
	public void init() {
		super.init();
	}
	
	public int computeGenus() {
		int num_Vertices,num_Faces,num_Edges;
		num_Vertices = m_geom.getNumVertices(); //Alternative length of getVertices()
		num_Faces = m_geom.getNumElements();
		num_Edges = 3*num_Faces/2;
		int edges = m_geom.getNumEdges();
		double result = (num_Vertices- num_Edges +num_Faces)/2.0;
		result= 1- result;
		if (result<0){
			return 0;
		}
		
		return (int)(result);
	}
	
	public int printVerticeNumber(){
		return m_geom.getNumVertices();
	}

	public int printFaceNumber(){
		return m_geom.getNumElements();
	}

	public int printEdgeNumber(){
		int v = m_geom.getNumVertices();
		int f = m_geom.getNumElements();
		return 3*f/2;
	}
	
	public double tetrahedronVolume(PdVector a, PdVector b, PdVector c){
		//PdVector cross = PdVector.crossNew(b, c);
		//double dot = PdVector.dot(cross, a);
		return PdVector.dot(PdVector.crossNew(c,b),a) / 6.0f;
		//return dot/6.0;
	}
	public double computeVolume() {
		
		PdVector [] vertices = m_geom.getVertices();
		PdVector vol = new PdVector((vertices.length/3)); // number of triangles ?
		for(int i = 0; i<vertices.length; i += 3){ // length?
			vol.addEntry(tetrahedronVolume(vertices[i],vertices[i+1],vertices[i+2]));
			//vol.addEntry(tetrahedronVolume(vertices.getEntries(i),vertices.getEntries(i+1),vertices.getEntries(i+2)));
		}
		return Math.abs(vol.sum());
	}
	
	public int computeComponents() {
		
		int compsNum = 0;
		PiVector [] faces = m_geom.getNeighbours();
		boolean [] visited = new boolean [faces.length];
		//boolean traversedAll = false;
		
		int current = -1;
		while(!traversedAll){
			/*for(int i =0; i<visited.length; i++){
				if(visited[i]==false){
					traversedAll=visited[i];
					current=i;
					break;
				}
				else if (i==visited.length && visited[i]==true){
					traversedAll=true;
				}
			}
			if(traversedAll){
				return compsNum;
			}*/
			current = checkVisited(visited);
			if(traversedAll){
				return compsNum;
			}
			compsNum++;
			Stack<Integer> todo = new Stack<Integer>(); 
			todo.push(current);
			
			while(!todo.isEmpty()){
				int a = todo.pop();
				if(!visited[a]){
					visited[a]=true;
					for (int faceIndex : faces[a].getEntries()) {
						todo.push(faceIndex);
					}
				}
			}	
		}
		return compsNum;
	}
	
	public int checkVisited(boolean [] visited){
		for(int i =0; i<visited.length; i++){
			if(visited[i]==false){
				return i;
			}
		}
		traversedAll=true;
		return -1;
	}
	
}
