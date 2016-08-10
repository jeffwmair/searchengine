package jwm.ir.domain.persistence;

import jwm.ir.domain.ValidExtension;

import java.util.List;

/**
 * Created by Jeff on 2016-08-09.
 */
public interface ExtensionDao {
    List<String> getAllValidExtensions();
}
