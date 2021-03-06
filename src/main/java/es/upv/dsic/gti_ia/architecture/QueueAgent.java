/**
 * This class creates a new agent queue.
 * 
 * @author  Joan Bellver Faus, GTI-IA, DSIC, UPV
 * @version 2009.9.07
 */

package es.upv.dsic.gti_ia.architecture;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

/**
 * 
 * This class defines an new agents template, extending of BaseAgent. This type
 * of agent's implements the protocols.
 * 
 * @author Joan Bellver Faus
 * 
 */

public class QueueAgent extends BaseAgent {

	private Queue<ACLMessage> messageList = new LinkedList<ACLMessage>();

	private Monitor monitor = null;

	private ArrayList<String> activeConversationsList = new ArrayList<String>();

	private ArrayList<Object> roles = new ArrayList<Object>();

	/**
	 * Create a new QueueAgent.
	 * 
	 * @param aid
	 *            agent ID.
	 * @throws Exception
	 *             if agent id already exists on the platform
	 */

	public QueueAgent(AgentID aid) throws Exception {
		super(aid);

	}

	/**
	 * Function that will be executed when the agent gets a message
	 * 
	 * @param msg
	 *            ACLMessage
	 */
	protected void onMessage(ACLMessage msg) {

		this.writeQueue(msg);

		// responsible for waking the class agent, may be the responder role or
		// the role iniciator
		if (monitor != null) {

			this.monitor.advise();
		}

	}

	/**
	 * This method adds a new message into messageList in synchronized mode.
	 * @param msg ACLMessage
	 */
	private synchronized void writeQueue(ACLMessage msg) {

		try {
			messageList.add(msg);
		} catch (Exception e) {
			System.out.println("e: " + e);
		}
		// Collections.synchronizedCollection(messageList);

	}

	/**
	 * Method to receives a magentix2 ACLMessage
	 * 
	 * @param template
	 *            is a MessageTemplate, will serve as a filter for receiving the
	 *            right message
	 * @return msg ACLMessage
	 */
	public synchronized ACLMessage receiveACLMessage(MessageTemplate template)
	throws Exception {
		ACLMessage msgselect = null;
		Iterator<ACLMessage> iterator = messageList.iterator();

		if (template.getProtocol() == "")
			throw new Exception("The protocol field is empty");

		while(iterator.hasNext())
		{
			ACLMessage msg = iterator.next();

			//Matching with the protocol and converastionID fields, to ensure that the conversation isn't a 
			//existing conversation 
			if (template.getProtocol().equals(msg.getProtocol())) {
				msgselect = msg;

			}

		}
		if (msgselect != null) {
			messageList.remove(msgselect);
		}
		return msgselect;
	}

	/**
	 * Method to receives a magentix2 ACLMessage
	 * 
	 * @param template
	 *            is a MessageTemplate, will serve as a filter for receiving the
	 *            right message
	 * @param type
	 *            1 = rol responder other = rol initiator
	 * @return msg ACLMessage
	 */
	synchronized ACLMessage receiveACLMessage(MessageTemplate template, int type) {
		ACLMessage msgselect = null;

		Iterator<ACLMessage> iterator = messageList.iterator();
		
		if (type == 1) {
				
				while(iterator.hasNext())
				{
					ACLMessage msg = iterator.next();
				
			
					//Matching with the protocol and converastionID fields, to ensure that the conversation isn't a 
					//existing conversation 
					if (template.getPerformativeInt() != -2
							&& template.getPerformative().equals(
									msg.getPerformative())
									|| !template.getProtocol().equals("")
									&& template.getProtocol().equals(msg.getProtocol())) {
						msgselect = msg;
						for (String conv : this.activeConversationsList) {
							//If exist, then the message should be administered by the initiator role.
							if (conv.equals(msg.getConversationId())) {
								msgselect = null;
								break;
							}
						}
					}

				}
			} else {

				while(iterator.hasNext())
				{
					ACLMessage msg = iterator.next();
				
					
					//This is a special case, when is a cancel performative.
					if (template.getPerformativeInt() != ACLMessage.CANCEL
							|| template.getPerformative().equals(
									msg.getPerformative())) {
						//Matching with the protocol, converastionID fields and sender. 
						if (template.getProtocol().equals(msg.getProtocol())) {
							//Look inside the conversations 
							for (String conversacion : template
									.getList_Conversation())
								if (conversacion
										.equals(msg.getConversationId())) {
									msgselect = msg;
									break;
								}
						}
						if (msgselect != null)
							break;
					}
				}// for
			}
			if (msgselect != null) {
				messageList.remove(msgselect);
			}

		
		return msgselect;
	}



