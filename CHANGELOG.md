# Change Log

## Unreleased
### Changed
- bin/release script accepts -a option to specify alternative deployment repository
- bin/release script "deploy-signed-release-locally" is now optional

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
