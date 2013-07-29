package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

/**
 * Methods to interact with the trace manager in order to publish and unpublish
 * tracing services, to subscribe and unsubscribe to/from tracing services and
 * to list available tracing entities and services.
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
 * @author Jose Vicente Ruiz Cepeda (jruiz1@dsic.upv.es)
 */
public class TraceInteract {

	/* Constants */
	private static final char SEPARATION_CHAR = '#';
	private static final String PUBLISH_LABEL = "publish";
	private static final String UNPUBLISH_LABEL = "unpublish";
	private static final String ANY_LABEL = "any";
	private static final String LIST_LABEL = "list";
	private static final String SERVICES_LABEL = "services";
	private static final String ENTITIES_LABEL = "entities";
	private static final String ALL_LABEL = "all";

	private static final String ACL_LABEL = "ACL";
	private static final String DEFAULT_TM_NAME = "qpid://TM@localhost:8080";
	private static final AgentID DEFAULT_TM_AID = new AgentID(DEFAULT_TM_NAME);

	/**
	 * Send an ACL message with the given performative, body and receiver. The
	 * sender agent will be the applicant agent.
	 * 
	 * @param originAgent
	 *            Agent which wants to communicate through this message.
	 * @param performative
	 *            Integer with the ACL performative to be used.
	 * @param receiverAgent
	 *            Agent which will receive the message.
	 * @param body
	 *            Content of the message.
	 */
	private static void sendACLMessage(BaseAgent originAgent, int performative,
			AgentID receiverAgent, String body) {
		AgentID senderAgent = originAgent.getAid();

		// Building an ACL message.
		ACLMessage msg = new ACLMessage(performative);
		msg.setLanguage(ACL_LABEL);
		msg.setSender(senderAgent);
		msg.setReceiver(receiverAgent);
		msg.setContent(body);

		// Sending the ACL message.
		originAgent.send(msg);
	}

	/**
	 * Publish a tracing service so that other agents can request it and receive
	 * the corresponding trace events.
	 * <p>
	 * 
	 * The applicant agent must specify a name for the tracing service, the
	 * corresponding type of the trace events which will be provided by the
	 * service and a description of the tracing service.
	 * <p>
	 * 
	 * This method communicates via ACL with a trace manager entity in localhost
	 * ("qpid://TM@localhost:8080"). To interact with a different trace manager
	 * entity, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#publishTracingService(AgentID tms_aid, BaseAgent applicantAgent, String serviceName, String description)}
	 * 
	 * @param applicantAgent
	 *            Agent which wants to publish the tracing service
	 * @param serviceName
	 *            Name of the tracing service which is to be published
	 * @param description
	 *            Description of the tracing service to be published
	 * 
	 * @see es.upv.dsic.gti_ia.core.TracingService
	 */
	public static void publishTracingService(BaseAgent applicantAgent,
			String serviceName, String description) {
		String body = PUBLISH_LABEL + SEPARATION_CHAR + serviceName.length()
				+ SEPARATION_CHAR + serviceName + description;
		sendACLMessage(applicantAgent, ACLMessage.REQUEST, DEFAULT_TM_AID, body);
	}

	/**
	 * Publish a tracing service so that other agents can request it and receive
	 * the corresponding trace events.
	 * <p>
	 * 
	 * The applicant agent must specify a name for the tracing service, the
	 * corresponding type of the trace events which will be provided by the
	 * service and a description of the tracing service.
	 * <p>
	 * 
	 * This method communicates via ACL with the trace manager entity specified
	 * by the parameter tms_aid. To interact with the default trace manager
	 * entity in localhost ("qpid://TM@localhost:8080"), use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#publishTracingService(BaseAgent applicantAgent, String serviceName, String description)}
	 * 
	 * @param tms_aid
	 *            AgentID of the trace manager entity which is being sent the
	 *            publication request
	 * @param applicantAgent
	 *            Agent which wants to publish the tracing service
	 * @param serviceName
	 *            Name of the tracing service which is to be published
	 * @param description
	 *            Description of the tracing service to be published
	 * 
	 * @see es.upv.dsic.gti_ia.core.TracingService
	 * @see es.upv.dsic.gti_ia.core.AgentID
	 */
	public static void publishTracingService(AgentID tms_aid,
			BaseAgent applicantAgent, String serviceName, String description) {
		String body = PUBLISH_LABEL + SEPARATION_CHAR + serviceName.length()
				+ SEPARATION_CHAR + serviceName + description;
		;
		sendACLMessage(applicantAgent, ACLMessage.REQUEST, tms_aid, body);
	}

