/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Principal.java
 *
 * Created on 21-ago-2009, 17:49:00
 */

package _Request_Example;

/**
 * 
 * @author joan
 */
import java.util.ArrayList;

import org.apache.qpid.transport.Connection;

import es.upv.dsic.gti_ia.core.AgentID;



public class Principal_Grafico extends javax.swing.JFrame {

	private Connection con;
	private Hospital AgenteHospital;
	private Testigo AgenteTestigo;
	private Llamadas llamada = new Llamadas();;


	/** Creates new form Principal */
	public Principal_Grafico() {
		initComponents();

	}

	public javax.swing.JTextArea getTextArea(int op) {
		if (op == 1) {
			return this.jTextArea1;
		} else {
			return this.jTextArea2;
		}
	}

	public javax.swing.JTextField getTextField() {

		return this.jTextField1;

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// @SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		jList1 = new javax.swing.JList();
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jButton3 = new javax.swing.JButton();
		jScrollPane2 = new javax.swing.JScrollPane();
		jTextArea1 = new javax.swing.JTextArea();
		jButton4 = new javax.swing.JButton();
		jScrollPane3 = new javax.swing.JScrollPane();
		jTextArea2 = new javax.swing.JTextArea();
		jTextField1 = new javax.swing.JTextField();
		jLabel1 = new javax.swing.JLabel();
		jButton5 = new javax.swing.JButton();
		jButton6 = new javax.swing.JButton();
		jButton7 = new javax.swing.JButton();
		jLabel2 = new javax.swing.JLabel();
		jTextField2 = new javax.swing.JTextField();
		jButton8 = new javax.swing.JButton();
		jButton9 = new javax.swing.JButton();

		jList1.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4",
					"Item 5" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane1.setViewportView(jList1);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);

		jButton1.setLabel("Crear Hospital");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jButton2.setLabel("Crear Testigo");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

