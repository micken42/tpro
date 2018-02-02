package de.htw_berlin.tpro.framework;

import java.io.*;
import java.net.*;
import java.util.*;

class DeploymentDirectoryListener implements DirectoryListener
{
    private WorkSpace workSpace;
    private ComponentManager compManager;

    public DeploymentDirectoryListener(WorkSpace workSpace,
                                       ComponentManager compManager)
    {
        this.workSpace = workSpace;
        this.compManager = compManager;
    }

    public void detectedNewFile(File zipFile)
    {
    	
        try
        {
            workSpace.createDirectory(zipFile);
            String compName = zipFile.getName();
            HashMap<String,String> configInfo =
                                       workSpace.analyzeManifestFile(compName);
            URL[] classpath = workSpace.getClassPath(compName);
            compManager.createComponent(compName, configInfo, classpath);
        }
        catch(IOException ioe)
        {
            System.err.println("---cannot unzip " + zipFile.getName());
            ioe.printStackTrace();
        }
        catch(Exception e)
        {
            System.err.println("---" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void detectedMissingFile(String missingFileName)
    {
        compManager.removeComponent(missingFileName);
        workSpace.deleteDirectory(missingFileName);
    }

    public void detectedModifiedFile(File modifiedFile)
    {
        detectedMissingFile(modifiedFile.getName());
        detectedNewFile(modifiedFile);
    }
}
