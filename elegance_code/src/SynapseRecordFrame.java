import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * This is the frame which displays the objects on an image in a table.
 *
 * @author zndavid
 * @version 1.0
 */
public class SynapseRecordFrame
	extends JFrame
{
	private GridBagLayout gridBagLayout1 = new GridBagLayout(  );
	private JScrollPane   jScrollPane1 = new JScrollPane(  );
	private JTable        jTable1      = new JTable(  );
	private int           synName = 0;
    JButton refreshButton = new JButton();

	/**
	 * Creates a new ObjectFrame object.
	 */
	public SynapseRecordFrame ( JTable jTable )
	{
		this.jTable1 = jTable;
		try
		{
			jbInit (  );
		}
		catch ( Exception e )
		{
			e.printStackTrace (  );
		}
	}

	private void jbInit (  )
		throws Exception
	{
		this.getContentPane (  ).setLayout ( gridBagLayout1 );
		this.setSize ( new Dimension( 400, 300 ) );
		this.setTitle ( "Records" );
        refreshButton.setText("Refresh");
        jScrollPane1.getViewport (  ).add ( jTable1, BorderLayout.CENTER );
		this.getContentPane (  ).add ( 
		    jScrollPane1,
		    new GridBagConstraints( 
		        0,
		        0,
		        1,
		        1,
		        1.0,
		        1.0,
		        GridBagConstraints.CENTER,
		        GridBagConstraints.BOTH,
		        new Insets( 0, 0, 0, 0 ),
		        0,
		        0
		     )
		 );
        this.getContentPane().add(refreshButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.setVisible ( true );
	}

	
	

	/**
	 * Replaces the table with the new table
	 *
	 * @param newTable The new table.
	 */
	public void resetTable ( JTable newTable, String newTitle )
	{
		jScrollPane1.getViewport (  ).removeAll (  );
		jTable1 = newTable;
		jScrollPane1.getViewport (  ).add ( jTable1, BorderLayout.CENTER );
        this.setTitle("Objects - " + newTitle);
		this.setVisible ( true );
		this.repaint (  );
	}

	/**
	 * resets the table so that it now contains the objects from the image in the
	 * database corresponding to the image number provided.
	 *
	 * @param newImageNumber The image number.
	 */
	public void resetTable ( int synN )
	{
        this.synName = synN;
		Connection con = null;

		try
		{
			con = EDatabase.borrowConnection ( 
				    
				   
				    
				 );

			Vector columnNames = new Vector(  );
			columnNames.addElement ( "Object Name" );
			columnNames.addElement ( "Type" );
			columnNames.addElement ( "Pre" );
			columnNames.addElement ( "Post" );
			columnNames.addElement ( "Username" );
			columnNames.addElement ( "Date" );
			columnNames.addElement ( "Rate" );

			Vector            results = new Vector(  );
			PreparedStatement pst =
				con.prepareStatement ( 
				    "select OBJ_Name, type, fromObj, toObj, username, DateEntered,rate from object where OBJ_Name=?"
				 );
			pst.setInt ( 1, synName);

			ResultSet rs = pst.executeQuery (  );

			while ( rs.next (  ) )
			{
				Vector resultRow = new Vector( 6 );
				resultRow.addElement ( rs.getInt ( "OBJ_Name" ) );
				resultRow.addElement ( rs.getString ( "type" ) );
				resultRow.addElement ( rs.getString ( "fromObj" ) );
				resultRow.addElement ( rs.getString ( "toObj" ) );
				resultRow.addElement ( rs.getString ( "username" ) );
				resultRow.addElement ( rs.getDate ( "DateEntered" ) );
				resultRow.addElement ( rs.getString ( "rate" ) );
				results.addElement ( resultRow );

				//Util.info("one object");
			}



			pst =
				con.prepareStatement ( 
				    "select OBJ_Name, type, fromObj, toObj, username, DateEntered,rate from synrecord where OBJ_Name=?"
				 );
			pst.setInt ( 1, synName);

			 rs = pst.executeQuery (  );

			while ( rs.next (  ) )
			{
			    Vector resultRow = new Vector( 6 );
				resultRow.addElement ( rs.getInt ( "OBJ_Name" ) );
				resultRow.addElement ( rs.getString ( "type" ) );
				resultRow.addElement ( rs.getString ( "fromObj" ) );
				resultRow.addElement ( rs.getString ( "toObj" ) );
				resultRow.addElement ( rs.getString ( "username" ) );
				resultRow.addElement ( rs.getDate ( "DateEntered" ) );
				resultRow.addElement ( rs.getString ( "rate" ) );
				results.addElement ( resultRow );
               
				//Util.info("one object");
			}

			
			jScrollPane1.getViewport (  ).removeAll (  );
			jTable1 = new JTable( results, columnNames );
			jScrollPane1.getViewport (  ).add ( jTable1, BorderLayout.CENTER );
			this.setVisible ( true );
			this.repaint (  );
		}
		catch ( Exception ex )
		{
			ex.printStackTrace (  );
			JOptionPane.showMessageDialog ( 
			    null,
			    ex.getMessage (  ),
			    "Error",
			    JOptionPane.ERROR_MESSAGE
			 );

			
		}finally {
			EDatabase.returnConnection(con);
		}
	}

}