		jButton3.setLabel("Accidente nuevo");
		jButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton3ActionPerformed(evt);
			}
		});

		jTextArea1.setColumns(20);
		jTextArea1.setRows(5);
		jScrollPane2.setViewportView(jTextArea1);

		jButton4.setLabel("Crear Conexi�n");
		jButton4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton4ActionPerformed(evt);
			}
		});

		jTextArea2.setColumns(20);
		jTextArea2.setRows(5);
		jTextArea2.setRequestFocusEnabled(false);
		jScrollPane3.setViewportView(jTextArea2);

		jTextField1.setText("5");

		jLabel1.setText("Distancia accidente.");

		jButton5.setText("Terminar");
		jButton5.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton5ActionPerformed(evt);
			}
		});

		jButton6.setText("Limpiar");
		jButton6.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton6ActionPerformed(evt);
			}
		});

		jButton7.setText("Limpiar");
		jButton7.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton7ActionPerformed(evt);
			}
		});

		jLabel2.setText("km");

		jTextField2.setText("100");

		jButton8.setText("Enviar n mensajes");
		jButton8.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton8ActionPerformed(evt);
			}
		});

		jButton9.setText("Crear Agentes");
		jButton9.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton9ActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addGap(45, 45, 45)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jButton5)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jScrollPane2,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								363,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								jButton1)
																						.addGroup(
																								layout
																										.createSequentialGroup()
																										.addComponent(
																												jButton4)
																										.addGap(
																												97,
																												97,
																												97)
																										.addComponent(
																												jButton9))
																						.addComponent(
																								jButton7))
																		.addGap(
																				42,
																				42,
																				42)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jScrollPane3,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								386,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addGroup(
																								layout
																										.createSequentialGroup()
																										.addGroup(
																												layout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING)
																														.addComponent(
																																jButton2)
																														.addComponent(
																																jButton6)
																														.addComponent(
																																jButton3))
																										.addGap(
																												48,
																												48,
																												48)
																										.addGroup(
																												layout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING)
																														.addComponent(
																																jTextField2,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																javax.swing.GroupLayout.PREFERRED_SIZE)
																														.addComponent(
																																jButton8)
																														.addGroup(
																																layout
																																		.createSequentialGroup()
																																		.addComponent(
																																				jLabel1)
																																		.addGap(
																																				18,
																																				18,
																																				18)
																																		.addComponent(
																																				jTextField1,
																																				javax.swing.GroupLayout.PREFERRED_SIZE,
																																				35,
																																				javax.swing.GroupLayout.PREFERRED_SIZE)
																																		.addGap(
																																				18,
																																				18,
																																				18)
																																		.addComponent(
																																				jLabel2)))))))
										.addGap(126, 126, 126)));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addGap(33, 33, 33)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jButton2)
																						.addComponent(
																								jLabel1)
																						.addComponent(
																								jTextField1,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								jLabel2))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				jButton3)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jButton4)
																						.addComponent(
																								jButton9))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jButton1)))
										.addGap(5, 5, 5)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jButton7)
														.addComponent(jButton6))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jScrollPane3,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																218,
																Short.MAX_VALUE)
														.addComponent(
																jScrollPane2,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																218,
																Short.MAX_VALUE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jButton5).addGap(21, 21,
												21))
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								layout
										.createSequentialGroup()
										.addContainerGap(64, Short.MAX_VALUE)
										.addComponent(jButton8)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jTextField2,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(279, 279, 279)));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed

		try{
		if (AgenteHospital == null) {
			AgenteHospital = new Hospital(new AgentID("OMS"), this);
			AgenteHospital.start();
			this.jTextArea1
					.append("Agente Hospital creado satisfactoriamente.\n");
		} else {
			System.out.println("Agente Hospital creado anteriormente");
		}
		
		}catch(Exception e){}
		// TODO add your handling code here:

	}// GEN-LAST:event_jButton1ActionPerformed

	private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton4ActionPerformed

		if (con == null) {
			con = new Connection();
			con.connect("gtiiaprojects.dsic.upv.es", 5672, "test", "guest",
					"guest", false); // TODO add your handling code here:
			this.jTextArea1.append("Conexi�n creada satisfactoriamente.\n");

		} else {
			System.out.println("Conexi�n creada anteriormente");
		}
	}// GEN-LAST:event_jButton4ActionPerformed

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed

		// TODO add your handling code here:
		try{
		if (AgenteTestigo == null) {

			AgenteTestigo = new Testigo(new AgentID("Testigo"), this, llamada);

			AgenteTestigo.start();
		} else {
			System.out.println("Agente Testigo  creado anteriormente");
		}
		}catch(Exception e){}

	}// GEN-LAST:event_jButton2ActionPerformed

	private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton3ActionPerformed
		// TODO add your handling code here:

		// AgenteTestigo.start();

		this.llamada.dar();

	}// GEN-LAST:event_jButton3ActionPerformed

	private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton5ActionPerformed

		AgenteTestigo.detenerHilo();

		// TODO add your handling code here:
	}// GEN-LAST:event_jButton5ActionPerformed

	private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton7ActionPerformed

		this.jTextArea1.setText("");

		// TODO add your handling code here:
	}// GEN-LAST:event_jButton7ActionPerformed

	private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton6ActionPerformed
		this.jTextArea2.setText(""); // TODO add your handling code here:
	}// GEN-LAST:event_jButton6ActionPerformed

	private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton8ActionPerformed
		// TODO add your handling code here:

		for (int i = 0; i <= Integer.parseInt(this.jTextField2.getText()); i++) {

			AgenteTestigo.enviarMensaje(i);

		}
	}// GEN-LAST:event_jButton8ActionPerformed

	private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton9ActionPerformed
		// TODO add your handling code here:

		//ArrayList<Testigo> agentsList = new ArrayList<Testigo>();
		try{
		Testigo Agente$nagentes = new Testigo(new AgentID("Testigo"), this, llamada);
		//String nagentes = "C" + hashCode() + "_" + System.currentTimeMillis();
		Agente$nagentes.start();
		}catch(Exception e){}

	}// GEN-LAST:event_jButton9ActionPerformed

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Principal_Grafico().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JButton jButton3;
	private javax.swing.JButton jButton4;
	private javax.swing.JButton jButton5;
	private javax.swing.JButton jButton6;
	private javax.swing.JButton jButton7;
	private javax.swing.JButton jButton8;
	private javax.swing.JButton jButton9;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JList jList1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JTextArea jTextArea1;
	private javax.swing.JTextArea jTextArea2;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JTextField jTextField2;
	// End of variables declaration//GEN-END:variables

}
