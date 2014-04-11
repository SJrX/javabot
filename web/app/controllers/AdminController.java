package controllers;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import javabot.dao.ApiDao;
import javabot.dao.ChannelDao;
import javabot.dao.FactoidDao;
import javabot.javadoc.JavadocApi;
import javabot.model.ApiEvent;
import javabot.model.Channel;
import javabot.model.Config;
import javabot.model.EventType;
import models.Admin;
import org.bson.types.ObjectId;
import org.pac4j.play.java.JavaController;
import org.pac4j.play.java.RequiresAuthentication;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import security.OAuthDeadboltHandler;
import utils.AdminDao;
import utils.ConfigDao;
import utils.Context;
import views.html.admin.config;

@Singleton
@Restrict({@Group("botAdmin")})
@RequiresAuthentication(clientName = "Google2Client")
public class AdminController extends JavaController {
  @Inject
  private ConfigDao configDao;

  @Inject
  private AdminDao adminDao;

  @Inject
  private FactoidDao factoidDao;

  @Inject
  private ApiDao apiDao;

  @Inject
  private ChannelDao channelDao;

  @Inject
  private Provider<Context> contextProvider;

  private javabot.model.Admin getAdmin() {
    return adminDao.getAdmin(Http.Context.current().session().get("userName"));
  }

  public Result login() {
    return null;
  }

  public Result index() {
    return null;
  }

  public Result showConfig() {
    return null;
  }

  public Result javadoc() {
    return null;
  }

  public Result showChannel(String name) {
    return null;
  }

  public Result addAdmin() {
    Form<Admin> adminForm = Form.form(Admin.class);
    Admin admin = adminForm.bindFromRequest().get();
    adminDao.save(admin);
    return ok(routes.AdminController.index().method());
  }

  //  @Get("/deleteAdmin")
  //  @Restrict(JavabotRoleHolder.BOT_ADMIN)
  public Result deleteAdmin(String id) /*= RequiresAuthentication("Google2Client")*/ {
//    javabot.model.Admin admin = adminDao.find(id);
//    if (admin != null && !admin.getBotOwner) {
//      adminDao.delete(admin);
//    }
//    return new ok(routes.AdminController.index().method());
    return null;
  }

  public Result saveConfig() {
    javabot.model.Admin admin = adminDao.getAdmin(Http.Context.current().session().get("userName"));
    Form<Config> configForm = Form.form(Config.class).bindFromRequest();
    if (configForm.hasErrors()) {
      return badRequest(config.apply(new OAuthDeadboltHandler(), contextProvider.get(),
                                configForm, configDao.get().getOperations(), configDao.operations()));
    } else {
      configDao.save(admin, configForm.get());
      return ok(routes.Application.index().method());
    }
  }

  public Result enableOperation(String name) {
    adminDao.enableOperation(name, adminDao.getAdmin(Http.Context.current().session().get("userName")).getUserName());
    return ok(routes.AdminController.showConfig().method());
  }

  public Result disableOperation(String name) {
    adminDao.disableOperation(name, getAdmin().getUserName());
    return ok(routes.AdminController.showConfig().method());
  }

  public Result reloadApi(String id) {
    apiDao.save(new ApiEvent(EventType.RELOAD, getAdmin().getUserName(), new ObjectId(id)));
    return ok(routes.AdminController.javadoc().method());
  }

  public Result addApi() {
    JavadocApi api = Form.form(JavadocApi.class).bindFromRequest().get();
    apiDao.save(new ApiEvent(getAdmin().getUserName(), api.getName(), api.getBaseUrl(), api.getDownloadUrl()));
    return ok(routes.AdminController.javadoc().method());
  }

  public Result deleteApi(String id) {
    apiDao.save(new ApiEvent(EventType.DELETE, getAdmin().getUserName(), new ObjectId(id)));
    return ok(routes.AdminController.javadoc().method());
  }

  public Result saveChannel() {
    Channel channelInfo = Form.form(Channel.class).bindFromRequest().get();
    channelDao.save(channelInfo);
    return ok(config.apply(new OAuthDeadboltHandler(), contextProvider.get(), buildConfigForm(),
        configDao.get().getOperations(), configDao.operations()));
  }

  private Form<Config> buildConfigForm() {
    return Form.form(Config.class).fill(configDao.get());
  }
}