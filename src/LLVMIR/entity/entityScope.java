package LLVMIR.entity;

import java.util.HashMap;

public class entityScope {
    public HashMap<String , entity> entityMap;
    public entityScope parent;

    public entityScope(entityScope parent) {
        entityMap = new HashMap<>();
        this.parent = parent;
    }

    public entityScope getParentEntityScope() {
        return parent;
    }

    public void addEntity(String name , entity e) {
        entityMap.put(name , e);
    }

    public Boolean containEntity(String name) {
        if (entityMap.containsKey(name)) return true;
        else if (parent != null) return parent.containEntity(name);
        else return false;
    }

    public entity getEntity(String name) {
        if (entityMap.containsKey(name)) return entityMap.get(name);
        else if (parent != null) return parent.getEntity(name);
        else return null;
    }
}
