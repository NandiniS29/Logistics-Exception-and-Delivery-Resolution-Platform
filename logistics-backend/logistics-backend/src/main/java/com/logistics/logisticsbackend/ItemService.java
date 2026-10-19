package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(Integer itemId) {

        return itemRepository
                .findById(itemId)
                .orElse(null);
    }

    public List<Item> getItemsByPackageId(Integer packageId) {

        return itemRepository
                .findByPackageId(packageId);
    }

    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(
            Integer itemId,
            Item item) {

        Item existing =
                itemRepository
                        .findById(itemId)
                        .orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setPackageId(
                item.getPackageId());

        existing.setItemName(
                item.getItemName());

        existing.setItemDescription(
                item.getItemDescription());

        existing.setQuantity(
                item.getQuantity());

        existing.setUnitPrice(
                item.getUnitPrice());

        existing.setItemStatus(
                item.getItemStatus());

        return itemRepository.save(existing);
    }

    public boolean deleteItem(Integer itemId) {

        if (!itemRepository.existsById(itemId)) {
            return false;
        }

        itemRepository.deleteById(itemId);

        return true;
    }
}
