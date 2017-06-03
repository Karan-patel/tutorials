package com.baeldung.hibernate.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import com.baeldung.hibernate.pojo.Supplier;

public class SupplierDao implements GenericDao<Supplier>{
    private SessionFactory sessionFactory;
    private String tenant;
    
    public SupplierDao(SessionFactory sessionFactory, String tenant) {
        this.sessionFactory = sessionFactory;
        this.tenant = tenant;
        populate();
    }

    @Override
    public void save(Supplier entity) {
        Session session = sessionFactory.withOptions().tenantIdentifier(tenant).openSession();
        session.save(entity);
    }

    @Override
    public void delete(Supplier supplier) {
        Session session = sessionFactory.withOptions().tenantIdentifier(tenant).openSession();
        session.delete(supplier);
    }

    @Override
    public Supplier findByName(String name) {
        Session session = sessionFactory.withOptions().tenantIdentifier(tenant).openSession();
        List<Supplier> fetchedSuppliers = session.createCriteria(Supplier.class).add(Expression.eq("name", name)).list();
        if (fetchedSuppliers.size()>0) {
            return fetchedSuppliers.get(0);
        }else {
            return null;
        }
    }

    @Override
    public List<Supplier> findAll() {
        Session session = sessionFactory.withOptions().tenantIdentifier(tenant).openSession();
        return session.createCriteria(Supplier.class).list();
    }

    @Override
    public void populate() {
        System.out.println("Init DB1");
        Session session = sessionFactory.withOptions().tenantIdentifier(tenant).openSession();
        Transaction transaction = session.getTransaction();
        
        transaction.begin();
        session.createSQLQuery("DROP ALL OBJECTS").executeUpdate();
        session
            .createSQLQuery(
                "create table Supplier (id integer generated by default as identity, country varchar(255), name varchar(255), primary key (id))")
            .executeUpdate();
        Supplier genertedSupplier = generateEntityForTenant(tenant);
        System.out.println("Inserting Supplier"+genertedSupplier);
        save (genertedSupplier);
        
    }

    private Supplier generateEntityForTenant(String forTenant) {
        if (forTenant.equals("mydb1")) {
            return new Supplier ("John","USA");
        }
        return new Supplier ("Miller","UK");
    }
    
    

}