
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JFrame;


public class N2YTable{
	
	Connection con = null;
	public static void main(String[] args) 
	{
		new N2YTable();
	}
	
	public N2YTable()
	{
		
	
      
		
		
		Statement st = null;
		ResultSet rs = null;
		
		try{
		
		con = EDatabase.borrowConnection ( );
	    st = con.createStatement();
	    rs = st.executeQuery("select name from n2ytable");
	    String contins = "0", name="", type="";
	   
	    while(rs.next()) 
	           {  
                    name =rs.getString(1);
                    contins = getContins(name);
                    type = getType(name);
                    updateTable(name,contins,type);
                    
               
			      
			   }
	    
		}catch(Exception e1){
	        e1.printStackTrace();
        }
		
		
            	
	}
	
	public String  getContins(String name)
	{
		String contins="";
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{
			
			
		    pst = con.prepareStatement("select continNums from neuron where name=?");
		    pst.setString(1, name);
		    //pst.setString(2, "["+name+"]");
		    rs = pst.executeQuery();
		   
		    if(rs.next()) { 
		    	return rs.getString(1);
		    	}
		    else{
		    	return "0";
		    }
	          
		    
		    
			}catch(Exception e1){
		        e1.printStackTrace();
	        }
		
		return "0";
	
	}
	
	
	public String  getType(String name)
	{
		String contins="";
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{
			
			
		    pst = con.prepareStatement("select type from neurontable where name=? or name=?");
		    pst.setString(1, name);
		    pst.setString(2, "["+name+"]");
		    rs = pst.executeQuery();
		   
		    if(rs.next()) { 
		    	return rs.getString(1);
		    	}
		    else{
		    	return "";
		    }
	          
		    
		    
			}catch(Exception e1){
		        e1.printStackTrace();
	        }
		
		return "";
	
	}
	
	public void updateTable(String name, String contins, String type)
	{
		
		PreparedStatement pst = null;
		
		try{
			
			
		    pst = con.prepareStatement("update n2ytable set continNums=?, type=? where name=?");
		    pst.setString(1, contins);
		    pst.setString(2, type);
		    pst.setString(3, name);
		    pst.executeUpdate();
		   
		   
		    }catch(Exception e1){
		        e1.printStackTrace();
	        }
		
		
	
	}




}