	/**
	 * Returns number of roles inside the agent 
	 * @return int number of roles
	 */
	private synchronized int addRole(Object b) {
		this.roles.add(b);
		return this.roles.size();
	}

	/**
	 * Removes a specific role.
	 * 
	 * @return int remove a role
	 */
	private synchronized int removeRole(Object b) {
		this.roles.remove(b);
		return this.roles.size();
	}

	/**
	 * Returns a number of roles
	 * 
	 * @return int number of roles
	 */
	private synchronized int getnRole() {
		return this.roles.size();
	}

	/**
	 * Adds new monitor
	 * 
	 * @return Monitor
	 */
	synchronized Monitor addMonitor(Object b) {
		this.addRole(b);
		if (this.monitor == null)
			this.monitor = new Monitor();
		return monitor;
	}

	/**
	 * Deletes monitor.
	 * 
	 * @param b Monitor
	 */
	synchronized void deleteMonitor(Object b) {
		this.removeRole(b);
		if (this.roles.size() == 0)
			this.monitor = null;
	}

	/**
	 * Sets a new Id conversation, this conversation is considered active.
	 * @param agentID
	 */
	synchronized void setActiveConversation(String agentID) {
		this.activeConversationsList.add(agentID);

	}

	/**
	 * Removes a agentID of the array of the active conversations.
	 * 
	 * @param agentID
	 */
	synchronized void deleteActiveConversation(String agentID) {
		for (String conv : this.activeConversationsList) {
			if (conv.equals(agentID)) {
				this.activeConversationsList.remove(agentID);
				break;
			}
		}
	}

	/**
	 * Removes all active conversations.
	 * 
	 * @return boolean value
	 */
	synchronized boolean deleteAllActiveConversation() {
		this.activeConversationsList.clear();
		if (this.activeConversationsList.size() == 0)
			return true;
		else
			return false;

	}

	/**
	 * Returns the monitor
	 * 
	 * @return Monitor
	 */
	synchronized Monitor getMonitor() {
		return this.monitor;
	}

	/**
	 * When agent state is terminated
	 */
	protected void terminate() {

		if (this.getnRole() == 0)
			logger.info("Finish ,active roles do not exist");
		else {
			for (Object obj : this.roles) {

				String patron;

				patron = obj.getClass().getSuperclass().getName().substring(
						obj.getClass().getSuperclass().getName().lastIndexOf(
						".") + 1,
						obj.getClass().getSuperclass().getName().length());

				if (patron.equals("FIPARequestInitiator"))

				{
					((FIPARequestInitiator) obj).finish();
					logger.info("Finish with role Resquest Initiator, state:  "
							+ ((FIPARequestInitiator) obj).getState());

				} else if (patron.equals("FIPARequestResponder")) {

					((FIPARequestResponder) obj).finish();
					logger
					.info("Finish with role Responder, protocol:Request, state:  "
							+ ((FIPARequestResponder) obj).getState());
				}
				if (patron.equals("FIPAQueryInitiator")) {

					logger
					.info("Finish with role Initiator, protocol:Query, state:  "
							+ ((FIPAQueryInitiator) obj).getState());
				} else if (patron.equals("FIPAQueryResponder")) {

					logger
					.info("Finish with role Responder, protocol:Query, state:  "
							+ ((FIPAQueryResponder) obj).getState());
				}
				if (patron.equals("FIPAContractNetInitiator")) {

					logger
					.info("Finish with role Initiator, protocol:Contract-Net state:  "
							+ ((FIPAContractNetInitiator) obj)
							.getState());
				} else if (patron.equals("FIPAContractNetResponder")) {

					logger
					.info("Finish with role Responder, protocol:Contract-Net state:  "
							+ ((FIPAContractNetResponder) obj)
							.getState());
				}
			}
		}

		super.terminate();

	}

