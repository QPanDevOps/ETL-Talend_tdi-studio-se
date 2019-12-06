---
version: 7.2.1
module: https://talend.poolparty.biz/coretaxonomy/42
product: https://talend.poolparty.biz/coretaxonomy/17
---

# TPS-3600

| Info             | Value |
| ---------------- | ---------------- |
| Patch Name       | Patch\_20191206\_TPS-3600\_v2 |
| Release Date     | 2019-12-06 |
| Target Version   | 20190620\_1446-V7.2.1 |
| Product affected | Talend Studio |

## Introduction

This is a self-contained patch.

**NOTE**: For information on how to obtain this patch, reach out to your Support contact at Talend.

## Fixed issues

This patch contains the following fixes:

- TPS-3592 tBigQueryBulkExec loading data from Google Cloud Storage to BigQuery failed after setting header (TBD-9578)
- TPS-3600 tBigQueryBulkExec get error when run with big file (TBD-8853)

## Prerequisites

Consider the following requirements for your system:

- Talend Studio 7.2.1 must be installed.
- Patch_20190923_TPS-3356_v3-7.2.1.zip must be installed.

## Installation

### Installing the patch using Software update

1) Logon TAC and switch to Configuration->Software Update, then enter the correct values and save referring to the documentation: https://help.talend.com/reader/f7Em9WV_cPm2RRywucSN0Q/j9x5iXV~vyxMlUafnDejaQ

2) Switch to Software update page, where the new patch will be listed. The patch can be downloaded from here into the nexus repository.

3) On Studio Side: Logon Studio with remote mode, on the logon page the Update button is displayed: click this button to install the patch.

### Installing the patch using Talend Studio

1) Create a folder named "patches" under your studio installer directory and copy the patch .zip file to this folder.

2) Restart your studio: a window pops up, then click OK to install the patch, or restart the commandline and the patch will be installed automatically.

### Installing the patch using Commandline

Execute the following commands:

1. Talend-Studio-win-x86_64.exe -nosplash -application org.talend.commandline.CommandLine -consoleLog -data commandline-workspace startServer -p 8002 --talendDebug
2. initRemote {tac_url} -ul {TAC login username} -up {TAC login password}
3. checkAndUpdate -tu {TAC login username} -tup {TAC login password}

## Affected files for this patch

The following files are installed by this patch:

- plugins/org.talend.designer.components.localprovider/components/tBigQueryBulkExec/tBigQueryBulkExec_begin.javajet