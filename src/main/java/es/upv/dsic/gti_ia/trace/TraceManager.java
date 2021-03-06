package es.upv.dsic.gti_ia.trace;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Level;
import org.apache.qpid.transport.DeliveryProperties;
import org.apache.qpid.transport.Header;
import org.apache.qpid.transport.MessageAcceptMode;
import org.apache.qpid.transport.MessageAcquireMode;
import org.apache.qpid.transport.MessageProperties;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Option;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.core.TracingService;
import es.upv.dsic.gti_ia.organization.Configuration;
import es.upv.dsic.gti_ia.trace.TracingEntityList;
import es.upv.dsic.gti_ia.trace.exception.TraceServiceNotAllowedException;

/**
 * Trace Manager entity definition.
 * 
 * The trace manager entity is an agent in charge of coordinating and managing
 * the event trace process. Tracing entities have to interact with the trace
 * manager through ACL messages in order to publish/unpublish their tracing
 * service and in order to subscribe/unsubscribe from tracing services.
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
 * @author Jose Vicente Ruiz Cepeda (jruiz1@dsic.upv.es)
 */
public class TraceManager extends BaseAgent {
	
	// TODO: normalize everything using constants and share them also with
	// TraceInteract and other related classes...
	
	/**
	 * Constant with the name of the default trace manager.
	 */
	public static final String DEFAULT_TM_NAME = "qpid://TM@localhost:8080";
	
	/**
	 * Constant with the agent id of the default trace manager.
	 */
	public static final AgentID DEFAULT_TM_AID = new AgentID(DEFAULT_TM_NAME);
	
	/**
	 * List of tracing entities present in the system.
	 * 
	 * @see es.upv.dsic.gti-ia.trace.TracingEntityList
	 */
	private TracingEntityList TracingEntities;
	/**
	 * List of tracing entities which provide a tracing service.
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntityList
	 */
	private TracingEntityList TSProviderEntities;
	/**
	 * List of tracing entities which are subscribed to some tracing service
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntityList
	 */
	private TracingEntityList TSSubscriberEntities;
	/**
	 * List of tracing services available in the system.
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceList
	 */
	private TracingServiceList TracingServices;
	
	/**
	 * Flag which determines if the trace manager has been launched in
	 * monitorization mode, which is necessary in order to subscribe to all
	 * available tracing services using
	 * {@link es.upv.dsic.gti_ia.trace.TraceInteract#requestAllTracingServices(BaseAgent requesterAgent)}
	 */
	private boolean monitorizable;
	
	/**
	 * Semaphore whose value determines if the TraceManager must finish its
	 * execution. At the beginning, its value will be 0, but it will be changed
	 * to 1 if the public method
	 * {@link es.upv.dsic.gti_ia.trace.TraceManager#shutdown()} is executed.
	 */
	private Semaphore finishExecution;
	
	/* Bit mask used to manage the trace interactions.
	 *
	 * private TraceMask traceMask;
	 */
	
	/**
	 * Instance of {@link es.upv.dsic.gti_ia.organization.Configuration} class
	 * used to access data contained in file Settings.xml, such as the trace
	 * mask.
	 */
	private static final Configuration conf = Configuration.getConfiguration();
	
	/**
	 * Constructor which creates and initializes a TraceManager with the
	 * monitorization flag set to 'false'.
	 * <p>
	 * 
	 * Initialization tasks are internally performed by invoking the private
	 * method {@link es.upv.dsic.gti_ia.trace.TraceManager#initialize()}. These
	 * tasks are the following:
	 * <p>
	 * 
	 * 1) Creation of empty Tracing Entities, Tracing Service Providers, Tracing
	 * Service Subscribers and Tracing Services lists.
	 * <p>
	 * 2) Add the trace manager to the Tracing Entities List.
	 * <p>
	 * 3) Initialize the Tracing Services list with DI tracing services and add
	 * the trace manager as provider of those tracing services which are
	 * mandatory and requestable.
	 * <p>
	 * 4) Subscribe to NEW_AGENT and AGENT_DESTROYED tracing services in order
	 * to be able to register tracing entities in the system
	 * <p>
	 * 5) Send a system trace event of type WELCOME_TM to inform already
	 * existing agents of the arrival of the trace manager.
	 * 
	 * @see es.upv.dsic.gti_ia.core.TracingService
	 * 
	 * @param aid
	 *            AgentID which will be used to create the agent
	 * 
	 * @throws Exception
	 */
	public TraceManager(AgentID aid) throws Exception {
		this(aid, false);
	}
	
	/**
	 * Constructor which creates and initializes a TraceManager.
	 * <p>
	 * 
	 * Initialization tasks are internally performed by invoking the private
	 * method {@link es.upv.dsic.gti_ia.trace.TraceManager#initialize()}. These
	 * tasks are the following:
	 * <p>
	 * 
	 * 1) Creation of empty Tracing Entities, Tracing Service Providers, Tracing
	 * Service Subscribers and Tracing Services lists.
	 * <p>
	 * 2) Add the trace manager to the Tracing Entities List.
	 * <p>
	 * 3) Initialize the Tracing Services list with DI tracing services and add
	 * the trace manager as provider of those tracing services which are
	 * mandatory and requestable.
	 * <p>
	 * 4) Subscribe to NEW_AGENT and AGENT_DESTROYED tracing services in order
	 * to be able to register tracing entities in the system
	 * <p>
	 * 5) Send a system trace event of type WELCOME_TM to inform already
	 * existing agents of the arrival of the trace manager.
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TraceManager#initialize()
	 * 
	 * @param aid
	 *            AgentID which will be used to create the agent
	 * @param monitorizable
	 *            Value to which the monitorizable attribute of the class will
	 *            be set
	 * 
	 * @throws Exception
	 */
	public TraceManager(AgentID aid, boolean monitorizable) throws Exception {
		super(aid);
		
		this.monitorizable = monitorizable;
		
		this.finishExecution = new Semaphore(0);
		
		if (monitorizable) {
			logger.info("[TRACE MANAGER]: " + this.getAid().toString()
					+ " launched with monitorization...");
		} else {
			logger.info("[TRACE MANAGER]: " + this.getAid().toString()
					+ " launched...");
		}
		
		logger.setLevel(Level.OFF);
		
		/* Obtain the trace mask from the configuration class */
		this.traceMask = new TraceMask(conf.getTraceMask());
		
		initialize();
	}
	
	/**
	 * Returns the current trace mask of the system.
	 * 
	 * @return the traceMask
	 */
	public TraceMask getTraceMask() {
		return traceMask.clone();
	}
	
	/**
	 * Set a new trace mask in the system.
	 * 
	 * @param traceMask
	 *            the trace mask to set
	 */
	public void setTraceMask(TraceMask traceMask) {
		this.traceMask = traceMask.clone();
		
		// Expand the new trace mask to other agents.
		TraceEvent newMaskEvent = new TraceEvent(
				TracingService.DI_TracingServices[TracingService.NEW_MASK]
						.getName(),
				new AgentID(SYSTEM_NAME, this.getAid().protocol,
						this.getAid().host, this.getAid().port), traceMask
						.toString());
		this.sendSystemTraceEvent(newMaskEvent, null);
	}
	
