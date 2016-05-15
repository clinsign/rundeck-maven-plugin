# Rundeck Maven Plugin

## Usage
```
<build>
        <plugins>
            <plugin>
                <groupId>com.jstaormina</groupId>
                <artifactId>rundeck-maven-plugin</artifactId>
                <version>1.0.0</version>
                <configuration>
                    <url>[rundeck url]</url>
                    <token>[rundeck api token]</token>
                    <jobUuid>[rundeck job uuid]</jobUuid>
                    <nodeFilters>
                        <property>
                            <name>[node name]</name>
                            <value>[node value]</value>
                        </property>
                        ...
                    </nodeFilters>
                    <options>
                        <property>
                            <name>[option name]</name>
                            <value>[option Value]</value>
                        </property>
                        ...
                    </options>
                </configuration>
            </plugin>
            ...
        </plugins>
    </build>
```

## Example
```
<build>
        <plugins>
            <plugin>
                <groupId>com.jstaormina</groupId>
                <artifactId>rundeck-maven-plugin</artifactId>
                <version>1.0.0</version>
                <configuration>
                    <url>http://localhost:4440</url>
                    <token>3uKe9ji3zqMhnviTnkiOQRhmXRYIp6vW</token>
                    <jobUuid>b6437cde-3736-48ca-9512-43ae8b4ab294</jobUuid>
                    <nodeFilters>
                        <property>
                            <name>node1</name>
                            <value>node1.example.com</value>
                        </property>
                        <property>
                            <name>node2</name>
                            <value>node2.example.com</value>
                        </property>
                    </nodeFilters>
                    <options>
                        <property>
                            <name>serviceName</name>
                            <value>foo service</value>
                        </property>
                        <property>
                            <name>serviceVersion</name>
                            <value>1.2.1</value>
                        </property>
                    </options>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