	/**
	 * Unpublish a tracing service.
	 * <p>
	 * 
	 * Unpublish a previously published tracing service so that other agents
	 * cannot subscribe nor receive the corresponding trace events from the
	 * applicantAgent (if there were more providers of the tracing service,
	 * subscribers would still be able to receive trace events of that tracing
	 * service generated by these other providers.
	 * <p>
	 * 
	 * This method communicates via ACL with a trace manager entity in localhost
	 * ("qpid://TM@localhost:8080"). To interact with a different trace manager
	 * entity, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#unpublishTracingService(AgentID tms_aid, BaseAgent applicantAgent, String serviceName)}
	 * 
	 * @param applicantAgent
	 *            Agent which wants to unpublish the tracing service
	 * @param serviceName
	 *            Name of the tracing service which is to be unpublished
	 */
	public static void unpublishTracingService(BaseAgent applicantAgent,
			String serviceName) {
		String body = UNPUBLISH_LABEL + SEPARATION_CHAR + serviceName;
		sendACLMessage(applicantAgent, ACLMessage.REQUEST, DEFAULT_TM_AID, body);
	}

	/**
	 * Unpublish a tracing service.
	 * <p>
	 * 
	 * Unpublish a previously published tracing service so that other agents
	 * cannot subscribe nor receive the corresponding trace events from the
	 * applicantAgent (if there were more providers of the tracing service,
	 * subscribers would still be able to receive trace events of that tracing
	 * service generated by these other providers.
	 * <p>
	 * 
	 * This method communicates via ACL with the trace manager entity specified
	 * by the parameter tms_aid. To interact with the default trace manager
	 * entity in localhost ("qpid://TM@localhost:8080"), use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#unpublishTracingService(BaseAgent applicantAgent, String serviceName)}
	 * 
	 * @param tms_aid
	 *            AgentID of the trace manager entity which is being sent the
	 *            unpublication request
	 * @param applicantAgent
	 *            Agent which wants to unpublish the tracing service
	 * @param serviceName
	 *            Name of the tracing service which is to be unpublished
	 */
	public static void unpublishTracingService(AgentID tms_aid,
			BaseAgent applicantAgent, String serviceName) {
		String body = UNPUBLISH_LABEL + SEPARATION_CHAR + serviceName;
		sendACLMessage(applicantAgent, ACLMessage.REQUEST, tms_aid, body);
	}

	/**
	 * Request a tracing service from any tracing entity.
	 * <p>
	 * 
	 * The requester agent must specify a name for the tracing service, the
	 * corresponding type of the trace events which will be provided by the
	 * service and a description of the tracing service.
	 * <p>
	 * 
	 * This method communicates via ACL with a trace manager entity in localhost
	 * ("qpid://TM@localhost:8080"). To interact with a different trace manager
	 * entity, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#requestTracingService(AgentID tms_aid, BaseAgent requesterAgent, String serviceName)}
	 * <p>
	 * 
	 * This method requests a tracing service from any tracing entity which
	 * provides it. To request a tracing service from a specific tracing entity
	 * and receive only the trace events of the corresponding tracing service
	 * generated by that entity in the system, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#requestTracingService(BaseAgent requesterAgent, String serviceName, AgentID originEntity)}
	 * 
	 * @param requesterAgent
	 *            Agent which wants to request the tracing service
	 * @param serviceName
	 *            Name of the tracing service which is to be requested
	 * 
	 * @see es.upv.dsic.gti_ia.core.TracingService
	 */
	public static void requestTracingService(BaseAgent requesterAgent,
			String serviceName) {
		/*
		 * If the origin entity has not been specified, i.e., any entity is
		 * valid, so use "any" label.
		 */
		String body = serviceName + SEPARATION_CHAR + ANY_LABEL;
		sendACLMessage(requesterAgent, ACLMessage.SUBSCRIBE, DEFAULT_TM_AID,
				body);
	}