	/**
	 * Initializes the TraceManager.
	 * <p>
	 * 
	 * Initialization tasks are the following:
	 * <p>
	 * 
	 * 1) Creation of empty Tracing Entities, Tracing Service Providers, Tracing
	 * Service Subscribers and Tracing Services lists.
	 * <p>
	 * 2) Add the trace manager to the Tracing Entities List.
	 * <p>
	 * 3) Initialize the Tracing Services list with DI tracing services and add
	 * the trace manager as provider of those tracing services which are
	 * mandatory and requestable.
	 * <p>
	 * 4) Subscribe to NEW_AGENT and AGENT_DESTROYED tracing services in order
	 * to be able to register tracing entities in the system.
	 * <p>
	 * 5) Send a system trace event of type WELCOME_TM to inform already
	 * existing agents of the arrival of the trace manager.
	 */
	private void initialize() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		TracingEntities = new TracingEntityList();
		TSProviderEntities = new TracingEntityList();
		TSSubscriberEntities = new TracingEntityList();
		TracingServices = new TracingServiceList();
		TracingEntity tEntity;
		TracingService tService;
		
		// Add Trace Manager to the tracing entities list
		tEntity = new TracingEntity(TracingEntity.AGENT, this.getAid());
		synchronized (TracingEntities) {
			TracingEntities.add(tEntity);
		}
		
		if (!TracingServices.initializeWithDITracingServices()) {
			logger.error("[TRACE MANAGER]: Error while initializing the tracing service list");
		}
		
		// Add as provider of those tracing services which are mandatory and requestable.
		for (int i = 0; i < TracingService.MAX_DI_TS; i++) {
			if (TracingService.DI_TracingServices[i].getRequestable()) {
				tService = TracingServices
						.getTS(TracingService.DI_TracingServices[i].getName());
				synchronized (tEntity.getPublishedTS()) {
					tEntity.getPublishedTS().add(tService);
				}
				synchronized (tService.getProviders()) {
					tService.getProviders().add(tEntity);
				}
			}
		}
		
		/* 
		 * In order to register tracing entities, the trace manager has to subscribe
		 * to certain tracing services: NEW_AGENT, AGENT_DESTROYED, NEW_ARTIFACT
		 * and NEW_AGGREGATION.
		 * Subscriptions to these tracing services cannot be removed; so, it is
		 * not necessary to store them.
		 * 
		 * TODO: ARTIFACTS and AGGREGATIONS are not supported yet
		 */
		arguments.put("x-match", "all");
		arguments.put("tracing_service", TracingService.DI_TracingServices[TracingService.NEW_AGENT].getName());
		this.traceSession.exchangeBind(this.getAid().name + ".trace", "amq.match",
				TracingService.DI_TracingServices[TracingService.NEW_AGENT].getName() + "#any", arguments);
		arguments.clear();
		
		arguments.put("x-match", "all");
		arguments.put("tracing_service", TracingService.DI_TracingServices[TracingService.AGENT_DESTROYED].getName());
		this.traceSession.exchangeBind(this.getAid().name + ".trace", "amq.match",
				TracingService.DI_TracingServices[TracingService.AGENT_DESTROYED].getName() + "#any", arguments);
		arguments.clear();
		
		// Send WELCOME_TM trace event to all agents (null destination).
		TraceEvent welcomeEvent = new TraceEvent(TracingService.DI_TracingServices[TracingService.WELCOME_TM].getName(),
				new AgentID(SYSTEM_NAME, this.getAid().protocol, this.getAid().host, this.getAid().port), "");
		this.sendSystemTraceEvent(welcomeEvent, null);
		
