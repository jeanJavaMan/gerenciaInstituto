/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.util;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author Jeanderson
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            File arquivoXMl = new File("hibernate.cfg.xml");

            Configuration configuracao = new Configuration().configure(arquivoXMl);
            sessionFactory = configuracao.buildSessionFactory();
        } catch (HibernateException ex) {
            Log.salvaLogger(ex);
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }

    public static boolean restartSession() {
        try {
            File arquivoXMl = new File("hibernate.cfg.xml");
            Configuration configuracao = new Configuration().configure(arquivoXMl);
            sessionFactory = configuracao.buildSessionFactory();
            return true;
        } catch (HibernateException ex) {
            System.err.println(ex);
            return false;
        }
    }

    public static void shutdown() {
        sessionFactory.close();
    }
}
