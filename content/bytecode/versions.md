---
title: Class File Versions
description: Class file version for each JDK release
---

Each JDK Release comes with its class file version. Class files are backward
compatible. But class files compiled for newer JDK releases cannot be executed
and will result in an `UnsupportedClassVersionError`.

A class file version consists of a _major_ and a _minor_ version:
`<major>.<minor>`\
For recent JDK releases, the major version is incremented for every release
and the minor version is 0. The only exception are class files which use
_preview features_ of that JDK release. In that case the minor version is
65535. A class file which uses preview features can only be loaded by the
JDK release matching the major version. Loading such classes with a newer
JDK release is not permitted.

The following table lists class file versions for each JDK release. Follow the
linked JDK information to find download options for a suitable JDK.

{{< classfileversionslist >}}
