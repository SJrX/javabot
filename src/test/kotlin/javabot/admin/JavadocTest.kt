package javabot.admin

import com.jayway.awaitility.Awaitility
import com.jayway.awaitility.Duration
import javabot.BaseMessagingTest
import javabot.dao.ApiDao
import javabot.dao.JavadocClassDao
import javabot.javadoc.JavadocApi
import javabot.model.ApiEvent
import javabot.model.EventType
import org.slf4j.LoggerFactory
import org.testng.Assert
import org.testng.annotations.Test
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Test
public class JavadocTest : BaseMessagingTest() {

    @Inject
    lateinit val apiDao: ApiDao

    @Inject
    lateinit val javadocClassDao: JavadocClassDao

    @Test
    @Throws(IOException::class)
    public fun servlets() {
        val apiName = "Servlet"
        dropApi(apiName)
        addApi(apiName, "http://tomcat.apache.org/tomcat-7.0-doc/servletapi/",
              "https://repo1.maven.org/maven2/javax/servlet/javax.servlet-api/3.0.1/javax.servlet-api-3.0.1.jar")
        checkServlets(apiName)
    }

    @Test(dependsOnMethods = arrayOf("servlets"))
    public fun reloadServlets() {
        val apiName = "Servlet"
        val event = ApiEvent(EventType.RELOAD, testUser.nick, apiDao.find(apiName)?.id)
        eventDao.save(event)
        waitForEvent(event, "reloading " + apiName, Duration(30, TimeUnit.MINUTES))
        messages.get()
        checkServlets(apiName)
    }

    private fun checkServlets(apiName: String) {
        Assert.assertEquals(javadocClassDao.getClass(apiDao.find(apiName)!!, "javax.servlet.http", "HttpServletRequest").size(), 1)
        scanForResponse("~javadoc HttpServlet", "javax/servlet/http/HttpServlet.html")
        scanForResponse("~javadoc HttpServlet.doGet(*)", "javax/servlet/http/HttpServlet.html#doGet")
        scanForResponse("~javadoc HttpServletRequest", "javax/servlet/http/HttpServletRequest.html")
        scanForResponse("~javadoc HttpServletRequest.getMethod()", "javax/servlet/http/HttpServletRequest.html#getMethod")
    }

    @Test
    @Throws(IOException::class)
    public fun javaee() {
        val apiName = "JavaEE7"
        dropApi(apiName)
        addApi(apiName, "http://docs.oracle.com/javaee/7/api/", "https://repo1.maven.org/maven2/javax/javaee-api/7.0/javaee-api-7.0.jar")
        scanForResponse("~javadoc Annotated", "javax/enterprise/inject/spi/Annotated.html")
        scanForResponse("~javadoc Annotated.getAnnotation(*)", "javax/enterprise/inject/spi/Annotated.html#getAnnotation")
        scanForResponse("~javadoc ContextService", "javax/enterprise/concurrent/ContextService.html")
        scanForResponse("~javadoc ContextService.createContextualProxy(*)", "createContextualProxy(java.lang.Object, java.lang.Class...)")
        scanForResponse("~javadoc ContextService.createContextualProxy(*)",
              "createContextualProxy(java.lang.Object, java.util.Map, java.lang.Class...)")
        scanForResponse("~javadoc ContextService.createContextualProxy(*)", "createContextualProxy(T, java.lang.Class)")
        scanForResponse("~javadoc ContextService.createContextualProxy(*)",
              "createContextualProxy(T, java.util.Map, java.lang.Class)")
        scanForResponse("~javadoc PartitionPlan", "javax/batch/api/partition/PartitionPlan.html")
        scanForResponse("~javadoc PartitionPlan.setPartitionProperties(Properties[])",
              "javax/batch/api/partition/PartitionPlan.html#setPartitionProperties(java.util.Properties[])")
    }

    @Test
    @Throws(MalformedURLException::class)
    public fun jdk() {
        javabot
        if (java.lang.Boolean.valueOf(System.getProperty("dropJDK", "false"))!!) {
            LOG.debug("Dropping JDK API")
            dropApi("JDK")
            LOG.debug("Done")
        }
        var api: JavadocApi? = apiDao.find("JDK")
        if (api == null) {
            val event = ApiEvent(testUser.nick, "JDK", "http://docs.oracle.com/javase/8/docs/api",
                  File(System.getProperty("java.home"), "lib/rt.jar").toURI().toURL().toString())
            eventDao.save(event)
            waitForEvent(event, "adding JDK", Duration(30, TimeUnit.MINUTES))
            messages.get()
            api = apiDao.find("JDK")
        }
        Assert.assertEquals(javadocClassDao.getClass(api!!, "java.lang", "Integer").size(), 1)
    }

    private fun addApi(apiName: String, apiUrlString: String, downloadUrlString: String) {
        val event = ApiEvent(testUser.nick, apiName, apiUrlString, downloadUrlString)
        eventDao.save(event)
        waitForEvent(event, "adding " + apiName, Duration(30, TimeUnit.MINUTES))
        LOG.debug("done waiting for event to finish")
        messages.get()
    }

    private fun dropApi(apiName: String) {
        javabot
        val event = ApiEvent(EventType.DELETE, testUser.nick, apiName)
        eventDao.save(event)
        waitForEvent(event, "dropping " + apiName, Duration.FIVE_MINUTES)
        Awaitility.await().atMost(60, TimeUnit.SECONDS).until<Any> { apiDao.find(apiName) == null }
        messages.get()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(JavadocTest::class.java)
    }
}
