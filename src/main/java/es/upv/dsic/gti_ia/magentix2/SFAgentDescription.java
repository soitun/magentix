package es.upv.dsic.gti_ia.magentix2;

public class SFAgentDescription {

	private String goalprofile;
	private String ID;
	private String ImplementationID;
	private String URLProfile;
	private String URLProcess;

	/**
	 * 
	 * @param URLProcess The URL where the owl's document (related with service process) is located.
	 * @param URLProfile The URL where the owl's document (related with service profile) is located.
	 */
	public SFAgentDescription(String URLProcess, String URLProfile) {
		this.goalprofile = "";
		this.ID = "";
		this.ImplementationID = "";
		this.URLProcess=URLProcess;
		this.URLProfile =URLProfile;
		
		
	}

	/**
	 * Change The URL where the owl's document (related with service process) is located.
	 * @param url
	 */
	public void setURLProcess(String url)
	{
		this.URLProcess = url;
	}
	
	/**
	 * Return the URL where the owl's document (related with service process) is located.
	 * @return String 
	 */
	public String getURLProcess()
	{
		return this.URLProcess;
	}
	/**
	 * Change the URL where the owl's document (related with service profile) is located.
	 * @param url
	 */
	public void setURLProfile(String url)
	{
		this.URLProfile = url;
	}
	/**
	 * Return the URL where the owl's document (related with service profile) is located.
	 * @return
	 */
	public String getURIProfile()
	{
		return this.URLProfile;
	}
	/**
	 * Change ID of the SFAgentDescription
	 * @param id
	 */
	public void setID(String id) {
		this.ID = id;
	}
	/**
	 * Return an ID of the SFAgentDescription
	 * @return
	 */
	public String getID() {
		return this.ID;
	}
	/**
	 * Add the implementationID
	 * @param im
	 */
	public void setImplementationID(String im) {
		this.ImplementationID = im;
	}
	/**
	 * Return implementationID
	 * @return
	 */
	public String getImplementationID() {
		return this.ImplementationID;

	}
	/**
	 * Return Service Goal
	 * @return
	 */
	public String getServiceGoal() {
		return this.goalprofile;
	}
	/**
	 * Return Profile goal
	 * @param goalProfile
	 */
	public void setServiceGoal(String goalProfile) {
		this.goalprofile = goalProfile;
	}


}