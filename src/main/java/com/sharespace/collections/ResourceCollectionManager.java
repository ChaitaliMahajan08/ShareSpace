package com.sharespace.collections;

import com.sharespace.model.Resource;

import java.util.*;

/**
 * Manages in-memory resource collections using Java Collections Framework.
 *
 * MILESTONE 2 - Collections demonstrated:
 *  - ArrayList: primary list of resources
 *  - HashMap:   O(1) lookup by resource ID
 *  - HashSet:   unique resource names (duplicate prevention)
 *  - Comparator: sort by name, category, availability
 *  - Generics:  all collections are properly typed
 *  - Iterator:  traversal in search and display methods
 */
public class ResourceCollectionManager {

    // ArrayList: ordered, indexed list of all resources (Milestone 2)
    private final ArrayList<Resource> resourceList;

    // HashMap: fast lookup by resource ID (Milestone 2)
    private final HashMap<Integer, Resource> resourceMap;

    // HashSet: tracks unique resource names to prevent duplicates (Milestone 2)
    private final HashSet<String> resourceNames;

    // Comparator for sorting by name (Milestone 2)
    public static final Comparator<Resource> BY_NAME =
            Comparator.comparing(r -> r.getName().toLowerCase());

    // Comparator for sorting by category, then name (Milestone 2)
    public static final Comparator<Resource> BY_CATEGORY =
            Comparator.comparing((Resource r) -> r.getCategory().toLowerCase())
                      .thenComparing(r -> r.getName().toLowerCase());

    // Comparator for sorting by availability descending (Milestone 2)
    public static final Comparator<Resource> BY_AVAILABILITY =
            Comparator.comparingInt(Resource::getAvailableQuantity).reversed();

    public ResourceCollectionManager() {
        this.resourceList = new ArrayList<>();
        this.resourceMap  = new HashMap<>();
        this.resourceNames = new HashSet<>();
    }

    /**
     * Adds a resource to all in-memory collections.
     * Uses HashSet to prevent duplicate names.
     */
    public boolean addResource(Resource resource) {
        String nameKey = resource.getName().toLowerCase().trim();
        if (resourceNames.contains(nameKey)) {
            System.out.println("[Collection] Duplicate resource name prevented: " + resource.getName());
            return false;
        }
        resourceList.add(resource);
        resourceMap.put(resource.getId(), resource);
        resourceNames.add(nameKey);
        return true;
    }

    /**
     * Retrieves a resource by ID using the HashMap (O(1)).
     */
    public Optional<Resource> findById(int id) {
        return Optional.ofNullable(resourceMap.get(id));
    }

    /**
     * Updates a resource in all collections.
     */
    public boolean updateResource(Resource updated) {
        Resource existing = resourceMap.get(updated.getId());
        if (existing == null) return false;

        // Remove old name from HashSet, add new
        resourceNames.remove(existing.getName().toLowerCase().trim());
        resourceNames.add(updated.getName().toLowerCase().trim());

        // Update in map
        resourceMap.put(updated.getId(), updated);

        // Update in list using ListIterator for safe in-place modification (Milestone 2)
        ListIterator<Resource> iterator = resourceList.listIterator();
        while (iterator.hasNext()) {
            Resource r = iterator.next();
            if (r.getId() == updated.getId()) {
                iterator.set(updated);
                break;
            }
        }
        return true;
    }

    /**
     * Removes a resource from all collections.
     */
    public boolean removeResource(int resourceId) {
        Resource existing = resourceMap.remove(resourceId);
        if (existing == null) return false;
        resourceNames.remove(existing.getName().toLowerCase().trim());
        // Use Iterator for safe removal from ArrayList (Milestone 2)
        Iterator<Resource> iterator = resourceList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId() == resourceId) {
                iterator.remove();
                break;
            }
        }
        return true;
    }

    /**
     * Loads resources from DB into in-memory structures.
     */
    public void loadAll(List<Resource> resources) {
        resourceList.clear();
        resourceMap.clear();
        resourceNames.clear();
        for (Resource r : resources) {
            resourceList.add(r);
            resourceMap.put(r.getId(), r);
            resourceNames.add(r.getName().toLowerCase().trim());
        }
    }

    /**
     * Returns all resources as an unmodifiable list.
     */
    public List<Resource> getAll() {
        return Collections.unmodifiableList(resourceList);
    }

    /**
     * Filters resources by category using enhanced for-loop (Milestone 2).
     */
    public List<Resource> filterByCategory(String category) {
        List<Resource> result = new ArrayList<>();
        for (Resource r : resourceList) {  // enhanced for-loop (Milestone 2)
            if (r.getCategory().equalsIgnoreCase(category) && r.isActive()) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Filters by availability using Iterator (Milestone 2).
     */
    public List<Resource> filterAvailable() {
        List<Resource> result = new ArrayList<>();
        Iterator<Resource> it = resourceList.iterator();  // Iterator (Milestone 2)
        while (it.hasNext()) {
            Resource r = it.next();
            if (r.getAvailableQuantity() > 0 && r.isActive()) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Searches resources by name keyword (case-insensitive).
     */
    public List<Resource> searchByName(String keyword) {
        List<Resource> result = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Resource r : resourceList) {
            if (r.getName().toLowerCase().contains(lower) && r.isActive()) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Returns a new list sorted by name using BY_NAME Comparator (Milestone 2).
     */
    public List<Resource> sortByName(List<Resource> resources) {
        List<Resource> sorted = new ArrayList<>(resources);
        sorted.sort(BY_NAME);
        return sorted;
    }

    /**
     * Returns a new list sorted by category using BY_CATEGORY Comparator (Milestone 2).
     */
    public List<Resource> sortByCategory(List<Resource> resources) {
        List<Resource> sorted = new ArrayList<>(resources);
        sorted.sort(BY_CATEGORY);
        return sorted;
    }

    /**
     * Returns a new list sorted by availability using BY_AVAILABILITY Comparator (Milestone 2).
     */
    public List<Resource> sortByAvailability(List<Resource> resources) {
        List<Resource> sorted = new ArrayList<>(resources);
        sorted.sort(BY_AVAILABILITY);
        return sorted;
    }

    /**
     * Sorts the internal list using natural ordering (Comparable on Resource — Milestone 2).
     */
    public List<Resource> sortNatural() {
        List<Resource> sorted = new ArrayList<>(resourceList);
        Collections.sort(sorted); // Uses Resource.compareTo (Comparable)
        return sorted;
    }

    public int size() { return resourceList.size(); }

    public Set<String> getUniqueNames() {
        return Collections.unmodifiableSet(resourceNames);
    }
}