	/**
	 * Request a tracing service from a specific tracing entity.
	 * <p>
	 * 
	 * The requester agent must specify a name for the tracing service, the
	 * corresponding type of the trace events which will be provided by the
	 * service and a description of the tracing service.
	 * <p>
	 * 
	 * This method communicates via ACL with a trace manager entity in localhost
	 * ("qpid://TM@localhost:8080"). To interact with a different trace manager
	 * entity, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#requestTracingService(AgentID tms_aid, BaseAgent requesterAgent, String serviceName, AgentID originEntity)}
	 * <p>
	 * 
	 * This method requests a tracing service provided by a specific tracing
	 * entity. To request a tracing service from any tracing entity and receive
	 * the trace events of the corresponding tracing service from any entity in
	 * the system which offers that tracing service, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#requestTracingService(BaseAgent requesterAgent, String serviceName)}
	 * 
	 * @param requesterAgent
	 *            Agent which wants to request the tracing service
	 * @param serviceName
	 * 
	 *            Name of the tracing service which is to be requested
	 * @param originEntity
	 *            AgentID of the tracing entity whose tracing service is
	 *            requested
	 * 
	 * @see es.upv.dsic.gti_ia.core.TracingService
	 */
	public static void requestTracingService(BaseAgent requesterAgent,
			String serviceName, AgentID originEntity) {
		String body = serviceName + SEPARATION_CHAR + originEntity.toString();
		sendACLMessage(requesterAgent, ACLMessage.SUBSCRIBE, DEFAULT_TM_AID,
				body);
	}

	/**
	 * Request a tracing service from any tracing entity.
	 * <p>
	 * 
	 * The requester agent must specify a name for the tracing service, the
	 * corresponding type of the trace events which will be provided by the
	 * service and a description of the tracing service.
	 * <p>
	 * 
	 * This method communicates via ACL with the trace manager entity specified
	 * by the parameter tms_aid. To interact with the default trace manager
	 * entity in localhost ("qpid://TM@localhost:8080"), use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#requestTracingService(AgentID tms_aid, BaseAgent requesterAgent, String serviceName)}
	 * <p>
	 * 
	 * This method requests a tracing service to receive trace events coming
	 * from any tracing entity which provides it. To request a tracing service a
	 * specific tracing entity and receive only the trace events which that
	 * tracing entity generates, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#requestTracingService(BaseAgent requesterAgent, String serviceName, AgentID originEntity)}
	 * 
	 * @param tms_aid
	 *            AgentID of the trace manager entity which is being sent the
	 *            request
	 * @param requesterAgent
	 *            Agent which wants to request the tracing service
	 * @param serviceName
	 *            Name of the tracing service which is to be requested
	 * 
	 * @see es.upv.dsic.gti_ia.core.TracingService
	 */
	public static void requestTracingService(AgentID tms_aid,
			BaseAgent requesterAgent, String serviceName) {
		/*
		 * If the origin entity has not been specified, i.e., any entity is
		 * valid, so use "any" label.
		 */
		String body = serviceName + SEPARATION_CHAR + ANY_LABEL;
		sendACLMessage(requesterAgent, ACLMessage.SUBSCRIBE, tms_aid, body);
	}

