import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



public class NetworkTable{
	
	

	public static void main(String[] args)  throws SQLException, ClassNotFoundException,
			java.lang.InstantiationException, java.lang.IllegalAccessException

	{
		// load object table
		
		Connection con = EDatabase.borrowConnection(
				
				);
		String jsql,jsql1,jsql2,jsql3;
		PreparedStatement pstmt,pstmt1,pstmt2,pstmt3;
		ResultSet rs,rs1,rs2,rs3;
		
		
		
		jsql = "select pre,post,concat(pre,post) as a,sum(sections) from synapsenomultiple where type='chemical' group by a";
		
		pstmt = con.prepareStatement(jsql);
		rs = pstmt.executeQuery();
		while (rs.next()) 
		{

			String pre = rs.getString(1);
			String post = rs.getString(2);
			int sections = rs.getInt(4);
			
			
			if (pre.compareTo(post)<0)
			{
			
			saveChem(post,pre,-1,"chemical",sections);
			}else{
			saveChem(pre,post,1,"chemical",sections);	
			}
			
		}
		
		rs.close();
		pstmt.close();
		
        jsql = "select pre,post,concat(pre,post) as a,sum(sections) from synapsenomultiple where type='electrical' group by a";
		
		pstmt = con.prepareStatement(jsql);
		rs = pstmt.executeQuery();
		while (rs.next()) 
		{

			String pre = rs.getString(1);
			String post = rs.getString(2);
			int sections = rs.getInt(4);
			
			 jsql1 = "select sum(sections) from synapsenomultiple where type='electrical' and pre=? and post=?";
				
				pstmt1 = con.prepareStatement(jsql1);
				pstmt1.setString(1, post);
				pstmt1.setString(2, pre);
				rs1 = pstmt1.executeQuery();
				while (rs1.next()) 
				{

					
					sections = sections + rs1.getInt(1);
					
					
				}
				rs1.close();
				pstmt1.close();
			
			
			
			
			saveChem(pre,post,0,"electical",sections);
			
			
		}
		rs.close();
		pstmt.close();

	}
	
	static void saveChem (String n1, String n2, int direction, String method, int weight1)throws SQLException, ClassNotFoundException,
	java.lang.InstantiationException, java.lang.IllegalAccessException

       {

				
		Connection con = EDatabase.borrowConnection ();
		String jsql;
		PreparedStatement pstmt;
		
		double weight = (double) weight1*0.006;
		

			jsql = "insert into network3 (n1,n2,direction,method,weight) values (?,?,?,?,?)";
			pstmt = con.prepareStatement(jsql);
			pstmt.setString(1,n1 );	
			pstmt.setString(2, n2);
			pstmt.setInt(3, direction);
			pstmt.setString(4, method);
			pstmt.setDouble(5, weight);
			
			pstmt.executeUpdate();
			pstmt.close();
	 }
	
	
	
 }

