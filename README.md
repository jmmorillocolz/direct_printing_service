# Direct Printing Services
Alternative to an applet to print tickets on any printer available on the client.
Run as Java Web Start application. Prints directly without displaying dialog box.

 In this version pass parameters like: printer name, print values, scale, and text to be printed.
 In future versions will be added other features.

 This JAVA application only works when it's invoked from a .jnlp file

 ## Main requirements:
 * Customer Side:
  - Mozilla Firefox browser (recommended)
  - Java 1.7 or higher
  - OS: Windows 7 or higher, and Linux
 
 * Server Side:
  - Apache Server 2
  - jnlp file with the proper configuration
 
 * Remarks: Compile and sign required to run properly.

# .jnlp configuration

Your jnlp file should contain the following:

* Have all execute permissions

```[xml]
<all-permissions/>
</security>
```
* In the "application-desc" directive add the 4 arguments necessary for the execution of the .jar.
	In this example we add the parameters with dynamic values from PHP.

```[xml]
<application-desc main-class="DirectPrintingService">
    <argument><?php echo urlencode(gzinflate(urldecode($html)));?></argument>
    <argument><?php echo urlencode($printername);?></argument>
	<argument><?php echo urlencode($printervalues);?></argument>
    <argument><?php echo urlencode($scale);?></argument>
</application-desc>
```

For more information on jnlp files and Java Web Start technology visit
[This Link](https://docs.oracle.com/javase/tutorial/deployment/webstart/index.html)