	/**
	 * Request a tracing service from a specific tracing entity.
	 * <p>
	 * 
	 * The requester agent must specify a name for the tracing service, the
	 * corresponding type of the trace events which will be provided by the
	 * service and a description of the tracing service.
	 * <p>
	 * 
	 * This method communicates via ACL with the trace manager entity specified
	 * by the parameter tms_aid. To interact with the default trace manager
	 * entity in localhost ("qpid://TM@localhost:8080"), use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#requestTracingService(BaseAgent requesterAgent, String serviceName, AgentID originEntity)}
	 * <p>
	 * 
	 * This method requests a tracing service provided by a specific tracing
	 * entity. To request a tracing service from any tracing entity and receive
	 * the trace events of the corresponding tracing service from any entity in
	 * the system which offers that tracing service, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#requestTracingService(AgentID tms_aid, BaseAgent requesterAgent, String serviceName, AgentID originEntity)}
	 * 
	 * @param tms_aid
	 *            AgentID of the trace manager entity which is being sent the
	 *            request
	 * @param requesterAgent
	 *            Agent which wants to request the tracing service
	 * @param serviceName
	 *            Name of the tracing service which is to be requested
	 * @param originEntity
	 *            AgentID of the tracing entity whose tracing service is
	 *            requested
	 * 
	 * @see es.upv.dsic.gti_ia.core.TracingService
	 */
	public static void requestTracingService(AgentID tms_aid,
			BaseAgent requesterAgent, String serviceName, AgentID originEntity) {
		String body = serviceName + SEPARATION_CHAR + originEntity.toString();
		sendACLMessage(requesterAgent, ACLMessage.SUBSCRIBE, tms_aid, body);
	}

	/**
	 * Request all tracing services available at the time.
	 * <p>
	 * 
	 * This method communicates via ACL with a trace manager entity in localhost
	 * ("qpid://TM@localhost:8080"). To interact with a different trace manager
	 * entity, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#requestAllTracingServices(AgentID tms_aid, BaseAgent requesterAgent)}
	 * 
	 * @param requesterAgent
	 *            Agent which wants to request tracing services
	 */
	public static void requestAllTracingServices(BaseAgent requesterAgent) {
		String body = ALL_LABEL;
		sendACLMessage(requesterAgent, ACLMessage.SUBSCRIBE, DEFAULT_TM_AID,
				body);
	}

	/**
	 * Request all tracing services available at the time.
	 * <p>
	 * 
	 * This method communicates via ACL with the trace manager entity specified
	 * by the parameter tms_aid. To interact with the default trace manager
	 * entity in localhost ("qpid://TM@localhost:8080"), use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#requestAllTracingServices(BaseAgent requesterAgent)}
	 * 
	 * @param tms_aid
	 *            AgentID of the trace manager entity which is being sent the
	 *            request
	 * @param requesterAgent
	 *            Agent which wants to request tracing services
	 */
	public static void requestAllTracingServices(AgentID tms_aid,
			BaseAgent requesterAgent) {
		String body = ALL_LABEL;
		sendACLMessage(requesterAgent, ACLMessage.SUBSCRIBE, tms_aid, body);
	}

	/**
	 * Cancel subscription to a tracing service from any tracing entity.
	 * <p>
	 * 
	 * This method communicates via ACL with a trace manager entity in localhost
	 * ("qpid://TM@localhost:8080"). To interact with a different trace manager
	 * entity, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#cancelTracingServiceSubscription(AgentID tms_aid, BaseAgent requesterAgent, String serviceName)}
	 * <p>
	 * 
	 * This method cancels the subscription to a tracing service from a specific
	 * tracing entity. To cancel a subscription to any entity, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#cancelTracingServiceSubscription(BaseAgent requesterAgent, String serviceName, AgentID originEntity)}
	 * 
	 * @param requesterAgent
	 *            Agent which wants to cancel its subscription
	 * @param serviceName
	 *            Name of the tracing service to which the subscription was made
	 */
	public static void cancelTracingServiceSubscription(
			BaseAgent requesterAgent, String serviceName) {
		/*
		 * If the origin entity has not been specified, i.e., any entity is
		 * valid, so use "any" label.
		 */
		String body = serviceName + SEPARATION_CHAR + ANY_LABEL;
		sendACLMessage(requesterAgent, ACLMessage.CANCEL, DEFAULT_TM_AID, body);
	}

