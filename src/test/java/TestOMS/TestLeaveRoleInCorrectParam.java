package TestOMS;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.exception.RoleNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;


public class TestLeaveRoleInCorrectParam extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;

	OMS oms = null;
	SF sf = null;

	Process qpid_broker;
	

	protected void tearDown() throws Exception {


		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		dbA = null;
		omsProxy = null;

		agent.terminate();
		agent = null;

		oms.Shutdown();
		sf.Shutdown();
		
		oms.await();
		sf.await();
		
		oms = null;
		sf = null;

		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
	protected void setUp() throws Exception {
		super.setUp();

		qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);
		

		AgentsConnection.connect();


		oms = new OMS(new AgentID("OMS"));

		sf =  new SF(new AgentID("SF"));

		oms.start();
		sf.start();


		agent = new Agent(new AgentID("pruebas"));



		omsProxy = new OMSProxy(agent);

		dbA = new DatabaseAccess();

		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//
		
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		

	}

	public void testLeaveRole1()
	{
		try
		{

			String result = omsProxy.leaveRole("participante", "noexiste");

			fail(result);

		}catch(UnitNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testLeaveRole2()
	{
		try
		{

			String result = omsProxy.leaveRole("participante", "");

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testLeaveRole3()
	{
		try
		{

			String result = omsProxy.leaveRole("participante", null);

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testLeaveRole4()
	{
		try
		{

			String result = omsProxy.leaveRole("noexiste", "virtual");

			fail(result);

		}catch(RoleNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testLeaveRole5()
	{
		try
		{

			String result = omsProxy.leaveRole("", "virtual");

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testLeaveRole6()
	{
		try
		{

			String result = omsProxy.leaveRole(null, "virtual");

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
}
