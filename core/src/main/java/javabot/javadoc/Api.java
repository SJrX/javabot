package javabot.javadoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.antwerkz.maven.SPI;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import javabot.model.Persistent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created Oct 29, 2008
 *
 * @author <a href="mailto:jlee@antwerkz.com">Justin Lee</a>
 */
@Entity("apis")
/*
@NamedQueries({
    @NamedQuery(name = ApiDao.FIND_BY_NAME, query = "select a from Api a where upper(a.name)=upper(:name)"),
    @NamedQuery(name = ApiDao.FIND_ALL, query = "select a from Api a order by a.name")
})
*/
@SPI(Persistent.class)
public class Api implements Persistent {
    private static final Logger log = LoggerFactory.getLogger(Api.class);
    @Id
    private Long id;
    private String name;
    private String baseUrl;
    private String packages;
    private List<Clazz> classes = new ArrayList<Clazz>();
    private static final List<String> JDK_JARS = Arrays.asList("classes.jar", "rt.jar", "jce.jar", "jsse.jar");

    public Api() {
    }

    public Api(final String apiName, final String url, final String pkgs) {
        name = apiName;
        baseUrl = url.endsWith("/") ? url : url + "/";
        packages = pkgs;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<Clazz> getClasses() {
        return classes;
    }

    public void setClasses(final List<Clazz> classes) {
        this.classes = classes;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(final String packages) {
        this.packages = packages;
    }
}
