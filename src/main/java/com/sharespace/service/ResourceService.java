package com.sharespace.service;

import com.sharespace.collections.ResourceCollectionManager;
import com.sharespace.dao.ResourceDAO;
import com.sharespace.exceptions.InvalidResourceException;
import com.sharespace.model.Resource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Handles all resource management operations.
 * Uses ResourceDAO for DB persistence and ResourceCollectionManager for in-memory ops.
 */
public class ResourceService {
    private final ResourceDAO resourceDAO;
    private final ResourceCollectionManager collectionManager;

    public ResourceService() {
        this.resourceDAO = new ResourceDAO();
        this.collectionManager = new ResourceCollectionManager();
    }

    /**
     * Adds a new resource after validation.
     */
    public Resource addResource(String name, String category, String description,
                                 int quantity, String location, int ownerId, String ownerName)
            throws InvalidResourceException, SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidResourceException("Resource name cannot be empty.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new InvalidResourceException("Category cannot be empty.");
        }
        if (quantity < 1) {
            throw new InvalidResourceException("Quantity must be at least 1.");
        }

        Resource resource = new Resource();
        resource.setName(name.trim());
        resource.setCategory(category.trim());
        resource.setDescription(description != null ? description.trim() : "");
        resource.setTotalQuantity(quantity);
        resource.setAvailableQuantity(quantity);
        resource.setLocation(location != null ? location.trim() : "");
        resource.setOwnerId(ownerId);
        resource.setOwnerName(ownerName);
        resource.setActive(true);

        int generatedId = resourceDAO.addResource(resource);
        resource.setId(generatedId);
        collectionManager.addResource(resource);
        return resource;
    }

    public Optional<Resource> findById(int id) throws SQLException {
        return resourceDAO.findById(id);
    }

    public List<Resource> getAllResources() throws SQLException {
        List<Resource> resources = resourceDAO.findAll();
        return resources;
    }

    public List<Resource> getAvailableResources() throws SQLException {
        return resourceDAO.findAvailable();
    }

    public List<Resource> getResourcesByOwner(int ownerId) throws SQLException {
        return resourceDAO.findByOwnerId(ownerId);
    }

    public List<Resource> filterByCategory(String category) throws SQLException {
        return resourceDAO.findByCategory(category);
    }

    public List<Resource> searchByName(String keyword) throws SQLException {
        return resourceDAO.searchByName(keyword);
    }

    public List<Resource> getSortedByName(List<Resource> resources) {
        return collectionManager.sortByName(resources);
    }

    public List<Resource> getSortedByCategory(List<Resource> resources) {
        return collectionManager.sortByCategory(resources);
    }

    public boolean updateResource(Resource resource) throws InvalidResourceException, SQLException {
        if (resource.getName() == null || resource.getName().trim().isEmpty()) {
            throw new InvalidResourceException("Resource name cannot be empty.");
        }
        if (resource.getTotalQuantity() < 0) {
            throw new InvalidResourceException("Total quantity cannot be negative.");
        }
        return resourceDAO.updateResource(resource);
    }

    public boolean removeResource(int resourceId) throws SQLException {
        return resourceDAO.deactivateResource(resourceId);
    }

    public boolean updateAvailableQuantity(int resourceId, int delta) throws SQLException {
        Optional<Resource> opt = resourceDAO.findById(resourceId);
        if (opt.isEmpty()) return false;
        Resource r = opt.get();
        int newQty = r.getAvailableQuantity() + delta;
        if (newQty < 0) newQty = 0;
        if (newQty > r.getTotalQuantity()) newQty = r.getTotalQuantity();
        return resourceDAO.updateAvailableQuantity(resourceId, newQty);
    }
}
