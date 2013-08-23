package javabot.dao;

import javax.inject.Inject;

import javabot.BaseTest;
import javabot.operations.BaseOperationTest;
import javabot.operations.BotOperation;
import javabot.operations.GetFactoidOperation;
import org.testng.annotations.Test;

@Test(groups = {"operations"})
public class SeeLoopTest extends BaseOperationTest {
  @Inject
  private FactoidDao factoidDao;

  public BotOperation createOperation() {
    return new GetFactoidOperation(getJavabot());
  }

  private void serial() {
    createCircularSee();
    createCircularSee2();
    createNormalSee();
    followReferencesCorrectly();
  }

  public void createCircularSee() {
    deleteSees();
    testMessage("~see1 is <see>see2", ok);
    testMessage("~see2 is <see>see3", ok);
    testMessage("~see3 is <see>see3", ok);
    testMessage("~see1", "Loop detected for factoid '<see>see3'.");
    deleteSees();
  }

  private void deleteSees() {
    factoidDao.delete("test", "see1");
    factoidDao.delete("test", "see2");
    factoidDao.delete("test", "see3");
  }

  public void createCircularSee2() {
    deleteSees();
    testMessage("~see1 is <see>see2", ok);
    testMessage("~see2 is <see>see3", ok);
    testMessage("~see3 is <see>see1", ok);
    testMessage("~see1", "Loop detected for factoid '<see>see2'.");
    deleteSees();
  }

  public void followReferencesCorrectly() {
    deleteSees();
    testMessage("~see1 is Bzzt $who", ok);
    testMessage("~see2 is <see>see1", ok);
    testMessage("~see3 is <see>see2", ok);
    testMessage("~see3", String.format("%s, see1 is Bzzt %s", BaseTest.TEST_USER, BaseTest.TEST_USER));
    deleteSees();
  }

  public void createNormalSee() {
    deleteSees();
    testMessage("~see1 is <see>see2", ok);
    testMessage("~see2 is <see>see3", ok);
    testMessage("~see3 is w00t", ok);
    testMessage("~see1", BaseTest.TEST_USER + ", see3 is w00t");
    deleteSees();
  }
}