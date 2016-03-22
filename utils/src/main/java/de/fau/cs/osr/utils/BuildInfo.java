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
package de.fau.cs.osr.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public class BuildInfo
{
	private String groupId;
	
	private String artifaceId;
	
	private String projectName;
	
	private String projectVersion;
	
	private String vendor;
	
	private String gitBranch;
	
	private String gitBuildUserName;
	
	private String gitBuildUserEmail;
	
	private String gitBuildTime;
	
	private String gitCommitId;
	
	private String gitCommitIdAbbrev;
	
	private String gitCommitUserName;
	
	private String gitCommitUserEmail;
	
	private String gitCommitMessageFull;
	
	private String gitCommitMessageShort;
	
	private String gitCommitTime;
	
	// =========================================================================
	
	public static BuildInfo build(String groupId, String artifactId, ClassLoader cl)
	{
		if (groupId == null || artifactId == null || cl == null)
			throw new NullPointerException();
		
		BuildInfo info = new BuildInfo();
		
		info.groupId = groupId;
		info.artifaceId = artifactId;
		
		String path = String.format(
		        "%s/%s.buildinfo.properties",
		        groupId.replace('.', '/'),
		        artifactId);
		
		InputStream is = cl.getResourceAsStream(path);
		if (is == null)
			return null;
		
		Properties properties = new Properties();
		try
		{
			Reader r = new InputStreamReader(is, "UTF-8");
			properties.load(r);
			is.close();
		}
		catch (IOException e)
		{
			return null;
		}
		
		String gid = properties.getProperty("project.groupId");
		if (gid != null)
			info.groupId = gid;
		
		String aid = properties.getProperty("project.artifactId");
		if (aid != null)
			info.artifaceId = aid;
		
		info.projectName = properties.getProperty("project.name");
		info.projectVersion = properties.getProperty("project.version");
		
		info.vendor = properties.getProperty("organization.name");
		
		info.gitBranch = properties.getProperty("git.branch");
		
		info.gitBuildUserName = properties.getProperty("git.build.user.name");
		info.gitBuildUserEmail = properties.getProperty("git.build.user.email");
		info.gitBuildTime = properties.getProperty("git.build.time");
		
		info.gitCommitId = properties.getProperty("git.commit.id");
		info.gitCommitIdAbbrev = properties.getProperty("git.commit.id.abbrev");
		info.gitCommitUserName = properties.getProperty("git.commit.user.name");
		info.gitCommitUserEmail = properties.getProperty("git.commit.user.email");
		info.gitCommitMessageFull = properties.getProperty("git.commit.message.full");
		info.gitCommitMessageShort = properties.getProperty("git.commit.message.short");
		info.gitCommitTime = properties.getProperty("git.commit.time");
		
		return info;
	}
	
	// =========================================================================
	
	public String getGroupId()
	{
		return groupId;
	}
	
	public String getArtifaceId()
	{
		return artifaceId;
	}
	
	public String getProjectName()
	{
		return projectName;
	}
	
	public String getProjectVersion()
	{
		return projectVersion;
	}
	
	public String getVendor()
	{
		return vendor;
	}
	
	public String getGitBranch()
	{
		return gitBranch;
	}
	
	public String getGitBuildUserName()
	{
		return gitBuildUserName;
	}
	
	public String getGitBuildUserEmail()
	{
		return gitBuildUserEmail;
	}
	
	public String getGitBuildTime()
	{
		return gitBuildTime;
	}
	
	public String getGitCommitId()
	{
		return gitCommitId;
	}
	
	public String getGitCommitIdAbbrev()
	{
		return gitCommitIdAbbrev;
	}
	
	public String getGitCommitUserName()
	{
		return gitCommitUserName;
	}
	
	public String getGitCommitUserEmail()
	{
		return gitCommitUserEmail;
	}
	
	public String getGitCommitMessageFull()
	{
		return gitCommitMessageFull;
	}
	
	public String getGitCommitMessageShort()
	{
		return gitCommitMessageShort;
	}
	
	public String getGitCommitTime()
	{
		return gitCommitTime;
	}
	
	// =========================================================================
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		if (groupId != null)
			sb.append("groupId               : " + groupId + "\n");
		
		if (artifaceId != null)
			sb.append("artifaceId            : " + artifaceId + "\n");
		
		if (projectName != null)
			sb.append("projectName           : " + projectName + "\n");
		
		if (projectVersion != null)
			sb.append("projectVersion        : " + projectVersion + "\n");
		
		if (vendor != null)
			sb.append("vendor                : " + vendor + "\n");
		
		if (gitBranch != null)
			sb.append("gitBranch             : " + gitBranch + "\n");
		
		if (gitBuildUserName != null)
			sb.append("gitBuildUserName      : " + gitBuildUserName + "\n");
		
		if (gitBuildUserEmail != null)
			sb.append("gitBuildUserEmail     : " + gitBuildUserEmail + "\n");
		
		if (gitBuildTime != null)
			sb.append("gitBuildTime          : " + gitBuildTime + "\n");
		
		if (gitCommitId != null)
			sb.append("gitCommitId           : " + gitCommitId + "\n");
		
		if (gitCommitIdAbbrev != null)
			sb.append("gitCommitIdAbbrev     : " + gitCommitIdAbbrev + "\n");
		
		if (gitCommitUserName != null)
			sb.append("gitCommitUserName     : " + gitCommitUserName + "\n");
		
		if (gitCommitUserEmail != null)
			sb.append("gitCommitUserEmail    : " + gitCommitUserEmail + "\n");
		
		if (gitCommitMessageFull != null)
			sb.append("gitCommitMessageFull  : " + gitCommitMessageFull + "\n");
		
		if (gitCommitMessageShort != null)
			sb.append("gitCommitMessageShort : " + gitCommitMessageShort + "\n");
		
		if (gitCommitTime != null)
			sb.append("gitCommitTime         : " + gitCommitTime + "\n");
		
		return sb.toString();
	}
}
