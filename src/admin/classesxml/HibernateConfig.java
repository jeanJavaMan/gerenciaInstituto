/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin.classesxml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author jeanderson
 */
@XStreamAlias("hibernate-configuration")
public class HibernateConfig {
    @XStreamAlias("session-factory")
    private SessionFactory factory;

    public SessionFactory getFactory() {
        return factory;
    }

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }
    
    
}