	/**
	 * Adds a new task (FIPA protocol) to the agent,was creating a new thread
	 * 
	 * @param obj
	 *            object of type FIPA protocol. ej: FIPARequestInitiator,
	 *            FIPARequestResponder, FIPAQueryResponder, FIPAQueryInitiator,
	 *            FIPAContractNetResponder, FIPAContractNetInitiator.
	 */
	public void addTask(Object obj) {

		String patron;

		patron = obj.getClass().getSuperclass().getName().substring(
				obj.getClass().getSuperclass().getName().lastIndexOf(".") + 1,
				obj.getClass().getSuperclass().getName().length());
		if (patron.equals("FIPARequestInitiator"))

		{

			ThreadInitiator h = new ThreadInitiator(obj, 1);
			h.start();

		} else if (patron.equals("FIPARequestResponder")) {

			ThreadResponder h = new ThreadResponder(obj, 1);
			h.start();
		}
		if (patron.equals("FIPAQueryInitiator")) {

			ThreadInitiator h = new ThreadInitiator(obj, 2);
			h.start();
		} else if (patron.equals("FIPAQueryResponder")) {

			ThreadResponder h = new ThreadResponder(obj, 2);
			h.start();
		}
		if (patron.equals("FIPAContractNetInitiator")) {

			ThreadInitiator h = new ThreadInitiator(obj, 3);
			h.start();
		} else if (patron.equals("FIPAContractNetResponder")) {

			ThreadResponder h = new ThreadResponder(obj, 3);
			h.start();
		}

	}

	/**
	 * This class adds a new thread with a request initiator conversation protocol. 
	 * 
	 *
	 */
	private class ThreadInitiator extends Thread {

		Object iniciador;
		int tipo;

		public ThreadInitiator(Object in, int tipo) {
			iniciador = in;
			this.tipo = tipo;
		}

		public void run() {

			switch (tipo) {
			case 1: {
				do {
					((FIPARequestInitiator) iniciador).action();
				} while (!((FIPARequestInitiator) iniciador).finished());
				break;
			}
			case 2: {
				do {
					((FIPAQueryInitiator) iniciador).action();
				} while (!((FIPAQueryInitiator) iniciador).finished());
				break;
			}
			case 3: {
				do {
					((FIPAContractNetInitiator) iniciador).action();
				} while (!((FIPAContractNetInitiator) iniciador).finished());
				break;
			}
			}
		}
	}

	/**
	 * This class adds a new thread with a request initiator conversation protocol
	 * 
	 *
	 */
	private class ThreadResponder extends Thread {
		Object responder;
		int tipo;

		public ThreadResponder(Object res, int tipo) {
			responder = res;
			this.tipo = tipo;
		}

		public void run() {
			switch (tipo) {
			case 1: {
				do {
					((FIPARequestResponder) responder).action();
				} while (((FIPARequestResponder) responder).getState() != -1);
				break;

			}
			case 2: {
				do {
					((FIPAQueryResponder) responder).action();
				} while (((FIPAQueryResponder) responder).getState() != -1);
				break;

			}
			case 3: {
				do {
					((FIPAContractNetResponder) responder).action();
				} while (true);
			}

			}
		}
	}

}
