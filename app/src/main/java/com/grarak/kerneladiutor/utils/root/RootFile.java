/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutor.utils.root;

import com.grarak.kerneladiutor.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 30.12.15.
 */
public class RootFile {

    private final String mFile;
    private RootUtils.SU mSU;

    public RootFile(String file) {
        mFile = file;
        mSU = RootUtils.getSU();
    }

    public RootFile(String file, RootUtils.SU su) {
        mFile = file;
        mSU = su;
    }

    public String getName() {
        return mSU.runCommand("basename '" + mFile + "'");
    }

    public void mkdir() {
        mSU.runCommand("mkdir -p '" + mFile + "'");
    }

    public void mv(String newPath) {
        mSU.runCommand("mv -f '" + mFile + "' '" + newPath + "'");
    }

    public void write(String text, boolean append) {
        String[] array = text.split("\\r?\\n");
        if (!append) delete();
        for (String line : array) {
            mSU.runCommand("echo '" + line + "' >> " + mFile);
        }
        RootUtils.chmod(mFile, "755", mSU);
    }

    public String execute(String... arguments) {
        StringBuilder args = new StringBuilder();
        for (String arg : arguments) {
            args.append(" \"").append(arg).append("\"");
        }
        return mSU.runCommand(mFile + args.toString());
    }

    public void delete() {
        mSU.runCommand("rm -r '" + mFile + "'");
    }

    public List<String> list() {
        List<String> list = new ArrayList<>();
        String files = mSU.runCommand("ls '" + mFile + "/'");
        if (files != null) {
            // Make sure the files exists
            for (String file : files.split("\\r?\\n")) {
                if (file != null && !file.isEmpty() && Utils.existFile(mFile + "/" + file)) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    public List<RootFile> listFiles() {
        List<RootFile> list = new ArrayList<>();
        String files = mSU.runCommand("ls '" + mFile + "/'");
        if (files != null) {
            // Make sure the files exists
            for (String file : files.split("\\r?\\n")) {
                if (file != null && !file.isEmpty() && Utils.existFile(mFile + "/" + file)) {
                    list.add(new RootFile(mFile + "/" + file, mSU));
                }
            }
        }
        return list;
    }

    public boolean isDirectory() {
        return "true".equals(mSU.runCommand("[ -d " + mFile + " ] && echo true"));
    }

    public RootFile getParentFile() {
        return new RootFile(mSU.runCommand("dirname \"" + mFile + "\""), mSU);
    }

    public RootFile getRealPath() {
        return new RootFile(mSU.runCommand("realpath \"" + mFile + "\""), mSU);
    }

    public boolean isEmpty() {
        return "false".equals(mSU.runCommand("find '" + mFile + "' -mindepth 1 | read || echo false"));
    }

    public boolean exists() {
        String output = mSU.runCommand("[ -e " + mFile + " ] && echo true");
        return output != null && output.equals("true");
    }

    public String readFile() {
        return mSU.runCommand("cat '" + mFile + "'");
    }

    public String toString() {
        return mFile;
    }

}
