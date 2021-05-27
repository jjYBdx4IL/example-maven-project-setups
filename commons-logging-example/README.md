# commons-logging Example

## Conclusion

* No placeholder support (slf4j has it)
* No runtime configuration officially supported/intended, though mileage may very.
* Don't see any reason to use it over slf4j-api if one is looking for a backend-agnostic logging frontend.
* slf4j's isDebugEnabled reflects dynamic logging configuration changes in the WildFly server. Haven't tested this with commons-logging.
