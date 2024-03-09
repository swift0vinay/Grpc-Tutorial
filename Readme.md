In order to convert/generate the proto code to java code manually
Perform following steps:

WINDOWS:
1. Download protoc compiler from https://github.com/protocolbuffers/protobuf/releases/tag/v25.3
2. Download plugin for generating service written in proto files as well. 
   Link: https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/1.62.2/
   (Go to maven repo, Click on any version and go into files, select binary for OS)

Commands:
1. To generate model files
   C:\Grpc\protoc-25.3-win64\bin\protoc.exe -I ./ --java_out=./proto-output/model .\src\main\proto\schema.proto 
2. To generate service files
   C:\Grpc\protoc-25.3-win64\bin\protoc.exe -I ./ --plugin=protoc-gen-grpc-java=D:\BackUp\Downloads\protoc-gen-grpc-java-1.62.2-windows-x86_64.exe --grpc-java_out=./proto-output/service .\src\main\proto\schema.proto