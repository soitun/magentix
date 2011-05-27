package LoadLauncher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import LoadLauncher.Load.*;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
//import es.upv.dsic.gti_ia.core.BaseAgent;

public class PublisherBroadcast extends BaseAgent {
	private String LOG_FILE_NAME;
	
	private ArrayList<Publication> publications;
	private ArrayList<AgentID> subscribers;
	private AgentID broadcastManagerAid;
	private ArrayList<Transmission> transmissions;
	private ArrayList<Long> sequences;
	private long messages_to_send;
	
	public PublisherBroadcast(Load load_spec, int index) throws Exception {
		super(load_spec.getPublishers().get(index).getAid());
		
		this.LOG_FILE_NAME = load_spec.getOutPath() + "/" +
			Load.prefixes[load_spec.getStrategy()] + "_" + this.getName() + "_result_log.txt";
		this.publications = load_spec.getPublishers().get(index).getPublications();
		this.transmissions = new ArrayList<Transmission>();
		this.subscribers = new ArrayList<AgentID>();
		this.broadcastManagerAid=load_spec.getMiddleAgentID();
		this.sequences = new ArrayList<Long>();
		this.messages_to_send = 0;
		
		ACLMessage msg;
		msg = new ACLMessage();
		msg.setLanguage("ACL");
		msg.setPerformative(ACLMessage.QUERY_REF);
		msg.setReceiver(broadcastManagerAid);
		msg.setSender(this.getAid());
		msg.setContent(String.valueOf(System.currentTimeMillis()));
		send(msg);
		
		Publication auxPub;
		
		synchronized (publications){
			Iterator<Publication> pubIterator = publications.iterator();
			
			while (pubIterator.hasNext()){
				// Publish things
				auxPub=pubIterator.next();
				sequences.add(auxPub.getMessagesToSend());
				messages_to_send = messages_to_send + sequences.get(publications.indexOf(auxPub));
			}
		}
	}
	
	public void execute() {
		Iterator<Publication> pubIterator;
		Iterator<AgentID> AidIterator;
		Publication auxPub, nextPub=null;
		long currentTime = System.currentTimeMillis();
		ACLMessage msg;
		long nextStop=Long.MAX_VALUE, timeout;
		
		Random generator = new Random(System.currentTimeMillis());
		
		// Initial transmissions
		pubIterator=publications.iterator();
		while(pubIterator.hasNext()){
			auxPub = pubIterator.next();
			
			try {
				timeout=generator.nextInt(5000);
				synchronized(this){
					wait(timeout);
				}
				currentTime = System.currentTimeMillis();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			Long auxLong=sequences.get(publications.indexOf(auxPub));
						
			msg=new ACLMessage();
			msg.setLanguage("ACL");
			msg.setPerformative(ACLMessage.INFORM);
			msg.setSender(this.getAid());
			msg.setContent(String.valueOf(System.currentTimeMillis()) + "#" + auxLong + "#" + auxPub.getChannelName());
			AidIterator=subscribers.iterator();
			while(AidIterator.hasNext()){
				msg.setReceiver(AidIterator.next());
				send(msg);
			}
			auxLong--;
			
			sequences.set(publications.indexOf(auxPub), auxLong);
			auxPub.setNextPublication(currentTime + auxPub.getPeriod());
			if (nextStop > auxPub.getNextPublication()){
				nextStop = auxPub.getNextPublication();
				nextPub=auxPub;
			}
			
			messages_to_send--;
		}
		
		while (messages_to_send > 0){
			try {
				if ((timeout=(nextStop-currentTime)) > 0){
					synchronized(this){
						wait(timeout);
					}
				}
//				else {
////					System.out.println("Que me cago, que no llego..!");
//				}
				currentTime = System.currentTimeMillis();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Long auxLong=sequences.get(publications.indexOf(nextPub));
			msg=new ACLMessage();
			msg.setLanguage("ACL");
			msg.setPerformative(ACLMessage.INFORM);
			msg.setSender(this.getAid());
			msg.setContent(String.valueOf(System.currentTimeMillis()) + "#" + auxLong + "#" + nextPub.getChannelName());
			AidIterator=subscribers.iterator();
			while(AidIterator.hasNext()){
				msg.setReceiver(AidIterator.next());
				send(msg);
			}
			
			auxLong--;
			sequences.set(publications.indexOf(nextPub), auxLong);
			
			if (auxLong > 0){
				nextPub.setNextPublication(currentTime + nextPub.getPeriod());
			}
			else{
				nextPub.setNextPublication(Long.MAX_VALUE);	
			}
			nextStop=nextPub.getNextPublication();
			
			pubIterator=publications.iterator();
			while(pubIterator.hasNext()){
				auxPub = pubIterator.next();
				if (nextStop > auxPub.getNextPublication()){
					nextStop = auxPub.getNextPublication();
					nextPub=auxPub;
				}
			}

			messages_to_send--;
		}
		
		System.out.println(this.getName() + " writing data...");
		
		// Append to file
		BufferedWriter log_file;
		try {
			log_file = new BufferedWriter(new FileWriter(LOG_FILE_NAME, false));
			Transmission auxTrans;
			synchronized(transmissions){
				Iterator<Transmission> transIter;
				transIter=transmissions.iterator();
				while (transIter.hasNext()){
					auxTrans=transIter.next();
					log_file.write(auxTrans.toString()+"\n");
				}
			}
			log_file.close();

			System.out.println(this.getName() + " done writing data!");
			
			synchronized(this){
				wait(1000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void onMessage(ACLMessage msg){
		Transmission trans;
		Date auxDate;
		
		if (msg.getPerformativeInt() == ACLMessage.INFORM){
			int i, nsubscribers, length;
			int index1, index2;
					
			auxDate = new Date(System.currentTimeMillis());
					
			index1=msg.getContent().indexOf('#');
					
			trans = new Transmission(msg.getSender(), this.getAid(),
						new Date(Long.parseLong(msg.getContent().substring(0, index1))),
						auxDate, Transmission.SYSTEM, msg.getContent().substring(index1+1));
			synchronized(transmissions){
				transmissions.add(trans);
			}
					
			index1=index1+1;
			index2=msg.getContent().indexOf('#', index1);
			nsubscribers=Integer.valueOf(msg.getContent().substring(index1, index2));
			index1=index2+1;
					
			for (i=0; i < nsubscribers; i++){
				index2=msg.getContent().indexOf('#', index1);
				length=Integer.valueOf(msg.getContent().substring(index1, index2));
				index1=index2+1;
				index2=index1+length;
				synchronized(subscribers){
					subscribers.add(new AgentID(msg.getContent().substring(index1, index2)));
				}
				index1=index2+1;
			}
		}
	}
}