	/**
	 * Cancel subscription to a tracing service from a specific tracing entity.
	 * <p>
	 * 
	 * This method communicates via ACL with a trace manager entity in localhost
	 * ("qpid://TM@localhost:8080"). To interact with a different trace manager
	 * entity, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#cancelTracingServiceSubscription(AgentID tms_aid, BaseAgent requesterAgent, String serviceName, AgentID originEntity)}
	 * <p>
	 * 
	 * This method cancels the subscription to a tracing service from a specific
	 * tracing entity. To cancel a subscription to any entity, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#cancelTracingServiceSubscription(BaseAgent requesterAgent, String serviceName)}
	 * 
	 * @param requesterAgent
	 *            Agent which wants to cancel its subscription
	 * @param serviceName
	 *            Name of the tracing service to which the subscription was made
	 * @param originEntity
	 *            AgentID of the tracing entity to which the subscription
	 *            referred to
	 */
	public static void cancelTracingServiceSubscription(
			BaseAgent requesterAgent, String serviceName, AgentID originEntity) {
		String body = serviceName + SEPARATION_CHAR + originEntity.toString();
		sendACLMessage(requesterAgent, ACLMessage.CANCEL, DEFAULT_TM_AID, body);
	}

	/**
	 * Cancel subscription to a tracing service from any tracing entity.
	 * <p>
	 * 
	 * This method communicates via ACL with the trace manager entity specified
	 * by the parameter tms_aid. To interact with the default trace manager
	 * entity in localhost ("qpid://TM@localhost:8080"), use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#cancelTracingServiceSubscription(BaseAgent requesterAgent, String serviceName)}
	 * <p>
	 * 
	 * This method cancels the subscription to a tracing service from a specific
	 * tracing entity. To cancel a subscription to any entity, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#cancelTracingServiceSubscription(AgentID tms_aid, BaseAgent requesterAgent, String serviceName, AgentID originEntity)}
	 * 
	 * @param tms_aid
	 *            AgentID of the trace manager entity which is being sent the
	 *            request
	 * @param requesterAgent
	 *            Agent which wants to cancel its subscription
	 * @param serviceName
	 *            Name of the tracing service to which the subscription was made
	 */
	public static void cancelTracingServiceSubscription(AgentID tms_aid,
			BaseAgent requesterAgent, String serviceName) {
		/*
		 * If the origin entity has not been specified, i.e., any entity is
		 * valid, so use "any" label.
		 */
		String body = serviceName + SEPARATION_CHAR + ANY_LABEL;
		sendACLMessage(requesterAgent, ACLMessage.CANCEL, tms_aid, body);
	}

