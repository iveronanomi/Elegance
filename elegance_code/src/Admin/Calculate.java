package Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


class Serie {
	String series;
	int orix;
	int oriy;
	int oriz;
	double zoomx;
	double zoomy;
	double zoomz;
	public Serie(String s,int x, int y,int z,  double zoomx,double zoomy,double zoomz)
	{
		series=s;
		orix=x;
		oriy=y;
		oriz=z;
		this.zoomy=zoomy;
		this.zoomx=zoomx;
		this.zoomz=zoomz;
	}
    }

class Img {
	private int sectionNum;
	private String series;

	public Img(int section, String series) {
		
		this.series = series;
		this.sectionNum = section;
	}

	public int getSectionNum() {
		// TODO Auto-generated method stub
		return sectionNum;
	}

	public String getSeries() {
		// TODO Auto-generated method stub
		return series;
	}
}

class Obj {
	private String objName, imgNum, remarks;
	private int x, y, cellbody, branches;

	public Obj(String name, int x, int y, int conN, String imgNum,
			int cellbody, String remarks) {
		this.setObjName(name);
		this.setX(x);
		this.setY(y);
		this.imgNum = imgNum;
		
		this.cellbody = cellbody;
		this.remarks = remarks;
	}

	

	public void setBranches(int t) {
		this.branches = t;
	}
	
	public int getBranches() {
		return branches;
	}

	public void setX(int xx) {
		this.x = xx;
	}

	public void setY(int yy) {
		this.y = yy;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public String getObjName() {
		return objName;
	}


	public int getCellbody() {
		// TODO Auto-generated method stub
		return cellbody;
	}

	public String getRemarks() {
		// TODO Auto-generated method stub
		return remarks;
	}

	public String getImageNum() {
		// TODO Auto-generated method stub
		return imgNum;
	}

}

class Rel {
	private int segmentNum;
	private String relID, objName1, objName2;

	public Rel(String relID, String obj1, String obj2) {
		this.setRelID(relID);
		this.objName1 = obj1;
		this.objName2 = obj2;
	}

	public void setSegmentNum(int seg) {
		this.segmentNum = seg;
	}
	
	public int getSegmentNum() {
		return segmentNum;
	}

	public String getTheOtherObj(String object) {
		if (object.equals(objName1))
			return objName2;
		if (object.equals(objName2))
			return objName1;
		return null;
	}

	public void setRelID(String relID) {
		this.relID = relID;
	}

	public String getRelID() {
		return relID;
	}

	public String getObjName1() {
		// TODO Auto-generated method stub
		return objName1;
	}
	
	public String getObjName2() {
		// TODO Auto-generated method stub
		return objName2;
	}
}

class Calculate {
	String objName;
	int continNum;
	HashMap objs;
	HashMap rels;
	HashMap imgs,serieslist;
	HashMap objPreInd, objPostInd;
	LinkedHashMap nodes, edges;
	ArrayList branchpoints;
	int seg;


	public Calculate(int continNum)throws SQLException,
	ClassNotFoundException, java.lang.InstantiationException,
	java.lang.IllegalAccessException 
	{
	
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection con = DriverManager.getConnection(
				DatabaseProperties.CONNECTION_STRING,
				DatabaseProperties.USERNAME, DatabaseProperties.PASSWORD);
		String jsql;
		PreparedStatement pst;
		ResultSet rs;
		jsql = "select OBJ_Name from object where CON_Number=? limit 2,1";
		pst = con.prepareStatement(jsql);
		pst.setInt(1,continNum);
		rs = pst.executeQuery();
		if (rs.next()) {

			int objN = rs.getInt(1);
			this.objName = Integer.toString(objN);
			this.continNum = continNum;
			
			objs = new HashMap(500000);
			rels = new HashMap(500000);
			imgs = new HashMap(50000);
			objPreInd = new HashMap(500000);
			objPostInd = new HashMap(500000);
			nodes = new LinkedHashMap(10000);
			edges = new LinkedHashMap(10000);
			branchpoints = new ArrayList();
			System.out.println("bsize1"+branchpoints.size());
			serieslist = new HashMap(100);
			seg=0;
			LoadImageTable();
			LoadSeriesTable();
			LoadObjectTable();
			LoadRelationshipTable();
			LoadPostIndexTable();
			LoadPreIndexTable();
			expandObj(objName);
			setSeg();
			System.out.print("nodes:");
			saveNodes();
			System.out.print("edges:");
			saveEdges();
			saveDisplay();
			
			
		}
	}

