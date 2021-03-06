package organization.TestDataBaseInterface;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.*;

import organization.TestDataBaseInterface.DatabaseAccess;
import es.upv.dsic.gti_ia.organization.DataBaseInterface;


/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestCheckAgentActive {

	DataBaseInterface dbI = null;
	DatabaseAccess dbA = null;
	private Method m = null;
	
	
	@Before
	public void setUp() throws Exception {
		
		Class[] parameterTypes = new Class[1];
	    parameterTypes[0] = java.lang.String.class;
		
	    m = DataBaseInterface.class.getDeclaredMethod("checkAgentActive", parameterTypes);
		m.setAccessible(true);
		
		dbA = new DatabaseAccess();

		//-------------  Clean Data Base  ------------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM actionNormParam");
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//
	}

	@After
	public void tearDown() throws Exception {

		//-------------  Clean Data Base  ------------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM actionNormParam");
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//

		dbA = null;
		
		dbI = null;
		
		m = null;
	}
	
	@Test(timeout = 5 * 1000)
	public void testCheckAgentActive1() {
		
		/**---------------------------------------------------------------------------------
		 * --			1.	
		 * --				- All parameters are correct
		 * --				- Agent play a role within unit
		 * --				- Agent is active
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String agentName = "proofAgent";
			String unit = "exampleUnitFlat";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
		    parameters[0] = agentName;
			
			boolean result = (Boolean) m.invoke(dbI, parameters);
			assertTrue("The agent exists in the system.", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testCheckAgentActive2() {
		
		/**---------------------------------------------------------------------------------
		 * --			2.	
		 * --				- All parameters are correct
		 * --				- Agent has a norm associated
		 * --				- Agent is active
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String agentName = "proofAgent";
			String unit = "exampleUnitFlat";
			String norm = "exampleNorm";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ norm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = '"+ agentName +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), '', '')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ norm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ norm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ norm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ norm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ norm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
		    parameters[0] = agentName;
			
			boolean result = (Boolean) m.invoke(dbI, parameters);
			assertTrue("The agent exists in the system.", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testCheckAgentActive3() {
		
		/**---------------------------------------------------------------------------------
		 * --			3.	
		 * --				- All parameters are correct
		 * --				- Agent isn't active
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String agentName = "proofAgent";
			//Data Base
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
		    parameters[0] = agentName;
			
			boolean result = (Boolean) m.invoke(dbI, parameters);
			assertFalse("The agent exists in the system.", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testCheckAgentActive4() {
		
		/**---------------------------------------------------------------------------------
		 * --			4.	
		 * --				- Any parameters are incorrect
		 * --				- Agent doesn't exist
		 * --				- Agent isn't active
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
		
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
		    parameters[0] = "NotExists";
			
			boolean result = (Boolean) m.invoke(dbI, parameters);
			assertFalse("The agent exists in the system.", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
}
