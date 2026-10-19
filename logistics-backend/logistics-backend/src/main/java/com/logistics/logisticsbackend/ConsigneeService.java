package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ConsigneeService {

    private final ConsigneeRepository consigneeRepository;

    public ConsigneeService(
            ConsigneeRepository consigneeRepository) {

        this.consigneeRepository = consigneeRepository;
    }

    public List<Consignee> getAllConsignees() {
        return consigneeRepository.findAll();
    }

    public Consignee getConsigneeById(Integer consigneeId) {

        return consigneeRepository
                .findById(consigneeId)
                .orElse(null);
    }

    public List<Consignee> getConsigneesByCustomerId(
            Integer customerId) {

        return consigneeRepository
                .findByCustomerId(customerId);
    }

    public Consignee createConsignee(
            Consignee consignee) {

        return consigneeRepository.save(consignee);
    }

    public Consignee updateConsignee(
            Integer consigneeId,
            Consignee consignee) {

        Consignee existing =
                consigneeRepository
                        .findById(consigneeId)
                        .orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setCustomerId(
                consignee.getCustomerId());

        existing.setConsigneeName(
                consignee.getConsigneeName());

        existing.setPhone(
                consignee.getPhone());

        existing.setAddress(
                consignee.getAddress());

        existing.setCity(
                consignee.getCity());

        existing.setState(
                consignee.getState());

        existing.setPincode(
                consignee.getPincode());

        existing.setConsigneeStatus(
                consignee.getConsigneeStatus());

        return consigneeRepository.save(existing);
    }

    public boolean deleteConsignee(Integer consigneeId) {

        if (!consigneeRepository
                .existsById(consigneeId)) {

            return false;
        }

        consigneeRepository.deleteById(consigneeId);

        return true;
    }
}