	/**
	 * Cancel subscription to a tracing service from a specific tracing entity.
	 * <p>
	 * 
	 * This method communicates via ACL with the trace manager entity specified
	 * by the parameter tms_aid. To interact with the default trace manager
	 * entity in localhost ("qpid://TM@localhost:8080"), use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#cancelTracingServiceSubscription(BaseAgent requesterAgent, String serviceName, AgentID originEntity)}
	 * <p>
	 * 
	 * This method cancels the subscription to a tracing service from a specific
	 * tracing entity. To cancel a subscription to any entity, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#cancelTracingServiceSubscription(AgentID tms_aid, BaseAgent requesterAgent, String serviceName)}
	 * 
	 * @param tms_aid
	 *            AgentID of the trace manager entity which is being sent the
	 *            request
	 * @param requesterAgent
	 *            Agent which wants to cancel its subscription
	 * @param serviceName
	 *            Name of the tracing service to which the subscription was made
	 * @param originEntity
	 *            AgentID of the tracing entity to which the subscription
	 *            referred to
	 */
	public static void cancelTracingServiceSubscription(AgentID tms_aid,
			BaseAgent requesterAgent, String serviceName, AgentID originEntity) {
		String body = serviceName + SEPARATION_CHAR + originEntity.toString();
		sendACLMessage(requesterAgent, ACLMessage.CANCEL, tms_aid, body);
	}

	/**
	 * Request a list of registered tracing entities.
	 * <p>
	 * 
	 * This method communicates via ACL with a trace manager entity in localhost
	 * ("qpid://TM@localhost:8080"). To interact with a different trace manager
	 * entity, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#listTracingEntities(AgentID tms_aid, BaseAgent applicantAgent)}
	 * 
	 * @param applicantAgent
	 *            Agent which wants to receive the list of tracing entities
	 */
	public static void listTracingEntities(BaseAgent applicantAgent) {
		String body = LIST_LABEL + SEPARATION_CHAR + ENTITIES_LABEL;
		sendACLMessage(applicantAgent, ACLMessage.REQUEST, DEFAULT_TM_AID, body);
	}

	/**
	 * Request a list of registered tracing entities to a specific trace manager
	 * entity.
	 * <p>
	 * 
	 * This method communicates via ACL with the trace manager entity specified
	 * by the parameter tms_aid. To interact with the default trace manager
	 * entity in localhost ("qpid://TM@localhost:8080"), use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#listTracingEntities(BaseAgent applicantAgent)}
	 * 
	 * @param tms_aid
	 *            AgentID of the trace manager entity which is being sent the
	 *            request
	 * @param applicantAgent
	 *            Agent which wants to receive the list of tracing entities
	 */
	public static void listTracingEntities(AgentID tms_aid,
			BaseAgent applicantAgent) {
		String body = LIST_LABEL + SEPARATION_CHAR + ENTITIES_LABEL;
		sendACLMessage(applicantAgent, ACLMessage.REQUEST, tms_aid, body);
	}

	/**
	 * Request a list of registered tracing services.
	 * <p>
	 * 
	 * This method communicates via ACL with a trace manager entity in localhost
	 * ("qpid://TM@localhost:8080"). To interact with a different trace manager
	 * entity, use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#listTracingServices(AgentID tms_aid, BaseAgent applicantAgent)}
	 * 
	 * @param applicantAgent
	 *            Agent which wants to receive the list of tracing services
	 */
	public static void listTracingServices(BaseAgent applicantAgent) {
		String body = LIST_LABEL + SEPARATION_CHAR + SERVICES_LABEL;
		sendACLMessage(applicantAgent, ACLMessage.REQUEST, DEFAULT_TM_AID, body);
	}

	/**
	 * Request a list of registered tracing entities.
	 * <p>
	 * 
	 * This method communicates via ACL with the trace manager entity specified
	 * by the parameter tms_aid. To interact with the default trace manager
	 * entity in localhost ("qpid://TM@localhost:8080"), use the method
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#listTracingServices(BaseAgent applicantAgent)}
	 * 
	 * @param tms_aid
	 *            AgentID of the trace manager entity which is being sent the
	 *            request
	 * @param applicantAgent
	 *            Agent which wants to receive the list of tracing services
	 */
	public static void listTracingServices(AgentID tms_aid,
			BaseAgent applicantAgent) {
		String body = LIST_LABEL + SEPARATION_CHAR + SERVICES_LABEL;
		sendACLMessage(applicantAgent, ACLMessage.REQUEST, tms_aid, body);
	}
}
