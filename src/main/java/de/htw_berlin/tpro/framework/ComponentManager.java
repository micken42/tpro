package de.htw_berlin.tpro.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;

class ComponentManager
{
    private HashMap<String, Object> components;
    private ComponentContext context;
    
    public ComponentManager()
    {
        components = new HashMap<String, Object>();
        context = new ComponentContextImpl();
    }
    
    public void createComponent(String componentName,
                                HashMap<String,String> configInfo, 
                                URL[] classpath)
        throws Exception
    {
        System.out.println("---addComponent(" + componentName + ")");
        System.out.println("---" + configInfo);
        String mainClassName = configInfo.get("Main");
        if(mainClassName == null)
        {
            return;
        }

        ClassLoader parent = null;
        String usedComp = configInfo.get("Uses");
        if(usedComp != null)
        {
            Object obj = components.get(usedComp);
            if(obj != null)
            {
                parent = obj.getClass().getClassLoader();
            }
        }
        
        ClassLoader cl;
        if(parent != null)
        {
            cl = new URLClassLoader(classpath, parent);
        }
        else
        {
            cl = new URLClassLoader(classpath);
        }
        
        Object mainObject = null;
        try
        {
        	System.out.println(mainClassName);
            Class<?> newClass = cl.loadClass(mainClassName);
            mainObject = newClass.newInstance();
            callAnnotatedMethods(mainObject, Start.class);
        }
        catch(Exception e)
        {
            System.out.println("---exception in addComponent");
            e.printStackTrace();
        }
        
        components.put(componentName, mainObject);

    }

    public void removeComponent(String componentName)
    {
        Object mainObject = components.get(componentName);
        if(mainObject == null)
        {
            return;
        }
        callAnnotatedMethods(mainObject, Stop.class);
        components.remove(componentName);
    }

    public void removeAllComponents()
    {
        String[] componentNames = components.keySet().toArray(new String[0]);
        for(String componentName: componentNames)
        {
            removeComponent(componentName);
        }
    }
    
    private void callAnnotatedMethods(Object mainObject,
                                      Class<? extends Annotation> annoClass)
    {
        Class<?> classDesc = mainObject.getClass();
        Method[] methods = classDesc.getMethods();
        for(Method m : methods)
        {
            Class<?>[] params = m.getParameterTypes();
            Class<?> returnType = m.getReturnType();
            if(m.isAnnotationPresent(annoClass) && returnType == void.class)
            {
                if(params.length == 0)
                {
                    try
                    {
                        m.invoke(mainObject);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else if(params.length == 1 &&
                        params[0] == ComponentContext.class)
                {
                    try
                    {
                        m.invoke(mainObject, context);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
