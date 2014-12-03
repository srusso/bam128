package com.bam128i.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class TextFileFilter extends FileFilter {
	
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String name = f.getName();
        return (name.endsWith(".txt") || name.endsWith(".bam"));
    }

    public String getDescription() {
        return "Plain Text (*.txt, *.bam)";
    }
}

