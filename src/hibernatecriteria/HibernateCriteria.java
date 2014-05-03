/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernatecriteria;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author YUNUS
 */
public class HibernateCriteria {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        AnnotationConfiguration config = new AnnotationConfiguration();
        config.addAnnotatedClass(YeniMusteri.class);
        config.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();

        session.getTransaction().begin();
        //ekle(session);
        List liste = borculariGetir(session, 5000);

        System.out.println("*****************************************");
        System.out.println("****************SONUÇLAR*****************");
        System.out.println("*****************************************");
        System.out.println("5000 Liradan Fazla Borçlular");
        for (Iterator it = liste.iterator(); it.hasNext();) {
            YeniMusteri m = (YeniMusteri) it.next();
            System.out.println(m.getAd() + " " + m.getSoyad() + " adlı kişinin borcu:" + m.getBorc());
        }

        System.out.println("-----------------------------------------");

        List isimListe = isimGetir(session, "5%");
        System.out.println("Adı '5' ile Başlayanlar için Sonuçlar:");
        for (Iterator it = isimListe.iterator(); it.hasNext();) {
            YeniMusteri m = (YeniMusteri) it.next();
            System.out.println(m.getAd() + " " + m.getSoyad() + " - " + m.getBorc());
        }

        System.out.println("-----------------------------------------");

        List sonuc = isimeBorcaGoreGetir(session);
        System.out.println("3500 Liradan Az Borcu Olanlar ve Adı '5' ile Başlayanlar için Sonuçlar:");
        for (Iterator it = sonuc.iterator(); it.hasNext();) {
            YeniMusteri m = (YeniMusteri) it.next();
            System.out.println(m.getAd() + " " + m.getSoyad() + " - " + m.getBorc());
        }

        System.out.println("-----------------------------------------");

        List sonucSart = isimeVeyaBorcaGoreGetir(session);
        System.out.println("3500 Liradan Az Borcu Olanlar veya Adı '5' ile Başlayanlar için Sonuçlar:");
        for (Iterator it = sonucSart.iterator(); it.hasNext();) {
            YeniMusteri m = (YeniMusteri) it.next();
            System.out.println(m.getAd() + " " + m.getSoyad() + " - " + m.getBorc());
        }

        System.out.println("-----------------------------------------");

        List disjunctionSonuc = sorgulaDisjuntion(session);
        System.out.println("Adı '3' veya Soyadı '7' ile Başlamayan veya 6000 Liradan Fazla Borcu Olanlar için Sonuçlar:");
        for (Iterator it = disjunctionSonuc.iterator(); it.hasNext();) {
            YeniMusteri m = (YeniMusteri) it.next();
            System.out.println(m.getAd() + " " + m.getSoyad() + " - " + m.getBorc());
        }

        System.out.println("-----------------------------------------");

        List alanListe = alanGetir(session);
        System.out.println("3500 Lira ve Daha Fazla Borcu Olanların Ad ve Borç Listesi için Sonuçlar:");
        for (Iterator it = alanListe.iterator(); it.hasNext();) {
            Object[] m = (Object[]) it.next();
            for (int i = 0; i < m.length; i++) {
                System.out.print(m[i] + " ");
            }
            System.out.println("");
        }
    }

    public static void ekle(Session session) {
        for (int i = 0; i < 10; i++) {
            YeniMusteri yeni = new YeniMusteri(i + ". ad", i + ". soyad", new Random().nextInt(10000) + 1000);
            session.save(yeni);
        }
        session.getTransaction().commit();
    }

    public static List borculariGetir(Session session, double borc) {
        Criteria criteria = session.createCriteria(YeniMusteri.class);
        criteria.add(Expression.ge("borc", borc));
        criteria.addOrder(Order.asc("borc"));

        return criteria.list();

    }

    public static List isimGetir(Session session, String ad) {
        Criteria criteria = session.createCriteria(YeniMusteri.class);
        criteria.add(Expression.like("ad", ad));
        return criteria.list();

    }

    public static List isimeBorcaGoreGetir(Session session) {
        Criteria criteria = session.createCriteria(YeniMusteri.class);
        criteria.add(Restrictions.lt("borc", 3500.0));
        criteria.add(Restrictions.like("ad", "5%"));

        return criteria.list();
    }

    public static List isimeVeyaBorcaGoreGetir(Session session) {
        Criteria criteria = session.createCriteria(YeniMusteri.class);
        Criterion borc = Restrictions.lt("borc", 3500.0);
        Criterion isim = Restrictions.like("ad", "5%");
        LogicalExpression exp = Restrictions.or(borc, isim);
        criteria.add(exp);

        return criteria.list();
    }

    public static List sorgulaDisjuntion(Session session) {
        Criteria criteria = session.createCriteria(YeniMusteri.class);
        Criterion salary = Restrictions.gt("borc", 6000.0);
        Criterion name = Restrictions.like("ad", "3%");
        Criterion surname = Restrictions.ilike("soyad", "7%");
        
        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(salary);
        disjunction.add(name);
        disjunction.add(surname);
        criteria.add(disjunction);

        return criteria.list();
    }

    public static List alanGetir(Session session) {
        Criteria criteria = session.createCriteria(YeniMusteri.class);
        criteria.add(Expression.ge("borc", 3500.0));
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.property("ad"));
        projList.add(Projections.property("borc"));
        criteria.setProjection(projList);

        return criteria.list();
    }
}
