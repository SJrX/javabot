package javabot.operations

import com.antwerkz.sofia.Sofia
import javabot.BaseTest
import javabot.BotListener
import javabot.dao.FactoidDao
import javabot.dao.KarmaDao
import org.pircbotx.User
import org.pircbotx.hooks.events.PrivateMessageEvent
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.io.IOException
import javax.inject.Inject

@Test(groups = arrayOf("operations"))
class AddFactoidOperationTest : BaseTest() {
    @Inject
    protected lateinit var factoidDao: FactoidDao
    @Inject
    protected lateinit var listener: BotListener
    @Inject
    protected lateinit var karmaDao: KarmaDao
    @Inject
    protected lateinit var addFactoidOperation: AddFactoidOperation
    @Inject
    protected lateinit var getFactoidOperation: GetFactoidOperation
    @Inject
    protected lateinit var forgetFactoidOperation: ForgetFactoidOperation

    @BeforeMethod
    fun setUp() {
        factoidDao.delete(BaseTest.TEST_TARGET_NICK, "test")
        factoidDao.delete(BaseTest.TEST_TARGET_NICK, "ping $1")
        factoidDao.delete(BaseTest.TEST_TARGET_NICK, "what")
        factoidDao.delete(BaseTest.TEST_TARGET_NICK, "what up")
        factoidDao.delete(BaseTest.TEST_TARGET_NICK, "test pong")
        factoidDao.delete(BaseTest.TEST_TARGET_NICK, "asdf")
        factoidDao.delete(BaseTest.TEST_TARGET_NICK, "12345")
        factoidDao.delete(BaseTest.TEST_TARGET_NICK, "replace")
    }

    fun factoidAdd() {
        var response = addFactoidOperation.handleMessage(message("test pong is pong"))
        Assert.assertEquals(response[0].value, ok)
        response = addFactoidOperation.handleMessage(message("ping \$1 is <action>sends some radar to \$1, awaits a response then forgets" +
                " how long it took"))
        Assert.assertEquals(response[0].value, ok)

        response = addFactoidOperation.handleMessage(message("what? is a question"))
        Assert.assertEquals(response[0].value, ok)
        response = addFactoidOperation.handleMessage(message("what up? is <see>what?"))
        Assert.assertEquals(response[0].value, ok)
    }

    fun replace() {
        var response = addFactoidOperation.handleMessage(message("replace is first entry"))
        Assert.assertEquals(response[0].value, ok)

        response = addFactoidOperation.handleMessage(message("no, replace is <reply>second entry"))
        Assert.assertEquals(response[0].value, Sofia.ok(testUser.nick))

        response = getFactoidOperation.handleMessage(message("replace"))
        Assert.assertEquals(response[0].value, "second entry")

        response = forgetFactoidOperation.handleMessage(message("forget replace"))
        Assert.assertEquals(response[0].value, Sofia.factoidForgotten("replace", testUser.nick))

        response = addFactoidOperation.handleMessage(message("no, replace is <reply>second entry"))
        Assert.assertEquals(response[0].value, Sofia.factoidUnknown("replace"))
    }

    @Test(dependsOnMethods = arrayOf("factoidAdd"))
    @Throws(IOException::class)
    fun duplicateAdd() {
        val message = "test pong is pong"
        var response = addFactoidOperation.handleMessage(message(message))
        Assert.assertEquals(response[0].value, ok)
        response = addFactoidOperation.handleMessage(message(message))
        Assert.assertEquals(response[0].value, Sofia.factoidExists("test pong", testUser.nick))
        forgetFactoidOperation.handleMessage(message("forget test pong"))
    }

    fun blankValue() {
        val response = addFactoidOperation.handleMessage(message("pong is"))
        Assert.assertEquals(response.size, 0)
    }

    fun addLog() {
        val response = addFactoidOperation.handleMessage(message("12345 is 12345"))
        Assert.assertEquals(response[0].value, ok)
        Assert.assertTrue(changeDao.findLog(Sofia.factoidAdded(testUser.nick, "12345", "12345")))
        forgetFactoidOperation.handleMessage(message("forget 12345"))
    }

    fun parensFactoids() {
        val factoid = "should be the full (/hi there) factoid"
        var response = addFactoidOperation.handleMessage(message("asdf is <reply>" + factoid))
        Assert.assertEquals(response[0].value, ok)
        response = getFactoidOperation.handleMessage(message("asdf"))
        Assert.assertEquals(response[0].value, factoid)
    }

    fun privMessage() {
        listener.onPrivateMessage(PrivateMessageEvent(ircBot.get(), targetUser, System.currentTimeMillis().toString() + " is doh!"))
        Assert.assertEquals(messages.get()[0], Sofia.privmsgChange())
    }

    private fun getKarma(target: User): Int {
        return karmaDao.find(target.nick)?.value ?: 0
    }
}