	public Calculate(int objectName, int continNum) throws SQLException,
			ClassNotFoundException, java.lang.InstantiationException,
			java.lang.IllegalAccessException {
		
		this.objName = Integer.toString(objectName);
		this.continNum = continNum;
		seg=0;
		objs = new HashMap(500000);
		rels = new HashMap(500000);
		imgs = new HashMap(50000);
		objPreInd = new HashMap(500000);
		objPostInd = new HashMap(500000);
		nodes = new LinkedHashMap(10000);
		edges = new LinkedHashMap(10000);
		branchpoints = new ArrayList();
		serieslist = new HashMap(100);
		
		LoadImageTable();
		LoadSeriesTable();
		LoadObjectTable();
		LoadRelationshipTable();
		LoadPostIndexTable();
		LoadPreIndexTable();
		expandObj(Integer.toString(objectName));
		setSeg();
		System.out.print("nodes:");
		saveNodes();
		System.out.print("edges:");
		saveEdges();
		saveDisplay();
		
	}



	public void LoadObjectTable() throws SQLException, ClassNotFoundException,
			java.lang.InstantiationException, java.lang.IllegalAccessException

	{
		// load object table
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection con = DriverManager.getConnection(
				DatabaseProperties.CONNECTION_STRING,
				DatabaseProperties.USERNAME, DatabaseProperties.PASSWORD);
		String jsql;
		PreparedStatement pstmt;
		ResultSet rs;

		jsql = "select OBJ_Name,OBJ_X,OBJ_Y,CON_Number,IMG_Number,cellType,OBJ_Remarks from object";
		pstmt = con.prepareStatement(jsql);
		rs = pstmt.executeQuery();
		while (rs.next()) {

			String name = rs.getString(1);
			int x = rs.getInt(2);
			int y = rs.getInt(3);
			int conN = rs.getInt(4);
			String imgNum = rs.getString(5);
			
			//System.out.println("imgNum: "+imgNum);
			
			//if (imgs.containsKey(imgNum)){
            String series = ((Img)(imgs.get(imgNum))).getSeries();
			
			if (series.equals("Ventral Cord 2")) series="VC";
			if (series.equals("Ventral Cord")) series="VC";
			if (series.equals("N2YDRG")) series="DRG";
			x = getSeriesX(x,series);
			y = getSeriesY(y,series);
			
			
			
			int cellbody = rs.getInt(6);
			String remarks = rs.getString(7);

			Obj obj = new Obj(name, x, y, conN, imgNum, cellbody, remarks);
			objs.put(name, obj);
		//	}

		}
		rs.close();
		pstmt.close();

	}
	
	public void LoadSeriesTable() throws SQLException, ClassNotFoundException,
	java.lang.InstantiationException, java.lang.IllegalAccessException

    {
    // load object table
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection con = DriverManager.getConnection(
	DatabaseProperties.CONNECTION_STRING,
	DatabaseProperties.USERNAME, DatabaseProperties.PASSWORD);
    String jsql;
    PreparedStatement pstmt;
    ResultSet rs;
    jsql = "select series,x,y,z,zoomx,zoomy,zoomz from series";
    pstmt = con.prepareStatement(jsql);
    rs = pstmt.executeQuery();
    while (rs.next()) {

	String series = rs.getString(1);
	int x = rs.getInt(2);
	int y = rs.getInt(3);
	int z = rs.getInt(4);
	double zoomx = rs.getDouble(5);
	double zoomy = rs.getDouble(6);
	double zoomz = rs.getDouble(7);
	
	Serie ser = new Serie(series,x,y,z,zoomx,zoomy,zoomz);
	serieslist.put(series,ser);
    }
    rs.close();
    pstmt.close();

    }

