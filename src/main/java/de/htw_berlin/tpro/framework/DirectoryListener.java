package de.htw_berlin.tpro.framework;

import java.io.File;

interface DirectoryListener
{
    public void detectedNewFile(File newFile);
    public void detectedMissingFile(String missingFileName);
    public void detectedModifiedFile(File modifiedFile);
}
