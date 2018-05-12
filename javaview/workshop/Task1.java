package workshop;

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

		return 0;
	}
	
	public double computeVolume() {
		
		
		return 0.0;
	}
	
	public int computeComponents() {
		
		return 0;
	}
	
}
