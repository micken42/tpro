package de.htw_berlin.tpro.framework;

public interface ComponentContext
{
    public void bind(String name, Object obj);
    public Object lookup(String name);
    public void unbind(String name);
}