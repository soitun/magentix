package organization.TestDataBaseInterface;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.*;

import organization.TestDataBaseInterface.DatabaseAccess;
import es.upv.dsic.gti_ia.organization.DataBaseInterface;


/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestGetPositionNormsOverwrited {

	DataBaseInterface dbI = null;
	DatabaseAccess dbA = null;
	private Method m = null;
	
	
	@Before
	public void setUp() throws Exception {
		
		Class[] parameterTypes = new Class[1];
	    parameterTypes[0] = java.lang.String.class;
		
	    m = DataBaseInterface.class.getDeclaredMethod("getPositionNorms", parameterTypes);
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
	public void testGetPositionNormsOverwrited1() {
		
		/**---------------------------------------------------------------------------------
		 * --			1.	
		 * --				- All parameters are correct
		 * --				- Norm is associated to member position
		 * --				- Norm2 is associated to any position
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String position = "member";
			String eNorm = "exampleNorm";
			String eNorm2 = "exampleNorm2";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'positionName'),(SELECT idposition FROM position WHERE positionName = '"+ position +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), '', '')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'exampleRoleCreator2')");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'f'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'positionName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerRole' AND numParams = 6), '', '')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerRole' AND numParams = 6), 'exampleRoleMember')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerRole' AND numParams = 6), 'proofUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerRole' AND numParams = 6), 'external')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerRole' AND numParams = 6), 'public')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerRole' AND numParams = 6), 'member')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerRole' AND numParams = 6), 'exampleAgent')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
			parameters[0] = unit;
		    
		    ArrayList<ArrayList<String>> result = (ArrayList<ArrayList<String>>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "[["+ eNorm +", "+ unit +", positionName, "+ position +"], ["+ eNorm2 +", "+ unit +", positionName, _]]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetPositionNormsOverwrited2() {
		
		/**---------------------------------------------------------------------------------
		 * --			2.	
		 * --				- All parameters are correct
		 * --				- Norm is associated to any agent
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String eNorm = "exampleNorm";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'positionName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), '', '')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
			parameters[0] = unit;
		    
		    ArrayList<ArrayList<String>> result = (ArrayList<ArrayList<String>>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "[["+ eNorm +", "+ unit +", positionName, _]]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetPositionNormsOverwrited3() {
		
		/**---------------------------------------------------------------------------------
		 * --			3.
		 * --				- All parameters are correct
		 * --				- Unit hasn't got norms associated to their positions
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String eNorm = "exampleNorm";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), '', '')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
			parameters[0] = unit;
		    
		    ArrayList<ArrayList<String>> result = (ArrayList<ArrayList<String>>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "[]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetPositionNormsOverwrited4() {
		
		/**---------------------------------------------------------------------------------
		 * --			4.	
		 * --				- Any parameters are incorrect
		 * --				- Unit doesn't exist
		 * --				- Not return information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
			parameters[0] = "NotExists";
		    
		    ArrayList<ArrayList<String>> result = (ArrayList<ArrayList<String>>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "[]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
}
