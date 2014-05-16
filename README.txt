How to execute OSIT (Open Source Inspect Tool by OSE, Samsung)

1. Below are necessary for running OSIT
 - Java runtime (above v1.6)
 - Protex SDK jars
   : copy protex sdk to [OSIT Install Dir]\lib\protexsdk
     1. create "protexsdk" directory in [OSIT Install Dir]\lib
     2. copy *.jar in Protex SDK to "protexsdk" directory

2. Execute com.sec.ose.osi.UIMain
 - If you use proxy, enter the proxy info to setting file("config.ini")
   : Proxy is used to view original source code, to connect with server.
     Proxy_Server_IP : Proxy server IP (eg. Proxy_Server_IP=10.20.111.222)
     Proxy_Server_Port : Proxy server port (eg. Proxy_Server_IP=8080)
     Proxy_UsedBy_Server : If you have to connect with server via proxy, 
                           then the value is 'Y' (eg. Proxy_UsedBy_Server=Y (or N))