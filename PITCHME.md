# ESDK
# Example Project

---

### build.gradle
#### The new plugin mechanism

+++?code=build.gradle&lang=groovy
@[1-7]
@[16]

#####ESDK Plugin on [Gradle Plugins](https://plugins.gradle.org/plugin/de.abas.esdk)

---

### build.gradle 
#### The esdk closure

+++?code=build.gradle&lang=groovy
@[18]
@[19-20]
@[21-23]
@[24]
@[25]
@[26]
@[27]
@[28]
@[29]
@[30-31]

---

### build.gradle
#### Dependencies

+++?code=build.gradle&lang=groovy
@[111-115]

---

### gradle.properties
#### Defining client access
+++?code=gradle.properties.template&lang=properties
@[1-3]
@[5-9]
@[11-14]
@[16-20]
@[22]

---

### docker-compose.yml
#### erp

+++?code=docker-compose.yml&lang=yaml
@[8-25]
@[9]

---

### docker-compose.yml
#### nexus

+++?code=docker-compose.yml&lang=yaml
@[27-32]

---
### docker-compose.yml
#### middleware

```yaml
version: "2"
services:
  erp:
    image: intra.registry.abas.sh/cloud-erp-neumand1:2016r4n13
    container_name: "erp"
    ports:
      - "6560:6550"
      - "11666:11666"
      - "8001:80"
      - "48592:48392"
      - "2205:22"
    logging:
      driver: json-file
      options:
        max-size: 5m

  middleware:
    image: intra.registry.abas.sh/mw:0.44.0
    container_name: "middleware"
    command:
      - "--edp"
      - "edp://erp:6550//abas/erp1/"
      - "--"
      - "--de.abas.mw.cors.enabled=true"
      - "--server.contextPath=/mw"
      - "--de.abas.mw.core.connection.default.initializeMetaDataOnStartup=true"
    ports:
      - "10000:8080"
    depends_on:
      - erp
    logging:
      driver: json-file
      options:
        max-size: 5m

  nexus:
    image: sonatype/nexus:oss
    container_name: "nexus"
    ports:
      - "8081:8081"
```
@[17-34]

---

### Documentation and Help
#### [Documentation](https://esdk-documentation.test.us-east-1.api.abas.ninja)
#### [ESDK Project Builder](https://esdk-project-builder.test.us-east-1.api.abas.ninja)

---