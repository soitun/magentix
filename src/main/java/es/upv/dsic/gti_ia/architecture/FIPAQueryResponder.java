package es.upv.dsic.gti_ia.architecture;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * This class implements the FIPA-Query interaction protocol, Role Responder.
 * 
 * @author  Joan Bellver Faus, GTI-IA, DSIC, UPV
 * @author David Fernández Molina, GTI-IA, DSIC, UPV
 * @version 2009.9.07
 */
public class FIPAQueryResponder {

	private final static int WAITING_MSG_STATE = 0;
	private final static int PREPARE_RESPONSE_STATE = 1;
	private final static int SEND_RESPONSE_STATE = 2;
	private final static int PREPARE_RES_NOT_STATE = 3;
	private final static int SEND_RESULT_NOTIFICATION_STATE = 4;
	private final static int RESET_STATE = 5;

	private MessageTemplate template;
	private int state = WAITING_MSG_STATE;
	public QueueAgent myAgent;
	private ACLMessage requestmsg;
	private ACLMessage responsemsg;
	private ACLMessage resNofificationmsg;
	private String name = "";
	private String port = "";

	private Monitor monitor = null;

	/**
	 * Creates a new FIPA-Query interaction protocol, rol responder.
	 * 
	 * @param agent
	 *            is the reference to the Agent Object
	 * @param template
	 *            is a MessageTemplate, will serve as a filter for receiving the right message
	 */

	public FIPAQueryResponder(QueueAgent _agent, MessageTemplate _template) {
		myAgent = _agent;
		template = _template;
		this.monitor = myAgent.addMonitor(this);

	}
	
	 /**
	  * Returns the agent.
	  * @return QueueAgent 
	  */
	 public QueueAgent getQueueAgent()
	 {
		return this.myAgent; 
		 
	 }

	 public int getState() {
		return this.state;
	}

	 /**
	  *  Runs the state machine with the communication protocol
	  */
	public void action() {

		switch (state) {
		case WAITING_MSG_STATE: {
			ACLMessage request = myAgent.receiveACLMessage(template, 1);
			if (request != null) {
				this.requestmsg = request;
				state = PREPARE_RESPONSE_STATE;
			} else {
				monitor.waiting();//waiting to new messages arrive
			}
			break;
		}
		case PREPARE_RESPONSE_STATE: {
			ACLMessage request = this.requestmsg;
			ACLMessage response = null;
			state = SEND_RESPONSE_STATE;
			try {
				response = prepareResponse(request);
			} catch (NotUnderstoodException nue) {
				response = request.createReply();
				response.setContent(nue.getMessage());
				response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			} catch (RefuseException re) {
				response = request.createReply();
				response.setContent(re.getMessage());
				response.setPerformative(ACLMessage.REFUSE);
			}

			this.responsemsg = response;
			break;
		}
		case SEND_RESPONSE_STATE: {
			ACLMessage response = this.responsemsg;

			if (response != null) {

				ACLMessage receivedMsg = this.requestmsg;

				response = arrangeMessage(receivedMsg, response);
				response.setSender(myAgent.getAid());
				//If the message is for a Jade agent

				if (response.getReceiver() != null) {
					if (response.getReceiver(0).protocol.equals("http")) {
						name = response
								.getReceiver()
								.name_all()
								.substring(
										0,
										response
												.getReceiver()
												.name_all()
												.indexOf(
														"@",
														response
																.getReceiver()
																.name_all()
																.indexOf(
																		"@") + 1));
						if (response.getReceiver().port.indexOf(":") != -1)
						{
						 port = response.getReceiver().port
								.substring(response.getReceiver().port
										.indexOf(":") + 1, response
										.getReceiver().port
										.indexOf("/", 10));
						}
						else
						{
							port = response.getReceiver().port
							.substring(0, response
									.getReceiver().port
									.indexOf("/"));
							
						}
					
						
						response.getReceiver().name = name;
						response.getReceiver().port = port;

					}
				}
				myAgent.send(response);

				if (response.getPerformativeInt() == ACLMessage.AGREE)
					state = PREPARE_RES_NOT_STATE;
				else {

					state = RESET_STATE;
				}

			} else {

				state = PREPARE_RES_NOT_STATE;
			}

			break;

		}
		case PREPARE_RES_NOT_STATE: {

			state = SEND_RESULT_NOTIFICATION_STATE;
			ACLMessage request = this.requestmsg;
			ACLMessage response = this.responsemsg;
			ACLMessage resNotification = null;

			try {
				resNotification = prepareResultNotification(request, response);

			

			} catch (FailureException fe) {

				resNotification = request.createReply();

				resNotification.setContent(fe.getMessage());
				resNotification.setPerformative(ACLMessage.FAILURE);
			}

			this.resNofificationmsg = resNotification;
			break;
		}
		case SEND_RESULT_NOTIFICATION_STATE: {
			state = RESET_STATE;
			ACLMessage resNotification = this.resNofificationmsg;
			if (resNotification != null) {

				ACLMessage receiveMsg = arrangeMessage(this.requestmsg,
						resNotification);
				receiveMsg.setSender(myAgent.getAid());
				if (receiveMsg.getReceiver() != null) {
					if (receiveMsg.getReceiver(0).protocol.equals("http")) {

						receiveMsg.getReceiver().name = name;
						receiveMsg.getReceiver().port = port;
					}
				}
				myAgent.send(receiveMsg);
			}

			break;

		}
		case RESET_STATE: {

			state = WAITING_MSG_STATE;
			this.requestmsg = null;
			this.resNofificationmsg = null;
			this.responsemsg = null;
			break;
		}

		}

	}

	private ACLMessage arrangeMessage(ACLMessage request, ACLMessage reply) {
		reply.setConversationId(request.getConversationId());
		reply.setInReplyTo(request.getReplyWith());
		reply.setProtocol(request.getProtocol());
		reply.setReceiver(request.getSender());

		return reply;
	}

	/**
	 * This method is called when the initiator's message is received that
	 * matches the message template passed in the constructor.
	 * 
	 * @param request
	 *            initial message
	 * @return message
	 * @throws NotUnderstoodException
	 * @throws RefuseException
	 */

	protected ACLMessage prepareResponse(ACLMessage request)
			throws NotUnderstoodException, RefuseException {
		return null;
	}

	/**
	 * This method is called after the response has been sent and only when one
	 * of the following two cases arise: the response was an agree message OR no
	 * response message was sent.
	 * 
	 * @param request request message
	 * @param responder responder message
	 * @return message
	 * @throws FailureException
	 */
	protected ACLMessage prepareResultNotification(ACLMessage request,
			ACLMessage responder) throws FailureException {
		return null;
	}

}
