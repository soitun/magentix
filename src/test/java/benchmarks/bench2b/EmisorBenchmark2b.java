package benchmarks.bench2b;

import org.apache.qpid.transport.Connection;
import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;
import es.upv.dsic.gti_ia.magentix2.SingleAgent;

public class EmisorBenchmark2b extends SingleAgent {
	int nmsgtot;	//nombre total de missatges a enviar
	int completat=0;
	int tmsg;		//tamany del missatge
    int ntotal=0;	//nombre total d'agents
    int nemisor;	//nombre del agent
    int nreceptor;	//nombre del primer destinatari
    int nmsg=0;
    long t1,t2,tot=0;


    
	public EmisorBenchmark2b(AgentID aid, Connection connection) {
		super(aid, connection);
		nmsgtot = 10;
	}
	
	public void execute(){
		//Enviem missatge de Ready al agent controlador
		AgentID controlador = new AgentID("controlador", "http", "localhost", "8080");
		ACLMessage msgcont = new ACLMessage(ACLMessage.REQUEST);
		msgcont.setContent("Ready");
		msgcont.setReceiver(controlador);
		msgcont.setSender(this.getAid());
		
		/*
		 * Envia mensaje al controlador
		 */
		send(msgcont); 
		
		/*
		 * Espera la respuesta de inicio (Start) por parte del controlador
		 */
		receiveACLMessage();			
		
		/*
		 * Construcci�n del mensaje
		 */
		String cadena = "";
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		for(int i=0;i<tmsg;i++)
			cadena = cadena + "a";
		msg.setContent(cadena);
		//destinatari
		AgentID receiver = new AgentID();
		receiver.protocol = "http";
		receiver.port = "8080";
		nreceptor = (nemisor % ntotal) + 1;
		receiver.name = "receptor"+nreceptor;
		receiver.host = "host"+nreceptor;
		/*
		 * Cuando se han enviado todos los mensajes se acaba
		 */
		while(nmsgtot != completat){
			send(msg);					//enviem missatge
			receiveACLMessage();		//esperem a la resposta del receptor
			
			if(completat < nmsg) tot = tot + t2 - t1; //nom�s agafarem estadístiques dels primers nmsg missatges
			completat++;
		}		
		System.out.println("Mitjana RTT: "+ (float) tot/nmsg+" ms");
		send(msgcont);					//quan acaba la prova enviem missatge al controlador
	}
}
