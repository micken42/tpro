package de.htw_berlin.tpro.framework;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

class WorkSpace
{
    private static final int BUFFER_SIZE = 2048;

    private String dirName;
    
    public WorkSpace(String dir)
    {
        dirName = dir;
        File directory = new File(dir);
        if(!directory.exists())
        {
            directory.mkdir();
        }
        if(!directory.isDirectory())
        {
            throw new IllegalArgumentException(dir + " is not a directory");
        }
        //clean working directory
        File[] files = directory.listFiles();
        for(File f: files)
        {
            deleteRecursively(f);
        }
    }
    
    public void deleteDirectory(String name)
    {
        deleteRecursively(new File(dirName + "/" + name));
    }
    
    private void deleteRecursively(File currentFile)
    {
        if(currentFile.isDirectory())
        {
            File[] contents = currentFile.listFiles();
            for(File file: contents)
            {
                deleteRecursively(file);
            }
        }
        //System.out.println("---delete " + currentFile.getName());
        currentFile.delete();
    }
    
    public void createDirectory(File zipFile) throws IOException
    {
        String newDirName = dirName + "/" + zipFile.getName();
        File newDir = new File(newDirName);
        newDir.mkdir();
        ZipFile zip = new ZipFile(zipFile);

        Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements())
        {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            //System.out.println("---extracting " + currentEntry);
            File destFile = new File(newDirName, currentEntry);
            //destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            if (!entry.isDirectory())
            {
                BufferedInputStream is = null;
                BufferedOutputStream dest = null;
                try
                {
                    is = new BufferedInputStream(zip.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER_SIZE];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    dest = new BufferedOutputStream(fos, BUFFER_SIZE);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER_SIZE)) != -1)
                    {
                        dest.write(data, 0, currentByte);
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(dest != null)
                    {
                        dest.flush();
                        dest.close();
                    }
                    if(is != null)
                    {
                        is.close();
                    }
                }
            }
        }
        zip.close();
    }

    public  HashMap<String,String> analyzeManifestFile(String compName)
        throws FrameworkException
    {
        String manifestDirName = dirName + "/" + compName + "/META-INF";
        File manifestDir = new File(manifestDirName);
        if(!manifestDir.exists())
        {
            throw new FrameworkException("no META-INF directory for " +
                                         "component " + compName);
        }
        if(!manifestDir.isDirectory())
        {
            throw new FrameworkException("META-INF is not a directory for "+
                                         "component " + compName);
        }
        File manifestFile = new File(manifestDir, "MANIFEST.MF");
        if(!manifestFile.exists())
        {
            throw new FrameworkException("no file MANIFEST.MF in META-INF "+
                                         "directory for component " +
                                         compName);
        }
        if(manifestFile.isDirectory())
        {
            throw new FrameworkException("META-INF/MANIFEST.MF must not be "+
                                         "a directory for component " +
                                         compName);
        }
        BufferedReader br = null;
        HashMap<String,String> result = new HashMap<String, String>();
        try
        {
            br = new BufferedReader(new FileReader(manifestFile));
            String line;
            while((line = br.readLine()) != null)
            {
                StringTokenizer st = new StringTokenizer(line, ":");
                if(st.hasMoreTokens())
                {
                    String key = st.nextToken().trim();
                    if(st.hasMoreTokens())
                    {
                        String value = st.nextToken().trim();
                        result.put(key, value);
                    }
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw new FrameworkException(e.getMessage());
        }
        finally
        {
            try
            {
                br.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    public URL[] getClassPath(String compName)
        throws FrameworkException
    {
        String classesDirName = dirName + "/" + compName + "/classes";
        File classesDir = new File(classesDirName);
        if(!classesDir.exists())
        {
            throw new FrameworkException("no classes directory for component " +
                                         compName);
        }
        if(!classesDir.isDirectory())
        {
            throw new FrameworkException("classes is not a directory for "+
                                         "component " + compName);
        }
        URL url;
        try
        {
            url = new File(classesDirName + "/").toURI().toURL();
        }
        catch (MalformedURLException e)
        {
            throw new FrameworkException(e.getMessage());
        }
        return new URL[]{url};
    }
    
    public void clean()
    {
        deleteDirectory(".");
    }
}
