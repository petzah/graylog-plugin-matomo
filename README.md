Graylog plugin for Matomo
=========================

**Required Graylog version:** 2.4.4 and later

## Use cases

* Forward http logs from multiple reverse proxy servers to central Graylog and send them Matomo (former Piwik).
  Example: All http logs from nginx reverse proxy servers are forwarded into central
  Graylog location for initial analysis and basic tracking.
  See https://github.com/makeITyourway/graylog2_nginx_acecss.log_json project how to send nginx to graylog

* Use Graylog as a filter before Matomo tracker. Forward only interesting items to Matomo.

## Installation

[Download the plugin](https://github.com/petzah/graylog-plugin-matomo/releases)
and place the `.jar` file in your Graylog plugin directory. The plugin directory
is the `plugins/` folder relative from your `graylog-server` directory by default
and can be configured in your `graylog.conf` file.

Restart `graylog-server` and you are done.

## Usage

### Configuring Graylog

In Graylog, go to the outputs configuration of a stream and add a new "Matomo
output" like this:

![](https://github.com/petzah/graylog-plugin-matomo/blob/master/images/screenshot1.png)

All messages coming into that stream should now be forwarded to your Matomo setup
in realtime.
See https://matomo.org/faq/general/faq_114/ how to get an api token.

## Build

This project is using Maven and requires Java 8 or higher.

You can build a plugin (JAR) with `mvn package`.

DEB and RPM packages can be build with `mvn jdeb:jdeb` and `mvn rpm:rpm` respectively.

## Plugin Release

We are using the maven release plugin:

```
$ mvn release:prepare
[...]
$ mvn release:perform
```

This sets the version numbers, creates a tag and pushes to GitHub. TravisCI will build the release artifacts and upload to GitHub automatically.
