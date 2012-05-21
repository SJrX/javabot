package javabot.dao;

import java.util.List;

import javabot.dao.util.QueryParam;
import javabot.model.Factoid;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({"ConstantNamingConvention"})
public interface FactoidDao extends BaseDao {
    String ALL = "Factoid.all";
    String COUNT = "Factoid.count";
    String BY_NAME = "Factoid.byName";
    String BY_PARAMETERIZED_NAME = "Factoid.byParameterizedName";

    boolean hasFactoid(String key);

    Factoid addFactoid(String sender, String key, String value);

    void delete(String sender, String key);

    Factoid getFactoid(String key);

    Factoid getParameterizedFactoid(String key);

    Factoid find(Long id);

    Long count();

    List<Factoid> getFactoids();

    List<Factoid> find(QueryParam qp);

    List<Factoid> getFactoidsFiltered(QueryParam qp, Factoid filter);

    Long factoidCountFiltered(Factoid filter);

    @Transactional
    void pruneFactoids();
}
