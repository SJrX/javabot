package javabot.kotlin.web.views

import com.google.inject.Injector
import javabot.Javabot
import javabot.dao.ConfigDao
import javabot.model.Config
import javabot.operations.BotOperation

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import java.util.ArrayList
import java.util.Collections
import java.util.TreeSet

public class ConfigurationView(injector: Injector, request: HttpServletRequest) : MainView(injector, request) {
    @Inject
    lateinit val configDao: ConfigDao
    @Inject
    lateinit val javabot: Javabot

    private val config: Config by lazy {
        configDao.get()
    }

    public fun operations(): List<BotOperation> {
        val all = ArrayList(javabot.getAllOperations().values())
        Collections.sort(all) { left, right -> left.getName().compareTo(right.getName()) }
        return all
    }

    public fun getCurrentOps(): Set<String> {
        return TreeSet(config.operations)
    }

    public fun enabled(operation: String): Boolean {
        return getCurrentOps().contains(operation)
    }

    override fun getChildView(): String {
        return "admin/configuration.ftl"
    }
}
