package de.htw_berlin.tpro.plugin.plugin1;

import de.htw_berlin.tpro.framework.Start;
import de.htw_berlin.tpro.framework.Stop;

public class MainClass
{
	
	//@Inject // Bean will be created on plugin installation
	//private UserMockServiceImpl ums;
	
    public MainClass()
    {
        System.out.println("User Mock Service Plugin installed!");
    }
    
    @Start
    public void start1()
    {
        System.out.println("app1.start1()");
    }
    
    public void start2()
    {
        System.out.println("app1.start2()");
    }
    
    @Start
    public int start3()
    {
        System.out.println("app1.start3()");
        return 0;
    }
    
    @Start
    public void start4(int i)
    {
        System.out.println("app1.start4(int)");
    }
    
    @Start
    protected void start5()
    {
        System.out.println("app1.start5()");
    }
    
    @Start
    public void start6()
    {
        System.out.println("app1.start6()");
    }
    
    @Stop
    public void stop1()
    {
        System.out.println("app1.stop1()");
    }
    
    @Stop
    public int stop2()
    {
        System.out.println("app1.stop2()");
        return 0;
    }
    
    @Stop
    public void stop3()
    {
        System.out.println("app1.stop3()");
    }
}

