package com.jwm.ir.entity.dao;

import com.jwm.ir.entity.ValidExtension;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 2016-08-09.
 */
public class ExtensionDaoImpl implements ExtensionDao {

    private final Session session;
    public ExtensionDaoImpl(Session session) {
        this.session = session;
    }

    @Override
    public List<String> getAllValidExtensions() {
        List<ValidExtension> validExtensions = session.createCriteria(ValidExtension.class).list();
        List<String> extensions = new ArrayList<>();
        for(ValidExtension ve : validExtensions) {
            extensions.add(ve.getExt());
        }
        return extensions;
    }
}
