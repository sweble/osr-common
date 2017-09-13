# Change Log

## 3.0.7 - 2017-09-13
### Changed
- Running maven gpg plugin verify phase, not in package phase

## 3.0.7 - 2017-09-13
### Changed
- Added jenkins profile which generates coverage reports when build in Jenkins.

## 3.0.6 - 2017-06-14
### Fixed
- Added missing descriptions to pom.xml files. The release to oss.sonatype.org 
  was failing with a validation error that complained about the missing 
  descriptions.

## 3.0.5 - 2017-02-06
### Fixed
- Fixed issue #1: "Comparison method violates its general contract" in
  VisitorLogic.sweepCache by replacing Java sort with quick select.

## 3.0.4 - 2017-01-16
### Changed
- Disabled Java 8 doclint by default in tooling pom
- bin/release script accepts -a option to specify alternative deployment
  repository
- bin/release script "deploy-signed-release-locally" is now optional
- bin/release script can work with master and develop branches that are called
  differently

## 3.0.3 - 2016-06-07
### Added
- BinaryPrefix can return value as double (getDoubleValue())

### Fixed
- NodeDeepComparer reported differences when ignored (xmlns) attributes were
  present
- NodeDeepComparer failed when comparing elements with and without prefix

## 3.0.2 - 2016-05-03
### Added
- Helper classes for metrics collection
  - AccumulatingRingBufferDouble
  - AccumulatingRingBufferLong
  - SpeedMeter
  - StopWatch2
- Helper class GitRepositoryState for parsing repository state as produced by
  the maven-git-commit-id-plugin
- NodeDeepComparer (subclass of DeepComparer) for org.w3c.dom.Node
