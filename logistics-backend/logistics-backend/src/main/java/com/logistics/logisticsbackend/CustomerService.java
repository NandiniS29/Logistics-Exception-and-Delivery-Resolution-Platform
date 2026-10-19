package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(
            CustomerRepository customerRepository) {

        this.customerRepository = customerRepository;
    }

    // =========================================================
    // GET ALL CUSTOMERS
    // =========================================================

    public List<Customer> getAllCustomers() {

        return customerRepository.findAll();
    }

    // =========================================================
    // GET CUSTOMER BY ID
    // =========================================================

    public Customer getCustomerById(
            Integer customerId) {

        return customerRepository
                .findById(customerId)
                .orElse(null);
    }

    // =========================================================
    // GET CUSTOMER BY EMAIL
    // =========================================================

    public Customer getCustomerByEmail(
            String email) {

        return customerRepository
                .findByEmail(email)
                .orElse(null);
    }

    // =========================================================
    // CREATE CUSTOMER
    // =========================================================

    public Customer createCustomer(
            Customer customer) {

        return customerRepository.save(customer);
    }

    // =========================================================
    // UPDATE CUSTOMER
    // =========================================================

    public Customer updateCustomer(
            Integer customerId,
            Customer customer) {

        Customer existing =
                customerRepository
                        .findById(customerId)
                        .orElse(null);

        if (existing == null) {
            return null;
        }

        // Update only the fields supplied by the profile form

        if (customer.getCustomerName() != null) {

            existing.setCustomerName(
                    customer.getCustomerName()
            );
        }

        if (customer.getEmail() != null) {

            existing.setEmail(
                    customer.getEmail()
            );
        }

        if (customer.getPhone() != null) {

            existing.setPhone(
                    customer.getPhone()
            );
        }

        if (customer.getAddress() != null) {

            existing.setAddress(
                    customer.getAddress()
            );
        }

        if (customer.getCity() != null) {

            existing.setCity(
                    customer.getCity()
            );
        }

        if (customer.getState() != null) {

            existing.setState(
                    customer.getState()
            );
        }

        if (customer.getPincode() != null) {

            existing.setPincode(
                    customer.getPincode()
            );
        }

        // IMPORTANT:
        // Preserve the existing customer status
        // if the frontend does not send it.

        if (customer.getCustomerStatus() != null) {

            existing.setCustomerStatus(
                    customer.getCustomerStatus()
            );
        }

        return customerRepository.save(existing);
    }

    // =========================================================
    // DELETE CUSTOMER
    // =========================================================

    public boolean deleteCustomer(
            Integer customerId) {

        if (!customerRepository
                .existsById(customerId)) {

            return false;
        }

        customerRepository.deleteById(customerId);

        return true;
    }
}
