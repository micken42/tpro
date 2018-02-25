 package de.htw_berlin.tpro.framework;

import java.util.HashMap;

class ComponentContextImpl implements ComponentContext
{
    private HashMap<String,Object> registry;
    
    public ComponentContextImpl()
    {
        registry = new HashMap<String,Object>();
    }
    
    public synchronized void bind(String name, Object obj)
    {
        registry.put(name, obj);
    }
    
    public synchronized Object lookup(String name)
    {
        return registry.get(name);
    }
    
    public synchronized void unbind(String name)
    {
        registry.remove(name);
    }
}
