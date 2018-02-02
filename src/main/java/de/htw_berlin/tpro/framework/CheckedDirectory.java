package de.htw_berlin.tpro.framework;

import java.io.*;
import java.util.*;

class DefaultFileFilter implements FileFilter
{
    public boolean accept(File file)
    {
        String fileName = file.getName();
        return fileName.endsWith(".jar") || fileName.endsWith(".zip");
    }
}

class AcceptAllFileFilter implements FileFilter
{
    public boolean accept(File file)
    {
        return true;
    }
}

class CheckedDirectory
{
    private File checkedDir;
    private HashMap<String,Long> knownFiles;
    private FileFilter fileFilter;
    private ArrayList<DirectoryListener> listeners;
    
    public CheckedDirectory(String deployDirName)
    {
        this(deployDirName, new DefaultFileFilter());
    }
    
    public CheckedDirectory(String deployDirName, FileFilter ff)
    {
        checkedDir = new File(deployDirName);
        if(!checkedDir.exists())
        {
            checkedDir.mkdir();
        }
        if(!checkedDir.isDirectory())
        {
            throw new IllegalArgumentException(deployDirName + " is not a directory");
        }
        knownFiles = new HashMap<String, Long>();
        if(ff != null)
        {
            fileFilter = ff;
        }
        else
        {
            fileFilter = new AcceptAllFileFilter();
        }
        listeners = new ArrayList<DirectoryListener>();
    }
    
    public void addDirectoryListener(DirectoryListener l)
    {
        listeners.add(l);
    }
    
    public void removeDirectoryListener(DirectoryListener l)
    {
        listeners.remove(l);
    }
    
    public void check()
    {
        File[] currentFiles;
        currentFiles = checkedDir.listFiles(fileFilter);
        
        for(File file: currentFiles)
        {
            if(!file.isDirectory())
            {
                Long time = knownFiles.get(file.getName());
                if(time == null)
                {
                    //new file
                    System.out.println("---found new file " + file.getName());
                    for(DirectoryListener l: listeners)
                    {
                        l.detectedNewFile(file);
                    }
                    knownFiles.put(file.getName(), file.lastModified());
                }
                else if(time < file.lastModified())
                {
                    //more recent file
                    System.out.println("---file " + file.getName() +
                                       " updated");
                    for(DirectoryListener l: listeners)
                    {
                        l.detectedModifiedFile(file);
                    }
                    knownFiles.put(file.getName(), file.lastModified());
                }
                else if(time > file.lastModified())
                {
                    //file got older????
                    System.out.println("---file " + file.getName() +
                                       " is older now???");
                }
                //else time == file.lastModified(): no change
            }
        }
        Iterator<String> iter = knownFiles.keySet().iterator();
        while(iter.hasNext())
        {
            String fileName = iter.next();
            boolean found = false;
            for(File file: currentFiles)
            {
                if(file.getName().equals(fileName))
                {
                    found = true;
                    break;
                }
            }
            if(!found)
            {
                //file deleted
                System.out.println("---file " + fileName + " deleted");
                for(DirectoryListener l: listeners)
                {
                    l.detectedMissingFile(fileName);
                }
                iter.remove();
            }
        }
    }
}