	// load image
	public void LoadImageTable() throws SQLException, ClassNotFoundException,
			java.lang.InstantiationException, java.lang.IllegalAccessException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection con = DriverManager.getConnection(
				DatabaseProperties.CONNECTION_STRING,
				DatabaseProperties.USERNAME, DatabaseProperties.PASSWORD);
		String jsql;
		PreparedStatement pstmt;
		ResultSet rs;

		jsql = "select IMG_Number,IMG_SectionNumber,IMG_Series from image";
		pstmt = con.prepareStatement(jsql);
		rs = pstmt.executeQuery();
		while (rs.next()) {

			String img = rs.getString(1);
			int section = rs.getInt(2);
			String series = rs.getString(3);

			Img image = new Img(section, series);
			imgs.put(img, image);

		}
		rs.close();
		pstmt.close();
	}

	// load relationship
	public void LoadRelationshipTable() throws SQLException,
			ClassNotFoundException, java.lang.InstantiationException,
			java.lang.IllegalAccessException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection con = DriverManager.getConnection(
				DatabaseProperties.CONNECTION_STRING,
				DatabaseProperties.USERNAME, DatabaseProperties.PASSWORD);
		String jsql;
		PreparedStatement pstmt;
		ResultSet rs;

		jsql = "select relID,objName1,objName2 from relationship";
		pstmt = con.prepareStatement(jsql);
		rs = pstmt.executeQuery();
		while (rs.next()) {

			String relID = rs.getString(1);
			String n1 = rs.getString(2);
			String n2 = rs.getString(3);

			Rel rel = new Rel(relID, n1, n2);
			rels.put(relID, rel);

		}
		rs.close();
		pstmt.close();
	}

	public void LoadPostIndexTable() throws SQLException,
			ClassNotFoundException, java.lang.InstantiationException,
			java.lang.IllegalAccessException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection con = DriverManager.getConnection(
				DatabaseProperties.CONNECTION_STRING,
				DatabaseProperties.USERNAME, DatabaseProperties.PASSWORD);
		String jsql;
		PreparedStatement pstmt;
		ResultSet rs;
		// load object_relationship index table
		// load objName1 and post relationship index table
		jsql = "select relID,objName1 from relationship order by objName1";
		pstmt = con.prepareStatement(jsql);
		rs = pstmt.executeQuery();
		int n1 = 0;
		String post = "";
		if (rs.next()) {
			n1 = rs.getInt(2);
			post = rs.getString(1);
		}
		while (rs.next()) {

			if (rs.getInt(2) != n1) {

				objPostInd.put(Integer.toString(n1), post);
				post = rs.getString(1);
				n1 = rs.getInt(2);

			} else {
				post = post + "," + rs.getString(1);
			}
			if(rs.isLast()) objPostInd.put(Integer.toString(n1), post);
		}
		rs.close();
		pstmt.close();
	}

	public void LoadPreIndexTable() throws SQLException,
			ClassNotFoundException, java.lang.InstantiationException,
			java.lang.IllegalAccessException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection con = DriverManager.getConnection(
				DatabaseProperties.CONNECTION_STRING,
				DatabaseProperties.USERNAME, DatabaseProperties.PASSWORD);
		String jsql;
		PreparedStatement pstmt;
		ResultSet rs;

		// load objName2 and pre relationship index table
		jsql = "select relID,objName2 from relationship order by objName2";
		pstmt = con.prepareStatement(jsql);
		rs = pstmt.executeQuery();
		int n2 = 0;
		String pre = "";
		if (rs.next()) {
			n2 = rs.getInt(2);
			pre = rs.getString(1);
		}
		while (rs.next()) {

			if (rs.getInt(2) != n2) {

				objPreInd.put(Integer.toString(n2), pre);
				pre = rs.getString(1);
				n2 = rs.getInt(2);

			} else {
				pre = pre + "," + rs.getString(1);
			}
			if(rs.isLast()) objPreInd.put(Integer.toString(n2), pre);
		}
		rs.close();
		pstmt.close();

	}
	
	

	void expandObj(String root) {
		//System.out.println("root:"+root);
		Obj rob = (Obj) (objs.get(root));
		
		int pre =0,post=0;
		ArrayList f = new ArrayList();
		if (objPreInd.containsKey(root)) 
		    {
			
			String[] preRels = ((String) objPreInd.get(root)).split(",");
			pre = preRels.length;
			for(int i=0;i<pre;i++) f.add(preRels[i]);
			
			}	
		if (objPostInd.containsKey(root)) 
		    {
			String[] postRels = ((String) objPostInd.get(root)).split(",");
			post = postRels.length;
			for(int i=0;i<post;i++) f.add(postRels[i]);
			}
		int rbranches = pre+post;
		
		
		rob.setBranches(rbranches);
		if(!nodes.containsKey(root))nodes.put(root, rob);
		if (rbranches != 2 || pre==0 || post==0)
		{
			System.out.println("branch point:"+root+"   "+rbranches+"   "+pre+"   "+post);
			branchpoints.add(root);
			System.out.println("bsize2"+branchpoints.size());
		}

		for (int i = 0; i < rbranches; i++) {

			if (!edges.containsKey(f.get(i))) {
				Rel rel = (Rel) (rels.get(f.get(i)));
				edges.put(f.get(i), rel);
				
				String p = rel.getTheOtherObj(root);
				if(!nodes.containsKey(p))expandObj(p);
			}

		}

	}

	ArrayList smooth;

	void setSeg() 
	{
		System.out.println("bsize3"+branchpoints.size());
		for (int i = 0; i < branchpoints.size(); i++) 
		{
			String root = (String) branchpoints.get(i);
			//System.out.println("root of seg "+root);
			if (objPostInd.containsKey(root)) 
		    {
				//System.out.println("root of seg "+root);
				//System.out.println("seg# "+seg);
			String[] postRels = ((String) objPostInd.get(root)).split(",");
			for(int j=0;j<postRels.length;j++)
				{
				seg++;
				//System.out.println("seg## "+seg);
				smooth = new ArrayList(10000);
				smooth.add(root);
				setNextSeg(root, postRels[j], seg);
				}
			}
		

		}

	}

	void setNextSeg(String root, String relation, int segmentNum) {
		Rel edge = (Rel) (edges.get(relation));
		edge.setSegmentNum(segmentNum);
		String p = edge.getTheOtherObj(root);
		smooth.add(p);
		if (objPostInd.containsKey(p)) 
	    {
		   String[] postRels = ((String) objPostInd.get(p)).split(",");
		   int branches = getBranches(p);
		   if (!branchpoints.contains(p)) 
		    {
			setNextSeg(p, postRels[0], segmentNum);
		    } 
		    else 
		    {
			smooth();
		    }
	    }else{smooth();}
	}

	public void smooth() {
		for (int j=0;j<5;j++)
		{
		for (int i = 1; i < smooth.size() - 1; i++) 
		{
			String pre = (String) (smooth.get(i - 1));
			String current = (String) (smooth.get(i));
			String next = (String) (smooth.get(i + 1));
			int x0 = ((Obj) (nodes.get(pre))).getX();
			int x1 = ((Obj) (nodes.get(next))).getX();
			((Obj) (nodes.get(current))).setX((x0 + x1) / 2);
			int y0 = ((Obj) (nodes.get(pre))).getY();
			int y1 = ((Obj) (nodes.get(next))).getY();
			((Obj) (nodes.get(current))).setY((y0 + y1) / 2);
		}
		for (int i = smooth.size() - 2; i > 1 ; i--) 
		{
			String pre = (String) (smooth.get(i - 1));
			String current = (String) (smooth.get(i));
			String next = (String) (smooth.get(i + 1));
			int x0 = ((Obj) (nodes.get(pre))).getX();
			int x1 = ((Obj) (nodes.get(next))).getX();
			((Obj) (nodes.get(current))).setX((x0 + x1) / 2);
			int y0 = ((Obj) (nodes.get(pre))).getY();
			int y1 = ((Obj) (nodes.get(next))).getY();
			((Obj) (nodes.get(current))).setY((y0 + y1) / 2);
		}
		}
		
		
	}

	public int getBranches(String root) {
		int pr = 0, po=0;
		if (objPreInd.containsKey(root)) 
		{
			String[] preRels = ((String) objPreInd.get(root)).split(",");
			pr = preRels.length;
		}
		if (objPostInd.containsKey(root))
		{
			String[] postRels = ((String) objPostInd.get(root)).split(",");
			po = postRels.length;
		}
		return po+pr;
	}

	public static String[] getONE(String[] arg1, String[] arg2) {
		String[] result = new String[arg1.length + arg2.length];
		System.arraycopy(arg1, 0, result, 0, arg1.length);
		System.arraycopy(arg2, 0, result, arg1.length, arg2.length);
		return result;
	}

	public void saveNodes()throws SQLException, ClassNotFoundException,
	java.lang.InstantiationException, java.lang.IllegalAccessException

    {
    // save nodes
		Class.forName("com.mysql.jdbc.Driver").newInstance();		
		Connection con = DriverManager.getConnection ( 
				DatabaseProperties.CONNECTION_STRING,  
				DatabaseProperties.USERNAME,  
				DatabaseProperties.PASSWORD );
		String jsql;
		PreparedStatement pstmt;
		/**jsql = "update object set type='cell' where CON_Number=?";
		pstmt = con.prepareStatement(jsql);
		pstmt.setInt(1, continNum);		
		pstmt.executeUpdate();
		pstmt.close();**/
		Set keys = nodes.entrySet();
		Iterator iter = keys.iterator();
		while(iter.hasNext())
		{
			Map.Entry entry = (Map.Entry)iter.next();
			Obj obj = (Obj)(entry.getValue());
			
			int name = Integer.parseInt(obj.getObjName());
			jsql = "update object set CON_Number=? where OBJ_Name=?";
			pstmt = con.prepareStatement(jsql);
			pstmt.setInt(1, continNum);		
				
			pstmt.setInt(2, name);	
			pstmt.executeUpdate();
			pstmt.close();
		}
    }
	
	public void saveEdges()throws SQLException, ClassNotFoundException,
	java.lang.InstantiationException, java.lang.IllegalAccessException

    {

		Class.forName("com.mysql.jdbc.Driver").newInstance();		
		Connection con = DriverManager.getConnection ( 
				DatabaseProperties.CONNECTION_STRING,  
				DatabaseProperties.USERNAME,  
				DatabaseProperties.PASSWORD );
		String jsql;
		PreparedStatement pstmt;
		
		Set keys = edges.entrySet();
		Iterator iter = keys.iterator();
		while(iter.hasNext())
		{
			Map.Entry entry = (Map.Entry)iter.next();
			Rel rel = (Rel)(entry.getValue());
			int name = Integer.parseInt(rel.getRelID());
			int segment = rel.getSegmentNum();
			//System.out.println(rel.getObjName1()+"   "+rel.getObjName2());
			jsql = "update relationship set continNum=?, segmentNum=? where relID=?";
			pstmt = con.prepareStatement(jsql);
			pstmt.setInt(1, continNum);	
			pstmt.setInt(2, segment);
			pstmt.setInt(3, name);
			pstmt.executeUpdate();
			pstmt.close();
		}
    }
			
	public void saveDisplay()throws SQLException, ClassNotFoundException,
	java.lang.InstantiationException, java.lang.IllegalAccessException

    {
  
		Class.forName("com.mysql.jdbc.Driver").newInstance();		
		Connection con = DriverManager.getConnection ( 
				DatabaseProperties.CONNECTION_STRING,  
				DatabaseProperties.USERNAME,  
				DatabaseProperties.PASSWORD );
		String jsql;
		PreparedStatement pstmt;
		
		jsql = "delete from display2 where continNum=?";
		pstmt = con.prepareStatement(jsql);
		pstmt.setInt(1, continNum);	
		pstmt.executeUpdate();
		pstmt.close();
		
		ArrayList seriess = new ArrayList();
		String continseries = "";
		
		Set keys = edges.entrySet();
		Iterator iter = keys.iterator();
		while(iter.hasNext())
		{
			Map.Entry entry = (Map.Entry)iter.next();
			Rel rel = (Rel)(entry.getValue());
			//System.out.println("obj1 "+rel.getObjName1());
			//System.out.println("obj2 "+rel.getObjName2());
			Obj obj1 = (Obj) nodes.get(rel.getObjName1());
			Obj obj2 = (Obj) nodes.get(rel.getObjName2());
			
			int x1 = obj1.getX();
			
			int y1 = obj1.getY();
			
			int segN = rel.getSegmentNum(); 
			int cellbody1 = obj1.getCellbody();
			int cellbody2 = obj2.getCellbody();
			int branch1 = obj1.getBranches();
			int branch2 = obj2.getBranches();
			String remarks1 = obj1.getRemarks();
			String remarks2 = obj2.getRemarks();
			String imgN1 = obj1.getImageNum();
			String imgN2 = obj2.getImageNum();
			//System.out.println("imgN1="+imgN1);
			//System.out.println("imgN2="+imgN2);
			int z1 = ((Img)(imgs.get(imgN1))).getSectionNum();
			int x2 = obj2.getX();
			int y2 = obj2.getY();
			int z2 = ((Img)(imgs.get(imgN2))).getSectionNum();
			String objName1=obj1.getObjName();
			String objName2=obj2.getObjName();
			
			String series1 = ((Img)(imgs.get(imgN1))).getSeries();
			String series2 = ((Img)(imgs.get(imgN2))).getSeries();
			
			if (series1.equals("Ventral Cord 2")) series1="VC";
			if (series1.equals("Ventral Cord")) series1="VC";
			if (series1.equals("N2YDRG")) series1="DRG";
			if(!seriess.contains(series1)) seriess.add(series1);
		
		
			
			

			
			jsql = "insert into display2 (x1,y1,z1,continNum,cellbody1,remarks1,segmentNum,branch1,objName1," +
			"x2,y2,z2,cellbody2,remarks2,branch2,objName2,series1,series2) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	pstmt = con.prepareStatement(jsql);
	pstmt.setInt(1, x1);	
	pstmt.setInt(2, y1);
	pstmt.setInt(3, z1);
	pstmt.setInt(4, continNum);	
	pstmt.setInt(5, cellbody1);
	pstmt.setString(6, remarks1);
	pstmt.setInt(7,segN);

	pstmt.setInt(8,branch1);
	pstmt.setString(9,objName1);
	pstmt.setInt(10, x2);	
	pstmt.setInt(11, y2);
	pstmt.setInt(12, z2);
	
	pstmt.setInt(13, cellbody2);
	pstmt.setString(14, remarks2);

	pstmt.setInt(15,branch2);
	pstmt.setString(16,objName2);
	pstmt.setString(17,series1);
	pstmt.setString(18,series2);
	
	pstmt.executeUpdate();
	pstmt.close();
		}
		
		
		pstmt = con.prepareStatement("select max(IMG_SectionNumber),min(IMG_SectionNumber) from object,image where object.IMG_Number=image.IMG_Number and CON_Number=?");
        pstmt.setInt(1, continNum);
        ResultSet rs1 = pstmt.executeQuery();
        rs1.next();
        int img2 = rs1.getInt(1);
        int img1 = rs1.getInt(2);
        rs1.close();
        pstmt.close();
		
		
		jsql = "update contin set series=?, count=?,sectionNum1=?,sectionNum2=?,CON_AlternateName2=? where CON_Number=?";
		continseries = (String) seriess.get(0);
		for(int i=1;i<seriess.size();i++)
		{
			continseries=continseries+","+(String)seriess.get(i);
		}
		
		pstmt = con.prepareStatement(jsql);
		pstmt.setString(1, continseries);
		pstmt.setInt(2, nodes.size());
		pstmt.setInt(3, img1);
		pstmt.setInt(4, img2);
		
		Contin c = new Contin(continNum);
		
		pstmt.setString(5, c.colorcode);
		
		pstmt.setInt(6, continNum);
		pstmt.executeUpdate();
		pstmt.close();
		
		
		
    }

public int getSeriesX(int x,String series)
{
if (serieslist.containsKey(series)){
Serie ser = (Serie)(serieslist.get(series));
x=ser.orix+(int)(x/ser.zoomx);
}
return x;	

}

public int getSeriesY(int y,String series)
{
if (serieslist.containsKey(series)){
Serie ser = (Serie)(serieslist.get(series));
y=ser.oriy+(int)(y/ser.zoomy);
}
return y;	
}



}