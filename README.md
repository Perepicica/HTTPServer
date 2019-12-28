# HTTPServer
## Getting started
```
gradle startService
```
Сервер запускается на порту 8080

## Example
1.       $curl http://localhost:8080?year=2020

         {"errorCode":200,"dataMessage":"14/09/20"}

2.       $curl http://localhost:8080?yer=2020

         {"errorCode":1,"dataMessage":"invalid parameter"}
   
3.       $curl http://localhost:8080?year=202t
               
         {"errorCode":2,"dataMessage":"invalid year"}
         
4.       $curl http://localhost:8080?year=123
               
         {"errorCode":2,"dataMessage":"invalid year"}
