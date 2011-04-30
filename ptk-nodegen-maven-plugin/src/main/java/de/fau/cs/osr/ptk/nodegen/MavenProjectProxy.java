/**
 * Copyright 2011 The Open Source Research Group,
 *                University of Erlangen-Nürnberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fau.cs.osr.ptk.nodegen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;

public class MavenProjectProxy
{
	private final MavenProject project;
	
	private final List<String> compileSourceRoots = new ArrayList<String>();
	
	private File basedir;
	
	// =========================================================================
	
	public MavenProjectProxy()
	{
		this.project = null;
	}
	
	public MavenProjectProxy(MavenProject project)
	{
		this.project = project;
	}
	
	// =========================================================================
	
	public void addCompileSourceRoot(String path)
	{
		if (project != null)
		{
			project.addCompileSourceRoot(path);
		}
		else
		{
			compileSourceRoots.add(path);
		}
	}
	
	public File getBasedir()
	{
		return project != null ? project.getBasedir() : this.basedir;
	}
	
	// =========================================================================
	
	public List<String> getCompileSourceRoots()
	{
		return compileSourceRoots;
	}
	
	public void setBasedir(File basedir)
	{
		this.basedir = basedir;
	}
}
