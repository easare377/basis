package org.odk.cersgis.basis.configure;

import java.util.List;

public interface ServerRepository {

    void save(String url);

    List<String> getServers();

    void clear();
}
