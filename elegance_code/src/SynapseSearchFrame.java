import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

/**
 * This is the frame which displays the objects on an image in a table.
 *
 * @author zndavid
 * @version 1.0
 */
public class SynapseSearchFrame
	extends JFrame
{
	private GridBagLayout gridBagLayout1 = new GridBagLayout(  );
	String username;
	Vector usyn1,usyn2;
	JLabel uncertain = new JLabel("uncertain synapses");
	JLabel user = new JLabel("user confliction");
	private JButton[] showButton1,showButton2;
	String[] syns1,syns2;
	MainFrame mainFrame;
	/**
	 * Creates a new ObjectFrame object.
	 */
	public SynapseSearchFrame ( Vector usyn1,Vector usyn2, String username, MainFrame mainFrame )
	{
		this.mainFrame=mainFrame;
		this.username=username;
		this.usyn1=usyn1;
		this.usyn2=usyn2;
		syns1 = new String[usyn1.size()];
		showButton1 = new JButton[usyn1.size()];
		for (int i=0;i<usyn1.size();i++)
		{
			syns1[i] = (String)usyn1.elementAt(i);
			ELog.info(syns1[i]);
		}
		
		syns2 = new String[usyn2.size()];
		showButton2 = new JButton[usyn2.size()];
		for (int i=0;i<usyn2.size();i++)
		{
			syns2[i] = (String)usyn2.elementAt(i);
			ELog.info(syns2[i]);
		}
		
		try
		{
			jbInit (  );
		}
		catch ( Exception e )
		{
			e.printStackTrace (  );
		}
	}

	private void jbInit() throws Exception {
		setTitle("View The Synapse Data");
		setSize(300, 400);
		JPanel p = new JPanel();
		p.setSize(300,400);
		p.setLayout(gridBagLayout1);
		JScrollPane scrollpane = new JScrollPane(p);
		getContentPane().add(scrollpane, BorderLayout.CENTER);
		
		p.add(uncertain,new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 20, 0, 0), 0, 0));
		
		for (int i=0;i<usyn1.size();i++)
		{
			ELog.info("i="+i);
			ELog.info(syns1[i]);
			showButton1[i]= new JButton(syns1[i]);
			showButton1[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showButton_actionPerformed(e);
				}
				});
			p.add(showButton1[i],new GridBagConstraints(0, i+1, 3, 1, 1.0, 1.0,
							GridBagConstraints.WEST, GridBagConstraints.NONE,
							new Insets(0, 20, 0, 0), 0, 0));
		}
		p.add(user,new GridBagConstraints(0, 1+usyn1.size(), 3, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 20, 0, 0), 0, 0));
		for (int i=0;i<usyn2.size();i++)
		{
			ELog.info("i="+i);
			ELog.info(syns2[i]);
			showButton2[i]= new JButton(syns2[i]);
			showButton2[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showButton_actionPerformed(e);
				}
				});
			p.add(showButton2[i],new GridBagConstraints(0, i+2+usyn1.size(), 3, 1, 1.0, 1.0,
							GridBagConstraints.WEST, GridBagConstraints.NONE,
							new Insets(0, 20, 0, 0), 0, 0));
		}
		
		this.setVisible ( true );
	}
	
	 private void showButton_actionPerformed(ActionEvent e) {
	    	String idx =e.getActionCommand();
	    	
	       	mainFrame.findSyn(idx);
	       	this.toFront();
	    	
	    	
		}


}
