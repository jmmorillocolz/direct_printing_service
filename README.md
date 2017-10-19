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

Su archivo jnlp debe contener lo siguiente:

* Tener todos los permisos de ejecución

```[xml]
<all-permissions/>
</security>
```
* En la directiva "application-desc" agregar los 4 argumentos necesarios para la ejecucion del .jar.
	En este ejemplo agregamos los parametros con valores dinamicos desde PHP.

```[xml]
<application-desc main-class="DirectPrintingService">
    <argument><?php echo urlencode(gzinflate(urldecode($html)));?></argument>
    <argument><?php echo urlencode($printername);?></argument>
	<argument><?php echo urlencode($printervalues);?></argument>
    <argument><?php echo urlencode($scale);?></argument>
</application-desc>
```

para mayor informacion sobre los archivos jnlp y la tecnologia Java Web Start visite 
[Este Link](https://docs.oracle.com/javase/tutorial/deployment/webstart/index.html)