		// arguments.put("x-match", "all");
		// arguments.put("tracing_service", TracingService.DI_TracingServices[TracingService.NEW_ARTIFACT].getName());
		// this.traceSession.exchangeBind(this.getAid().name+".trace", "amq.match",
		// 		TracingService.DI_TracingServices[TracingService.NEW_ARTIFACT].getName() + "#any", arguments);
		// arguments.clear();
		//
		// arguments.put("x-match", "all");
		// arguments.put("tracing_service",	TracingService.DI_TracingServices[TracingService.NEW_AGGREGATION].getName());
		// this.traceSession.exchangeBind(this.getAid().name+".trace", "amq.match",
		// 		TracingService.DI_TracingServices[TracingService.NEW_AGGREGATION].getName() + "#any", arguments);
		// arguments.clear();
	}
	
	/**
	 * Sends a system trace event to the amq.match exchange
	 * 
	 * @param tEvent
	 *            Trace event to be sent
	 * 
	 * @param destination
	 *            Tracing entity to which the trace event is directed to. If set
	 *            to null, the system trace event is understood to be directed
	 *            to all tracing entities.
	 */
	private void sendSystemTraceEvent(TraceEvent tEvent, TracingEntity destination) {
		
		MessageTransfer xfr = new MessageTransfer();
		
		xfr.destination("amq.match");
		xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
		
		DeliveryProperties deliveryProps = new DeliveryProperties();
		
		// Serialize message content
		String body;
		// Timestamp
		body = String.valueOf(tEvent.getTimestamp()) + "#";
		// EventType
		body = body + tEvent.getTracingService().length() + "#"
				+ tEvent.getTracingService();
		// OriginEntiy
		body = body + tEvent.getOriginEntity().getType() + "#";
		body = body + tEvent.getOriginEntity().getAid().toString().length()
				+ "#" + tEvent.getOriginEntity().getAid().toString();
		// Content
		body = body + tEvent.getContent().length() + "#" + tEvent.getContent();
		
		xfr.setBody(body);
		
		// set message headers
		MessageProperties messageProperties = new MessageProperties();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		// set the message property
		messageHeaders.put("tracing_service", tEvent.getTracingService());
		messageHeaders.put("origin_entity", "system");
		if (destination == null) {
			messageHeaders.put("receiver", "all");
		} else if (destination.getType() == TracingEntity.AGENT) {
			messageHeaders.put("receiver", destination.getAid().name);
		}
		// else{
		// // Other tracing entity types are not supported yet
		//
		// }
		
		messageProperties.setApplicationHeaders(messageHeaders);
		
		Header header = new Header(deliveryProps, messageProperties);
		
		this.traceSession.messageTransfer("amq.match",
				MessageAcceptMode.EXPLICIT, MessageAcquireMode.PRE_ACQUIRED,
				header, xfr.getBodyString());
		
		/*
		 * PRE-OPTIMIZATION OF EVENT TRANSMISSION
		 * MessageTransfer xfr = new MessageTransfer();
		 * 
		 * xfr.destination("amq.match");
		 * xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		 * xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
		 * 
		 * DeliveryProperties deliveryProps = new DeliveryProperties();
		 * MessageProperties messageProperties = new MessageProperties();
		 * Map<String, Object> messageHeaders = new HashMap<String, Object>();
		 * 
		 * ByteArrayOutputStream bos = new ByteArrayOutputStream();
		 * ObjectOutputStream oos; try { oos = new ObjectOutputStream(bos);
		 * oos.writeObject(tEvent); oos.flush(); } catch (IOException e) {
		 * e.printStackTrace(); } xfr.setBody(bos.toByteArray());
		 * 
		 * // set the message property messageHeaders.put("tracing_service",
		 * tEvent.getTracingService()); messageHeaders.put("origin_entity",
		 * "system"); if (destination == null){ messageHeaders.put("receiver",
		 * "all"); } else if (destination.getType() == TracingEntity.AGENT){
		 * messageHeaders.put("receiver", destination.getAid().name); } // else{
		 * // // Other tracing entity types are not supported yet // // }
		 * 
		 * messageProperties.setApplicationHeaders(messageHeaders);
		 * 
		 * traceSession.messageTransfer(xfr.getDestination(),
		 * xfr.getAcceptMode(), xfr.getAcquireMode(), new Header(deliveryProps,
		 * messageProperties), xfr.getBodyBytes());
		 */
	}
	
	/**
	 * Inherited from the class BaseAgent.
	 */
	@Override
	protected void execute() {
		try {
			this.finishExecution.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// Preparing a trace event to update the mask of all the agents.
			AgentID tmAID = this.getAid();
			AgentID systemAID = new AgentID(SYSTEM_NAME, tmAID.protocol,
					tmAID.host, tmAID.port);
			TracingService newMaskTS = TracingService.DI_TracingServices[TracingService.NEW_MASK];
			
			/*
			 * The new trace mask will have all services unavailable and the DIE
			 * bit active, since the trace manager has gone.
			 */
			TraceMask noTMMask = new TraceMask(false);
			noTMMask.set(TraceMask.DIE);
			
			TraceEvent noTMEvent = new TraceEvent(newMaskTS.getName(),
					systemAID, noTMMask.toString());
			sendSystemTraceEvent(noTMEvent, null);
		}
	}
	
	/**
	 * Finish the execution of the TraceManager.
	 */
	public void shutdown() {
		this.finishExecution.release();
	}
	
	/**
	 * Requests to the trace manager are sent via ACL messages which are
	 * processed in this method
	 * 
	 * @param msg
	 *            Message received
	 */
	protected void onMessage(ACLMessage msg) {
		String content, auxContent, serviceName, serviceDescription, originEntity;
		Map<String, Object> arguments;
		int index, index2, length, counter;
		TraceEvent tEvent; // = new TraceEvent();
		ACLMessage response_msg = null;
		String command, specification;
		
		TracingService tService = null;
		TracingEntity tEntity = null, originTEntity = null;
		TracingServiceSubscription tServiceSubscription = null;
		
		Iterator<TracingServiceSubscription> TSS_iter;
		Iterator<TracingService> TS_iter;
		Iterator<TracingEntity> TE_iter;
		
		AgentID originAid;// , requestedAid;
		int aidindice1 = 0;
		int aidindice2 = 0;
		
		String tEventContent;
		boolean agree_response = true;
		boolean added_TS = false;
		boolean added_TSP = false;
		boolean linked_TE_TS = false;
		boolean added_TSS = false;
		boolean error;
		
		logger.info("[TRACE MANAGER]: Received [" + msg.getPerformativeInt()
				+ "] -> " + msg.getContent());
		
		switch (msg.getPerformativeInt()) {
		
			case ACLMessage.REQUEST:
				
				content = msg.getContent();
				
				index = content.indexOf('#', 0);
				command = content.substring(0, index);
				
				if (command.equals("publish")) {
					// Publication of a tracing service
					index2 = content.indexOf('#', index + 1);
					length = Integer.parseInt(content.substring(index + 1,
							index2));
					serviceName = content.substring(index2 + 1, index2 + 1
							+ length);
					index = index2 + length + 1;
					serviceDescription = content.substring(index);
					
					// Check the CUSTOM bit of the trace mask.
					if (this.traceMask.get(TraceMask.CUSTOM) == false) {
						// The mask does not allow to publish tracing services.
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("publish#"
								+ serviceName.length() + "#" + serviceName
								+ TraceError.SERVICE_NOT_ALLOWED);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to "
								+ msg.getReceiver().toString());
					} else if ((tEntity = TracingEntities.getTEByAid(msg
							.getSender())) == null) {
						// Error getting the tracing entity
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("publish#"
								+ serviceName.length() + "#" + serviceName
								+ TraceError.ENTITY_NOT_FOUND);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to "
								+ msg.getReceiver().toString());
					} else if (!TSProviderEntities.contains(tEntity)) {
						// Register tracing entity as service provider
						synchronized (TSProviderEntities) {
							error = TSProviderEntities.add(tEntity);
						}
						if (error) {
							added_TSP = true;
						} else {
							// Error adding the tracing entity
							agree_response = false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("publish#"
									+ serviceName.length() + "#" + serviceName
									+ TraceError.BAD_ENTITY);
							logger.info("[TRACE MANAGER]: Sending REFUSE message to "
									+ msg.getReceiver().toString());
						}
					}
					
					// Add tracing service
					if (agree_response
							&& ((tService = TracingServices.getTS(serviceName)) == null)) {
						// The tracing service does not exist
						tService = new TracingService(serviceName,
								serviceDescription);
						synchronized (TracingServices) {
							error = TracingServices.add(tService);
						}
						if (error) {
							added_TS = true;
						} else {
							// Impossible to add tracing service
							agree_response = false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("publish#"
									+ serviceName.length() + "#" + serviceName
									+ TraceError.BAD_SERVICE);
							logger.info("[TRACE MANAGER]: Sending REFUSE message to "
									+ msg.getReceiver().toString());
						}
					}
					
					// Link service provider and tracing service
					if (agree_response) {
						if (tService.getProviders().contains(tEntity)
								|| tEntity.getPublishedTS().contains(tService)) {
							// Tracing service already published by the tracing
							// entity
							agree_response = false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("publish#"
									+ serviceName.length() + "#" + serviceName
									+ TraceError.SERVICE_DUPLICATE);
							logger.info("[TRACE MANAGER]: Sending REFUSE message to "
									+ msg.getReceiver().toString());
						} else {
							synchronized (tService.getProviders()) {
								error = tService.getProviders().add(tEntity);
							}
							if (error) {
								synchronized (tEntity.getPublishedTS()) {
									error = tEntity.getPublishedTS().add(
											tService);
								}
							}
							if (!error) {
								// Impossible to link properly tracing service
								// and
								// provider
								agree_response = false;
								response_msg = new ACLMessage(ACLMessage.REFUSE);
								response_msg.setSender(this.getAid());
								response_msg.setReceiver(msg.getSender());
								response_msg.setContent("publish#"
										+ serviceName.length() + "#"
										+ serviceName
										+ TraceError.SUBSCRIPTION_ERROR);
								logger.info("[TRACE MANAGER]: Sending REFUSE message to "
										+ msg.getReceiver().toString());
							} else {
								linked_TE_TS = true;
							}
						}
					}
					
					if (agree_response) {
						tEvent = new TraceEvent(
								TracingService.DI_TracingServices[TracingService.PUBLISHED_TRACING_SERVICE]
										.getName(), tEntity, serviceName);
						try {
							sendTraceEvent(tEvent);
						} catch (TraceServiceNotAllowedException e) {
							logger.error("The tracing service PUBLISHED_TRACING_SERVICE must always be sent.");
							e.printStackTrace();
						}
						// sendSystemTraceEvent(tEvent, tEntity);
						
						response_msg = new ACLMessage(ACLMessage.AGREE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("publish#" + serviceName);
						logger.info("[TRACE MANAGER]: Sending AGREE message to "
								+ msg.getReceiver().toString());
					} else {
						if (linked_TE_TS) {
							synchronized (tService.getProviders()) {
								tService.getProviders().remove(tEntity);
							}
							synchronized (tEntity.getPublishedTS()) {
								tEntity.getPublishedTS().remove(tService);
							}
						}
						if (added_TS) {
							synchronized (TracingServices) {
								TracingServices.remove(tService);
							}
						}
						if (added_TSP) {
							synchronized (TSProviderEntities) {
								TSProviderEntities.remove(tEntity);
							}
						}
					}
				} else if (command.equals("unpublish")) {
					// Remove publication of a tracing service
					serviceName = content.substring(index + 1);
					
					// Check the CUSTOM bit of the trace mask.
					if (this.traceMask.get(TraceMask.CUSTOM) == false) {
						// The mask does not allow to publish tracing services.
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("unpublish#"
								+ serviceName.length() + "#" + serviceName
								+ TraceError.SERVICE_NOT_ALLOWED);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to "
								+ msg.getReceiver().toString());
					} else if ((tService = TracingServices.getTS(serviceName)) == null) {
						// The tracing service does not exist
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("unpublish#"
								+ serviceName.length() + "#" + serviceName
								+ TraceError.SERVICE_NOT_FOUND);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to "
								+ msg.getReceiver().toString());
					} else if (tService.getMandatory()) {
						// The tracing service cannot be unpublished
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("unpublish#"
								+ serviceName.length() + "#" + serviceName
								+ TraceError.BAD_SERVICE);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to "
								+ msg.getReceiver().toString());
					} else if ((tEntity = TSProviderEntities.getTEByAid(msg
							.getSender())) == null) {
						// The tracing entity does not offer the tracing service
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("unpublish#"
								+ serviceName.length() + "#" + serviceName
								+ TraceError.BAD_SERVICE);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to "
								+ msg.getReceiver().toString());
					} else if (!tService.getProviders().contains(tEntity)
							|| !tEntity.getPublishedTS().contains(tService)) {
						// Tracing service not published by the tracing entity
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("unpublish#"
								+ serviceName.length() + "#" + serviceName
								+ TraceError.BAD_SERVICE);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to "
								+ msg.getReceiver().toString());
					}
					
					// Remove all subscriptions and send the corresponding trace
					// events to subscriptors
					if (agree_response) {
						synchronized (tService.getSubscriptions()) {
							TSS_iter = tService.getSubscriptions().iterator();
							
							if (tService.getProviders().size() == 1) {
								// Just one provider: remove all subscriptions
								while (TSS_iter.hasNext()) {
									tServiceSubscription = TSS_iter.next();
									
									tServiceSubscription.getSubscriptorEntity().getSubscribedToTS().remove(tServiceSubscription);
									if (tServiceSubscription.getSubscriptorEntity().getSubscribedToTS().size() == 0) {
										synchronized (TSSubscriberEntities) {
											TSSubscriberEntities.remove(tServiceSubscription.getSubscriptorEntity());
										}
									}
									TSS_iter.remove();
									
									if (tServiceSubscription.getAnyProvider()) {
										tEventContent = serviceName + "#any";
										// Remove subscription
										this.traceSession.exchangeUnbind(tServiceSubscription.getSubscriptorEntity().getAid().name + ".trace",
												"amq.match", serviceName + "#any", Option.NONE);
									} else {
										tEventContent = serviceName + msg.getSender();
										// Remove subscription
										this.traceSession.exchangeUnbind(tServiceSubscription.getSubscriptorEntity().getAid().name + ".trace",
												"amq.match", serviceName + "#" + msg.getSender(), Option.NONE);
									}
									
									tEvent = new TraceEvent(TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS].getName(),
											new AgentID("system", this.getAid().protocol, this.getAid().host, this.getAid().port),
											tEventContent);
									sendSystemTraceEvent(tEvent,tServiceSubscription.getSubscriptorEntity());
								}
							} else {
								while (TSS_iter.hasNext()) {
									tServiceSubscription = TSS_iter.next();
									
									if (!tServiceSubscription.getAnyProvider() && tServiceSubscription.getOriginEntity().equals(tEntity)) {
										
										tServiceSubscription.getSubscriptorEntity().getSubscribedToTS().remove(tServiceSubscription);
										
										if (tServiceSubscription.getSubscriptorEntity().getSubscribedToTS().size() == 0) {
											synchronized (TSSubscriberEntities) {
												TSSubscriberEntities.remove(tServiceSubscription.getSubscriptorEntity());
											}
										}
										TSS_iter.remove();
										
										tEventContent = serviceName + msg.getSender();
										// Remove subscription
										this.traceSession.exchangeUnbind(tServiceSubscription.getSubscriptorEntity().getAid().name + ".trace",
												"amq.match", serviceName + "#" + msg.getSender(), Option.NONE);
										
										tEvent = new TraceEvent(TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS].getName(),
												new AgentID("system", this.getAid().protocol, this.getAid().host, this.getAid().port),
												tEventContent);
										sendSystemTraceEvent(tEvent, tServiceSubscription.getSubscriptorEntity());
									}
								}
							}
						}
						synchronized (TracingServices) {
							TracingServices.remove(tService);
						}
						synchronized (tEntity.getPublishedTS()) {
							tEntity.getPublishedTS().remove(tService);
						}
						synchronized (tService.getProviders()) {
							tService.getProviders().remove(tEntity);
						}
						if (tEntity.getPublishedTS().size() == 0) {
							synchronized (TSProviderEntities) {
								TSProviderEntities.remove(tEntity);
							}
						}
						
						tEvent = new TraceEvent(
								TracingService.DI_TracingServices[TracingService.UNPUBLISHED_TRACING_SERVICE]
										.getName(), tEntity, serviceName);
						try {
							sendTraceEvent(tEvent);
						} catch (TraceServiceNotAllowedException e) {
							logger.error("The tracing service UNPUBLISHED_TRACING_SERVICE must always be sent.");
							e.printStackTrace();
						}
						
						// tEventContent=TracingService.DI_TracingServices[TracingService.UNPUBLISHED_TRACING_SERVICE].getName()
						// +
						// "#" + serviceName + "#" + tEntity.getAid();
						// tEvent=new
						// TraceEvent(TracingService.DI_TracingServices[TracingService.UNPUBLISHED_TRACING_SERVICE].getName(),
						// tEntity, tEventContent);
						// sendSystemTraceEvent(tEvent, tEntity);
						
						response_msg = new ACLMessage(ACLMessage.AGREE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("unpublish#" + serviceName);
						logger.info("[TRACE MANAGER]: Sending AGREE message to "
								+ msg.getReceiver().toString());
					}
				} else if (command.equals("list")) {
					specification = content.substring(index + 1);
					if (specification.contentEquals("entities")) {
						// Check the LIST_ENTITIES bit of the trace mask.
						if (this.traceMask.get(TraceMask.LIST_ENTITIES) == false) {
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("list#entities#"
									+ TraceError.SERVICE_NOT_ALLOWED);
							logger.info("[TRACE MANAGER]: Sending REFUSE message to "
									+ msg.getReceiver().toString());
						} else {
							// Return all available tracing entities
							response_msg = new ACLMessage(ACLMessage.AGREE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							
							content = "list#entities#" + TracingEntities.size();
							
							synchronized (TracingEntities) {
								TE_iter = TracingEntities.iterator();
								
								while (TE_iter.hasNext()) {
									tEntity = TE_iter.next();
									
									content = content
											+ "#"
											+ tEntity.getType()
											+ "#"
											+ tEntity.getAid().toString()
													.length() + "#"
											+ tEntity.getAid().toString();
								}
							}
							
							response_msg.setContent(content);
						}
					}
					// else if
					// (specification.contentEquals("service_entities")){
					// // Return all tracing entities which offer a tracing
					// service
					// requestedAid=new AgentID
					// response_msg = new ACLMessage(ACLMessage.AGREE);
					// response_msg.setSender(this.getAid());
					// response_msg.setReceiver(msg.getSender());
					//
					// content="list#service_entities#"+ TracingEntities.size();
					//
					// TE_iter=TracingEntities.iterator();
					//
					// while(TE_iter.hasNext()){
					// tEntity=TE_iter.next();
					//
					// content = content + "#" + tEntity.getType() + "#" +
					// tEntity.getAid().toString().length() + "#" +
					// tEntity.getAid().toString();
					// }
					//
					// response_msg.setContent(content);
					// }
					else if (specification.contentEquals("services")) {
						// Check the LIST_SERVICES bit of the trace mask.
						if (this.traceMask.get(TraceMask.LIST_SERVICES) == false) {
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("list#services#"
									+ TraceError.SERVICE_NOT_ALLOWED);
							logger.info("[TRACE MANAGER]: Sending REFUSE message to "
									+ msg.getReceiver().toString());
						} else {
							// Return all tracing services allowed by the mask.
							counter = 0;
							response_msg = new ACLMessage(ACLMessage.AGREE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							
							content = "list#services#";
							auxContent = "";
							
							synchronized (TracingServices) {
								TS_iter = TracingServices.iterator();
								
								while (TS_iter.hasNext()) {
									tService = TS_iter.next();
									if (tService.getMaskBitIndex() == null
											|| this.traceMask.get(tService
													.getMaskBitIndex()) == true) {
										auxContent += "#"
												+ tService.getName().length()
												+ "#" + tService.getName()
												+ "#"
												+ tService.getDescription();
										++counter;
									}
								}
							}
							
							response_msg.setContent(content
									+ Integer.toString(counter) + auxContent);
						}
					}
					// else if (specification.contentEquals("entity_services")){
					// // Return all tracing services a tracing entity offers
					// response_msg = new ACLMessage(ACLMessage.AGREE);
					// response_msg.setSender(this.getAid());
					// response_msg.setReceiver(msg.getSender());
					//
					// content="list#services#"+ TracingServices.size();
					//
					// TS_iter=TracingServices.iterator();
					//
					// while(TS_iter.hasNext()){
					// tService=TS_iter.next();
					//
					// content = content + "#" + tService.getName().length() +
					// "#" +
					// tService.getName() +
					// "#" + tService.getDescription();
					// }
					//
					// response_msg.setContent(content);
					// }
					else {
						index2 = content.indexOf('#',index+1);
						if(index2 > 0) specification = content.substring(index+1,index2);
						if (specification.contentEquals("service")) {
							// Return service description
							serviceName = content.substring(index2 + 1);
							if ((tService = TracingServices.getTS(serviceName)) == null
									|| (tService.getMaskBitIndex() != null && this.traceMask
											.get(tService.getMaskBitIndex()) == false)) {
								/*
								 * The tracing service does not exist or it is
								 * related with a bit in the mask and that bit
								 * is not active.
								 */
								agree_response = false;
								response_msg = new ACLMessage(ACLMessage.REFUSE);
								response_msg.setSender(this.getAid());
								response_msg.setReceiver(msg.getSender());
								response_msg.setContent("list#service#"
										+ serviceName.length() + "#"
										+ serviceName
										+ TraceError.SERVICE_NOT_ALLOWED);
								logger.info("[TRACE MANAGER]: Sending REFUSE message to "
										+ msg.getReceiver().toString());
							} else {
								response_msg = new ACLMessage(ACLMessage.AGREE);
								response_msg.setSender(this.getAid());
								response_msg.setReceiver(msg.getSender());
								
								content = "list#service#"
										+ serviceName.length() + "#"
										+ serviceName
										+ tService.getDescription();
								
								response_msg.setContent(content);
							}
						} else {
							/*
							 * Building a ACLMessage
							 */
							response_msg = new ACLMessage(ACLMessage.UNKNOWN);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent(content);
							logger.info("[TRACE MANAGER]: Returning UNKNOWN message to "
									+ msg.getReceiver().toString());
						}
					}
				} else if (command.equals("UpdateMask")) {
					/*
					 * Building a ACLMessage
					 */
					response_msg = new ACLMessage(ACLMessage.AGREE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("UpdateMask#"+getTraceMask().toString());
					response_msg.setConversationId(msg.getConversationId());
					
				} else {
					/*
					 * Building a ACLMessage
					 */
					response_msg = new ACLMessage(ACLMessage.UNKNOWN);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent(content);
					logger.info("[TRACE MANAGER]: Returning UNKNOWN message to "
							+ msg.getReceiver().toString());
				}
				
				send(response_msg);
				
				break;
			
			case ACLMessage.SUBSCRIBE:
				// Subscription to tracing services
				arguments = new HashMap<String, Object>();
				
				content = msg.getContent();
				
				if (content.equals("all")) {
					if (this.monitorizable) {
						if ((tEntity = TracingEntities.getTEByAid(msg
								.getSender())) == null) {
							// Tracing entity not found
							agree_response = false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("subscribe#3#all"
									+ TraceError.ENTITY_NOT_FOUND);
							logger.info("[TRACE MANAGER]: Sending REFUSE message to "
									+ msg.getReceiver().toString());
						} else if (!TSSubscriberEntities.contains(tEntity)) {
							// Register tracing entity as subscriber
							synchronized (TSSubscriberEntities) {
								error = TSSubscriberEntities.add(tEntity);
							}
							if (!error) {
								// Error adding the tracing entity
								agree_response = false;
								response_msg = new ACLMessage(ACLMessage.REFUSE);
								response_msg.setSender(this.getAid());
								response_msg.setReceiver(msg.getSender());
								response_msg.setContent("subscribe#3#all"
										+ TraceError.SUBSCRIPTION_ERROR);
								logger.info("[TRACE MANAGER]: Sending REFUSE message to "
										+ msg.getReceiver().toString());
							}
						}
						
						if (agree_response) {
							synchronized (TracingServices) {
								TS_iter = TracingServices.iterator();
								while (TS_iter.hasNext()) {
									tService = TS_iter.next();
									// Subscribe to the tracing service
									if (tEntity.getSubscribedToTS().getTSS(
											tEntity, null, tService) != null
											|| (tService.getMaskBitIndex() != null && this.traceMask
													.get(tService
															.getMaskBitIndex()) == false)) {
										/*
										 * The subscription already exists or
										 * the tracing service is related with a
										 * bit in the mask that is off.
										 */
										continue;
									} else {
										// Add subscription
										tServiceSubscription = new TracingServiceSubscription(
												tEntity, null, tService);
										tService.addSubscription(tServiceSubscription);
										tEntity.addSubscription(tServiceSubscription);
										
										if (!tService
												.getName()
												.contentEquals(
														TracingService.DI_TracingServices[TracingService.SUBSCRIBE]
																.getName())
												&& !tService
														.getName()
														.contentEquals(
																TracingService.DI_TracingServices[TracingService.UNSUBSCRIBE]
																		.getName())) {
											arguments.put("x-match", "all");
											arguments.put("tracing_service",
													tService.getName());
											
											this.traceSession
													.exchangeBind(
															msg.getSender().name
																	+ ".trace",
															"amq.match",
															tService.getName()
																	+ "#any",
															arguments);
										}
										
										// Send system trace event
										tEventContent = tService.getName()
												+ "#"
												+ tService.getDescription()
														.length() + " "
												+ tService.getDescription()
												+ "#any";
										
										tEvent = new TraceEvent(
												TracingService.DI_TracingServices[TracingService.SUBSCRIBE]
														.getName(), tEntity,
												tEventContent);
										sendSystemTraceEvent(tEvent,
												tServiceSubscription
														.getSubscriptorEntity());
									}
								}
							}
							/**
							 * Building a ACLMessage
							 */
							response_msg = new ACLMessage(ACLMessage.AGREE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("subscribe#3#all");
							logger.info("[TRACE MANAGER]: sending message to "
									+ msg.getReceiver().toString());
						}
					} else {
						// Not in monitorizable mode
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("subscribe#3#all"
								+ TraceError.AUTHORIZATION_ERROR);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to "
								+ msg.getReceiver().toString());
					}
				} else {
					index = content.indexOf('#', 0);
					serviceName = content.substring(0, index);
					originEntity = content.substring(index + 1);
					
					if (!originEntity.contentEquals("any")) {
						originAid = new AgentID(originEntity);
						/*originAid = new AgentID();
						aidindice1 = 0;
						aidindice2 = originEntity.indexOf(':');
						if (aidindice2 - aidindice1 <= 0)
							originAid.protocol = "";
						else
							originAid.protocol = originEntity.substring(
									aidindice1, aidindice2);
						aidindice1 = aidindice2 + 3;
						aidindice2 = originEntity.indexOf('@', aidindice1);
						if (aidindice2 - aidindice1 <= 0)
							originAid.name = "";
						else
							originAid.name = originEntity.substring(aidindice1,
									aidindice2);
						aidindice1 = aidindice2 + 1;
						aidindice2 = originEntity.indexOf(':', aidindice1);
						if (aidindice2 - aidindice1 <= 0)
							originAid.host = "";
						else
							originAid.host = originEntity.substring(aidindice1,
									aidindice2);
						originAid.port = originEntity.substring(aidindice2 + 1);*/
					} else {
						originAid = null;
						originTEntity = null;
					}
					
					if ((tEntity = TracingEntities.getTEByAid(msg.getSender())) == null) {
						// Tracing entity not found
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("subscribe#"
								+ serviceName.length() + "#" + serviceName
								+ originEntity.length() + "#" + originEntity
								+ TraceError.ENTITY_NOT_FOUND);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to "
								+ msg.getReceiver().toString());
					} else if ((tService = TracingServices.getTS(serviceName)) == null) {
						// The tracing service does not exist
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("subscribe#"
								+ serviceName.length() + "#" + serviceName
								+ originEntity.length() + "#" + originEntity
								+ TraceError.SERVICE_NOT_FOUND);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to "
								+ msg.getReceiver().toString());
					} else if (!tService.getRequestable()) {
						// The tracing service is not requestable
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("subscribe#"
								+ serviceName.length() + "#" + serviceName
								+ originEntity.length() + "#" + originEntity
								+ TraceError.BAD_SERVICE);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to "
								+ msg.getReceiver().toString());
					} else if ((originAid != null)
							&& ((originTEntity = tService.getProviders()
									.getTEByAid(originAid)) == null)) {
						// Tracing service not published by the origin tracing
						// entity
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("subscribe#"
								+ serviceName.length() + "#" + serviceName
								+ originEntity.length() + "#" + originEntity
								+ TraceError.SERVICE_NOT_FOUND);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to "
								+ msg.getReceiver().toString());
					}
					// Check if the subscription already exists
					else if (TSSubscriberEntities.contains(tEntity)
							&& (tEntity.getSubscribedToTS().getTSS(tEntity,
									originTEntity, tService) != null)) {
						// The subscription already exists
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("subscribe#"
								+ serviceName.length() + "#" + serviceName
								+ originEntity.length() + "#" + originEntity
								+ TraceError.SUBSCRIPTION_DUPLICATE);
						logger.info("[TRACE MANAGER]: sending REFUSE message to "
								+ msg.getReceiver().toString());
					} else if (tService.getMaskBitIndex() != null
							&& this.traceMask.get(tService.getMaskBitIndex()) == false) {
						// Tracing service not allowed by the mask.
						agree_response = false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("subscribe#"
								+ serviceName.length() + "#" + serviceName
								+ originEntity.length() + "#" + originEntity
								+ TraceError.SERVICE_NOT_ALLOWED);
						logger.info("[TRACE MANAGER]: Sending REFUSE message to "
								+ msg.getReceiver().toString());
					} else if (!TSSubscriberEntities.contains(tEntity)) {
						// Register tracing entity as subscriber
						synchronized (TSSubscriberEntities) {
							error = TSSubscriberEntities.add(tEntity);
						}
						if (error) {
							added_TSS = true;
						} else {
							// Error adding the tracing entity
							agree_response = false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("subscribe#"
									+ serviceName.length() + "#" + serviceName
									+ originEntity.length() + "#"
									+ originEntity
									+ TraceError.SUBSCRIPTION_ERROR);
							logger.info("[TRACE MANAGER]: Sending REFUSE message to "
									+ msg.getReceiver().toString());
						}
					}
					
					if (agree_response) {
						// Add subscription
						tServiceSubscription = new TracingServiceSubscription(
								tEntity, originTEntity, tService);
						synchronized (tService.getSubscriptions()) {
							tService.addSubscription(tServiceSubscription);
						}
						synchronized (tEntity.getSubscribedToTS()) {
							tEntity.addSubscription(tServiceSubscription);
						}
						
						arguments.put("x-match", "all");
						arguments.put("tracing_service", serviceName);
						
						if (!originEntity.contentEquals("any")) {
							arguments.put("origin_entity", originEntity);
						}
						
						this.traceSession.exchangeBind(msg.getSender().name
								+ ".trace", "amq.match", serviceName + "#"
								+ originEntity, arguments);
						
						// Send system trace event
						// tEntity=new TracingEntity(TracingEntity.AGENT,
						// new AgentID("system", this.getAid().protocol,
						// this.getAid().host, this.getAid().port));
						
						// tEventContent=tService.getName() + "#" +
						// originEntity;
						tEventContent = serviceName + "#"
								+ tService.getDescription().length() + "#"
								+ tService.getDescription() + "#"
								+ originEntity;
						
						tEvent = new TraceEvent(
								TracingService.DI_TracingServices[TracingService.SUBSCRIBE]
										.getName(), tEntity, tEventContent);
						sendSystemTraceEvent(tEvent,
								tServiceSubscription.getSubscriptorEntity());
						
						/**
						 * Building a ACLMessage
						 */
						response_msg = new ACLMessage(ACLMessage.AGREE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("subscribe#"
								+ serviceName.length() + "#" + serviceName
								+ "#" + originEntity);
						// logger.info("[TRACE MANAGER]: sending message to " +
						// msg.getReceiver().toString());
					} else {
						if (added_TSS) {
							synchronized (TSSubscriberEntities) {
								TSSubscriberEntities.remove(tEntity);
							}
						}
					}
				}
				
				/**
				 * Sending a ACLMessage
				 */
				send(response_msg);
				
				break;
			
			case ACLMessage.CANCEL:
				// Unsubscription from a tracing service
				
				content = msg.getContent();
				
				index = content.indexOf('#', 0);
				serviceName = content.substring(0, index);
				originEntity = content.substring(index + 1);
				
				if (!originEntity.contentEquals("any")) {
					originAid = new AgentID(originEntity);
					/*originAid = new AgentID();
					aidindice1 = 0;
					aidindice2 = originEntity.indexOf(':');
					if (aidindice2 - aidindice1 <= 0)
						originAid.protocol = "";
					else
						originAid.protocol = originEntity.substring(aidindice1,
								aidindice2);
					aidindice1 = aidindice2 + 3;
					aidindice2 = originEntity.indexOf('@', aidindice1);
					if (aidindice2 - aidindice1 <= 0)
						originAid.name = "";
					else
						originAid.name = originEntity.substring(aidindice1,
								aidindice2);
					aidindice1 = aidindice2 + 1;
					aidindice2 = originEntity.indexOf(':', aidindice1);
					if (aidindice2 - aidindice1 <= 0)
						originAid.host = "";
					else
						originAid.host = originEntity.substring(aidindice1,
								aidindice2);
					originAid.port = originEntity.substring(aidindice2 + 1);*/
				} else {
					originAid = null;
					originTEntity = null;
				}
				
				if ((tService = TracingServices.getTS(serviceName)) == null) {
					// The tracing service does not exist
					agree_response = false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("unsubscribe#"
							+ serviceName.length() + "#" + serviceName
							+ originEntity.length() + "#" + originEntity
							+ TraceError.SERVICE_NOT_FOUND);
					logger.info("[TRACE MANAGER]: Sending REFUSE message to "
							+ msg.getReceiver().toString());
				}
				// Check if the subscription exists
				else if (((tEntity = TSSubscriberEntities.getTEByAid(msg
						.getSender())) == null)
						|| ((originAid != null) && (originTEntity = tService
								.getProviders().getTEByAid(originAid)) == null)
						|| ((tServiceSubscription = tEntity.getSubscribedToTS()
								.getTSS(tEntity, originTEntity, tService)) == null)) {
					
					// The subscription does not exist
					agree_response = false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("unsubscribe#"
							+ serviceName.length() + "#" + serviceName
							+ originEntity.length() + "#" + originEntity
							+ TraceError.SUBSCRIPTION_NOT_FOUND);
					logger.info("[TRACE MANAGER]: sending REFUSE message to "
							+ msg.getReceiver().toString());
				}
				// Check if the trace mask allows this operation.
				else if (tService.getMaskBitIndex() != null
						&& this.traceMask.get(tService.getMaskBitIndex()) == false) {
					// The tracing service is not allowed by the mask.
					agree_response = false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("unsubscribe#"
							+ serviceName.length() + "#" + serviceName
							+ originEntity.length() + "#" + originEntity
							+ TraceError.SERVICE_NOT_ALLOWED);
					logger.info("[TRACE MANAGER]: Sending REFUSE message to "
							+ msg.getReceiver().toString());
				}
				
				if (agree_response) {
					// Remove subscription
					synchronized (tEntity.getSubscribedToTS()) {
						tEntity.getSubscribedToTS()
								.remove(tServiceSubscription);
					}
					if (tEntity.getSubscribedToTS().size() == 0) {
						synchronized (TSSubscriberEntities) {
							TSSubscriberEntities.remove(tEntity);
						}
					}
					synchronized (tService.getSubscriptions()) {
						tService.getSubscriptions()
								.remove(tServiceSubscription);
					}
					this.traceSession.exchangeUnbind(msg.getSender().name
							+ ".trace", "amq.match", serviceName + "#"
							+ originEntity, Option.NONE);
					// logger.info("[TRACE MANAGER]: unbinding " +
					// msg.getSender().name+".trace from " + eventType);
					
					// Send system trace event
					tEventContent = serviceName + "#" + originEntity;
					
					tEvent = new TraceEvent(
							TracingService.DI_TracingServices[TracingService.UNSUBSCRIBE]
									.getName(), tEntity, tEventContent);
					
					sendSystemTraceEvent(tEvent, tEntity);
					
					/**
					 * Building a ACLMessage
					 */
					response_msg = new ACLMessage(ACLMessage.AGREE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("unsubscribe#"
							+ serviceName.length() + "#" + serviceName + "#"
							+ originEntity);
					logger.info("[TRACE MANAGER]: sending AGREE message to "
							+ msg.getReceiver().toString());
				}
				
				/**
				 * Sending a ACLMessage
				 */
				send(response_msg);
				
				break;
			
			default:
				/**
				 * Building a ACLMessage
				 */
				response_msg = new ACLMessage(ACLMessage.UNKNOWN);
				response_msg.setSender(this.getAid());
				response_msg.setReceiver(msg.getSender());
				response_msg.setContent(msg.getContent());
				logger.info("Mensaje received in " + this.getName()
						+ " agent, by onMessage: " + msg.getContent());
				logger.info("[TRACE MANAGER]: returning UNKNOWN message to "
						+ msg.getReceiver().toString());
				send(response_msg);
		}
		
	}
	
	/**
	 * Method intended to override the inherited from base agent, in order to
	 * reduce unnecessary overload.
	 * 
	 * @param tEvent
	 *            a trace event received.
	 */
	@SuppressWarnings("unused")
	private void preOnTraceEvent(TraceEvent tEvent) {
		this.onTraceEvent(tEvent);
	}
	
	@Override
	public void onTraceEvent(TraceEvent tEvent) {
		TracingEntity tEntity;
		TracingService tService;
		TracingServiceSubscription tServiceSubscription;
		Iterator<TracingService> TS_iter;
		Iterator<TracingServiceSubscription> TSS_iter;
		TraceEvent responseTEvent;
		boolean error;
		
		if (tEvent.getTracingService().contentEquals(
				TracingService.DI_TracingServices[TracingService.NEW_AGENT]
						.getName()) == true) {
			// Register tracing entity
			tEntity = new TracingEntity(TracingEntity.AGENT, new AgentID(
					tEvent.getContent()));
			synchronized (TracingEntities) {
				error = TracingEntities.add(tEntity);
			}
			if (error) {
				for (int i = 0; i < TracingService.MAX_DI_TS; i++) {
					if (TracingService.DI_TracingServices[i].getRequestable()) {
						tService = TracingServices
								.getTS(TracingService.DI_TracingServices[i]
										.getName());
						synchronized (tEntity.getPublishedTS()) {
							tEntity.getPublishedTS().add(tService);
						}
						synchronized (tService.getProviders()) {
							tService.getProviders().add(tEntity);
						}
					}
				}
			} else {
				// TODO: TRACE_ERROR ??
				// System.out.println("ERROR");
			}
			
			// Send a NEW_MASK trace event with the new trace mask.
			responseTEvent = new TraceEvent(
					TracingService.DI_TracingServices[TracingService.NEW_MASK]
							.getName(),
					new AgentID(SYSTEM_NAME, this.getAid().protocol, this
							.getAid().host, this.getAid().port), this
							.getTraceMask().toString());
			this.sendSystemTraceEvent(responseTEvent, tEntity);
			
		} else if (tEvent
				.getTracingService()
				.contentEquals(
						TracingService.DI_TracingServices[TracingService.AGENT_DESTROYED]
								.getName()) == true) {
			// Unregister tracing entity
			tEntity = TracingEntities.getTEByAid(new AgentID(tEvent
					.getContent()));
			
			// Cancel subscriptions of that tracing entity to any tracing
			// service
			if (TSSubscriberEntities.contains(tEntity)) {
				// There are subscriptions to cancel
				synchronized (tEntity.getSubscribedToTS()) {
					TSS_iter = tEntity.getSubscribedToTS().iterator();
					while (TSS_iter.hasNext()) {
						tServiceSubscription = TSS_iter.next();
						TracingServices
								.getTS(tServiceSubscription.getTracingService()
										.getName()).getSubscriptions()
								.remove(tServiceSubscription);
						TSS_iter.remove();
					}
				}
				synchronized (TSSubscriberEntities) {
					TSSubscriberEntities.remove(tEntity);
				}
			}
			
			// Unpublish tracing services the tracing entity was offering
			if (TSProviderEntities.contains(tEntity)) {
				// There are services which may have to be unpublished
				synchronized (tEntity.getPublishedTS()) {
					TS_iter = tEntity.getPublishedTS().iterator();
					while (TS_iter.hasNext()) {
						tService = TS_iter.next();
						synchronized (tService.getSubscriptions()) {
							TSS_iter = tService.getSubscriptions().iterator();
							
							if (tService.getProviders().size() == 1) {
								// Just one provider: remove all subscriptions
								// and inform to subscriptors
								while (TSS_iter.hasNext()) {
									tServiceSubscription = TSS_iter.next();
									
									tServiceSubscription.getSubscriptorEntity()
											.getSubscribedToTS()
											.remove(tServiceSubscription);
									if (tServiceSubscription
											.getSubscriptorEntity()
											.getSubscribedToTS().size() == 0) {
										synchronized (TSSubscriberEntities) {
											TSSubscriberEntities
													.remove(tServiceSubscription
															.getSubscriptorEntity());
										}
									}
									TSS_iter.remove();
									
									responseTEvent = new TraceEvent(
											TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS]
													.getName(), new AgentID(
													"system",
													this.getAid().protocol,
													this.getAid().host, this
															.getAid().port), "");
									
									if (tServiceSubscription.getAnyProvider()) {
										responseTEvent.setContent(tService
												.getName() + "#any");
										// Remove subscription
										this.traceSession.exchangeUnbind(
												tServiceSubscription
														.getSubscriptorEntity()
														.getAid().name
														+ ".trace",
												"amq.match", tService.getName()
														+ "#any", Option.NONE);
									} else {
										responseTEvent.setContent(tService
												.getName()
												+ tEntity.getAid().toString());
										// Remove subscription
										this.traceSession.exchangeUnbind(
												tServiceSubscription
														.getSubscriptorEntity()
														.getAid().name
														+ ".trace",
												"amq.match", tService.getName()
														+ tEntity.getAid()
																.toString(),
												Option.NONE);
									}
									
									sendSystemTraceEvent(responseTEvent,
											tServiceSubscription
													.getSubscriptorEntity());
								}
							} else {
								while (TSS_iter.hasNext()) {
									tServiceSubscription = TSS_iter.next();
									if (!tServiceSubscription.getAnyProvider()
											&& tServiceSubscription
													.getOriginEntity().equals(
															tEntity)) {
										
										tServiceSubscription
												.getSubscriptorEntity()
												.getSubscribedToTS()
												.remove(tServiceSubscription);
										if (tServiceSubscription
												.getSubscriptorEntity()
												.getSubscribedToTS().size() == 0) {
											synchronized (TSSubscriberEntities) {
												TSSubscriberEntities
														.remove(tServiceSubscription
																.getSubscriptorEntity());
											}
										}
										TSS_iter.remove();
										
										responseTEvent = new TraceEvent(
												TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS]
														.getName(),
												new AgentID("system", this
														.getAid().protocol,
														this.getAid().host,
														this.getAid().port),
												tService.getName()
														+ tEntity.getAid()
																.toString());
										// Remove subscription
										this.traceSession.exchangeUnbind(
												tServiceSubscription
														.getSubscriptorEntity()
														.getAid().name
														+ ".trace",
												"amq.match", tService.getName()
														+ tEntity.getAid()
																.toString(),
												Option.NONE);
										
										sendSystemTraceEvent(responseTEvent,
												tServiceSubscription
														.getSubscriptorEntity());
									}
								}
							}
						}
						TS_iter.remove();
						synchronized (tService.getProviders()) {
							tService.getProviders().remove(tEntity);
						}
					}
				}
				
				synchronized (TSProviderEntities) {
					TSProviderEntities.remove(tEntity);
				}
			}
			synchronized (TracingEntities) {
				TracingEntities.remove(tEntity);
			}
		}
	}
	/*public static void main(String args[]) throws Exception {
		AgentsConnection.connect();
		TraceManager tm = new TraceManager(new AgentID("TM"));
		System.out.println(tm.getTraceMask());
		TraceMask tMask = tm.getTraceMask();
		tMask.set(TraceMask.DIE);
		System.out.println(tMask);
		System.out.println(tm.getTraceMask());
		tm.shutdown();
	}*/
}
