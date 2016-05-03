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
import java.util.Properties;

public class GitRepositoryState
{
	public static GitRepositoryState getGitRepositoryState(String resourceName) throws IOException
	{
		InputStream resourceAsStream = GitRepositoryState.class.getClassLoader()
				.getResourceAsStream(resourceName);

		if (resourceAsStream == null)
			throw new IOException("Resource not found: " + resourceName);

		return getGitRepositoryState(resourceAsStream);
	}

	private static GitRepositoryState getGitRepositoryState(InputStream resource) throws IOException
	{
		if (resource == null)
			throw new NullPointerException();
		Properties properties = new Properties();
		properties.load(resource);
		return new GitRepositoryState(properties);
	}

	// =========================================================================

	public final String tags;

	public final String branch;

	public final String dirty;

	public final String remoteOriginUrl;

	public final String commitId;

	public final String commitIdAbbrev;

	public final String describe;

	public final String describeShort;

	public final String commitUserName;

	public final String commitUserEmail;

	public final String commitMessageFull;

	public final String commitMessageShort;

	public final String commitTime;

	public final String closestTagName;

	public final String closestTagCommitCount;

	public final String buildUserName;

	public final String buildUserEmail;

	public final String buildTime;

	public final String buildHost;

	public final String buildVersion;

	// =========================================================================

	public GitRepositoryState(Properties properties)
	{
		this.tags = valueToString(properties.get("git.tags"));
		this.branch = valueToString(properties.get("git.branch"));
		this.dirty = valueToString(properties.get("git.dirty"));
		this.remoteOriginUrl = valueToString(properties.get("git.remote.origin.url"));

		String commitId = valueToString(properties.get("git.commit.id.full"));
		if (commitId == null)
			commitId = valueToString(properties.get("git.commit.id"));
		this.commitId = commitId;
		this.commitIdAbbrev = valueToString(properties.get("git.commit.id.abbrev"));
		this.describe = valueToString(properties.get("git.commit.id.describe"));
		this.describeShort = valueToString(properties.get("git.commit.id.describe-short"));
		this.commitUserName = valueToString(properties.get("git.commit.user.name"));
		this.commitUserEmail = valueToString(properties.get("git.commit.user.email"));
		this.commitMessageFull = valueToString(properties.get("git.commit.message.full"));
		this.commitMessageShort = valueToString(properties.get("git.commit.message.short"));
		this.commitTime = valueToString(properties.get("git.commit.time"));
		this.closestTagName = valueToString(properties.get("git.closest.tag.name"));
		this.closestTagCommitCount = valueToString(properties.get("git.closest.tag.commit.count"));

		this.buildUserName = valueToString(properties.get("git.build.user.name"));
		this.buildUserEmail = valueToString(properties.get("git.build.user.email"));
		this.buildTime = valueToString(properties.get("git.build.time"));
		this.buildHost = valueToString(properties.get("git.build.host"));
		this.buildVersion = valueToString(properties.get("git.build.version"));
	}

	private static String valueToString(Object object)
	{
		if (object == null)
			return null;
		return String.valueOf(object);
	}

	// =========================================================================

	public String getTags()
	{
		return tags;
	}

	public String getBranch()
	{
		return branch;
	}

	public String getDirty()
	{
		return dirty;
	}

	public String getRemoteOriginUrl()
	{
		return remoteOriginUrl;
	}

	public String getCommitId()
	{
		return commitId;
	}

	public String getCommitIdAbbrev()
	{
		return commitIdAbbrev;
	}

	public String getDescribe()
	{
		return describe;
	}

	public String getDescribeShort()
	{
		return describeShort;
	}

	public String getCommitUserName()
	{
		return commitUserName;
	}

	public String getCommitUserEmail()
	{
		return commitUserEmail;
	}

	public String getCommitMessageFull()
	{
		return commitMessageFull;
	}

	public String getCommitMessageShort()
	{
		return commitMessageShort;
	}

	public String getCommitTime()
	{
		return commitTime;
	}

	public String getClosestTagName()
	{
		return closestTagName;
	}

	public String getClosestTagCommitCount()
	{
		return closestTagCommitCount;
	}

	public String getBuildUserName()
	{
		return buildUserName;
	}

	public String getBuildUserEmail()
	{
		return buildUserEmail;
	}

	public String getBuildTime()
	{
		return buildTime;
	}

	public String getBuildHost()
	{
		return buildHost;
	}

	public String getBuildVersion()
	{
		return buildVersion;
	}
}
