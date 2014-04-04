package TestCAgents.Agents;

import java.util.concurrent.CountDownLatch;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_RECRUITING_Initiator;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;

public class HarryRecruitingInitiatorClass extends CAgent {

	//Variables for testing
	public String informMsg;
	public String refuseMsg;
	private CountDownLatch finished;
	public String agreeMsg;
	public String receivedMsgFromProxy;
	
	
	public HarryRecruitingInitiatorClass(AgentID aid, CountDownLatch finished) throws Exception {
		super(aid);
		this.finished = finished;
		informMsg = "";
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		ACLMessage msg;

		System.out.println(myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent());
		
		
		
		//CAMBIAR RESUMEN
		// Each agent's conversation is carried out by a CProcessor.
		// CProcessors are created by the CFactories in response
		// to messages that start the agent's activity in a conversation

		// An easy way to create CFactories is to create them from the 
		// predefined factories of package es.upv.dsi.gri_ia.cAgents.protocols
		// Another option, not shown in this example, is that the agent
		// designs her own factory and, therefore, a new interaction protocol

		// In this example the agent is going to act as the initiator in the
		// REQUEST protocol defined by FIPA.
		// In order to do so, she has to extend the class FIPA_REQUEST_Initiator
		// implementing the method that receives results of the request (doInform)
		

		class myFIPA_RECRUITING extends FIPA_RECRUITING_Initiator{
			
			protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
				myProcessor.getInternalData().put("InitialMessage", msg);
			}

			
			
			/**
			 * Method executed when the initiator receives a refuse message
			 * @param myProcessor the CProcessor managing the conversation
			 * @param msg refuse message
			 */
			protected void doReceiveRefuse(CProcessor myProcessor, ACLMessage msg) {
				System.out.println("Ok Sally :(");
				refuseMsg = msg.getContent();
			}
			
			/**
			 * Method executed when the initiator receives an agree message
			 * @param myProcessor the CProcessor managing the conversation
			 * @param msg agree message
			 */
			protected void doReceiveAgree(CProcessor myProcessor, ACLMessage msg) {
				System.out.println("I'm waiting for response...");
				agreeMsg = msg.getContent();
				
			}

						
			/**
			 * Method executed when the initiator receives a proxy failure message
			 * @param myProcessor the CProcessor managing the conversation
			 * @param msg proxy failure message
			 */
			protected void doReceiveFailureProxy(CProcessor myProcessor, ACLMessage msg) {
				System.out.println("Proxy action failed");
			}
			
			/**
			 * Method executed when the initiator receives a no match message
			 * @param mmyProcessor the CProcessor managing the conversation
			 * @param msg no match message
			 */
			protected void doReceiveFailureNoMatch(CProcessor myProcessor, ACLMessage msg) {
				System.out.println("No agent match found");
			}

						
			/**
			 * Method executed when the initiator receives an inform message
			 * @param myProcessor the CProcessor managing the conversation
			 * @param msg inform message
			 */
			protected void doReceiveInform(CProcessor myProcessor, ACLMessage msg) {
				System.out.println("Proxy worked");
			}
			
			/**
			 * Method executed when the conversation ends
			 * @param myProcessor the CProcessor managing the conversation
			 * @param messageToSend final message
			 */
			protected void doFinalRecruitingInitiator(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend = myProcessor.getLastSentMessage();
			}

			@Override
			protected void setProxyMessage(CProcessor myProcessor,
					ACLMessage messageToSend) {	
					messageToSend.setProtocol("fipa-recruiting");
					messageToSend.setPerformative(ACLMessage.PROPOSE);
					messageToSend.setReceiver(new AgentID("Sally"));
					messageToSend.setSender(myProcessor.getMyAgent()
							.getAid());
					messageToSend.setContent("Is anyone there?");

				
			}
			
		}
		
		// We create the message that will be sent in the doRequest method
		// of the conversation

		msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setReceiver(new AgentID("Sally"));
		msg.setContent("May you give me your phone number?");
		msg.setProtocol("fipa-recruiting");
		msg.setSender(getAid());
		
		// The agent creates the CFactory that creates processors that initiate
		// REQUEST protocol conversations. In this
		// example the CFactory gets the name "TALK", we don't add any
		// additional message acceptance criterion other than the required
		// by the REQUEST protocol (null) and we do not limit the number of simultaneous
		// processors (value 0)
		
		CFactory recruiting = new myFIPA_RECRUITING().newFactory("RECRUITING", null , msg,1, this, 0);

		// The factory is setup to answer start conversation requests from the agent
		// using the REQUEST protocol.

		this.addFactoryAsInitiator(recruiting);

		// finally the new conversation starts. Because it is synchronous, 
		// the current interaction halts until the new conversation ends.
		//myProcessor.createSyncConversation(msg);
		this.startSyncConversation("RECRUITING");

		MessageFilter filter;
		ACLMessage template;

		// We create a factory in order to manage propositions

		filter = new MessageFilter("performative = PROPOSE");

		CFactory talk = new CFactory("TALK", filter, 1,
				this);

		// A CProcessor always starts in the predefined state BEGIN.
		// We have to associate this state with a method that will be
		// executed at the beginning of the conversation.

		///////////////////////////////////////////////////////////////////////////////
		// BEGIN state

		BeginState BEGIN = (BeginState) talk.cProcessorTemplate().getState(
				"BEGIN");

		class BEGIN_Method implements BeginStateMethod {
			public String run(CProcessor myProcessor, ACLMessage msg) {
				// In this example there is nothing more to do than continue
				// to the next state which will send the answer.
				return "WAIT";
			};
		}
		
		BEGIN.setMethod(new BEGIN_Method());
		
		talk.cProcessorTemplate().registerState(new WaitState("WAIT", 0));
		talk.cProcessorTemplate().addTransition("BEGIN", "WAIT");
		
		class GETMESSAGE_Method implements ReceiveStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageReceived) {
				System.out.println("Getting message");
				receivedMsgFromProxy = messageReceived.getPerformative()+": "+messageReceived.getContent();
				return "REFUSE";
			}
		}
		
		ReceiveState GETMESSAGE = new ReceiveState("GETMESSAGE");
		GETMESSAGE.setMethod(new GETMESSAGE_Method());
		filter = new MessageFilter("performative = PROPOSE");
		GETMESSAGE.setAcceptFilter(filter);
		talk.cProcessorTemplate().registerState(GETMESSAGE);
		talk.cProcessorTemplate().addTransition("WAIT", "GETMESSAGE");
		
		
		///////////////////////////////////////////////////////////////////////////////
		// REFUSE state

		SendState REFUSE = new SendState("REFUSE");

		class REFUSE_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {

				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
				messageToSend.setContent("Maybe someday");
				return "FINAL";
			}
		}
		
		REFUSE.setMethod(new REFUSE_Method());
		
		template = new ACLMessage(ACLMessage.REFUSE);
		REFUSE.setMessageTemplate(template);

		talk.cProcessorTemplate().registerState(REFUSE);
		talk.cProcessorTemplate().addTransition(GETMESSAGE, REFUSE);

		///////////////////////////////////////////////////////////////////////////////
		// FINAL state

		FinalState FINAL = new FinalState("FINAL");

		class FINAL_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.setContent("Done");
				//myProcessor.getMyAgent().Shutdown();
			}
		}
		FINAL.setMethod(new FINAL_Method());

		talk.cProcessorTemplate().registerState(FINAL);
		talk.cProcessorTemplate().addTransition("REFUSE", "FINAL");

		// The template processor is ready. We activate the factory
		// as participant. Every message that arrives to the agent
		// with the performative set to PURPOSE will make the factory
		// TALK to create a processor in order to manage the conversation.
		this.addFactoryAsParticipant(talk);
		
		
		
	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		finished.countDown();
	}
}