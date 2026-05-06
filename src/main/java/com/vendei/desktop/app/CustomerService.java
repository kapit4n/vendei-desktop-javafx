package com.vendei.desktop.app;

import com.vendei.desktop.domain.Customer;
import com.vendei.desktop.infra.customers.CustomerRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class CustomerService {
    private final CustomerRepository customers;

    public CustomerService(CustomerRepository customers) {
        this.customers = Objects.requireNonNull(customers);
    }

    public List<Customer> listAll(String query) {
        return customers.listAll(query);
    }

    public int countAll(String query) {
        return customers.countAll(query);
    }

    public Optional<Customer> findById(long id) {
        return customers.findById(id);
    }

    public Customer create(String name, String document) {
        return customers.insert(name, document);
    }
}
