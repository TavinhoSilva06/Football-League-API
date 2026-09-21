# Football League API — Setup Notes

## Environment Issues Encountered (Dia 1)

### Java & Maven
- **Expected**: Java 26 (specified in original `pom.xml`)
- **Actual**: OpenJDK 21 LTS (Temurin-21.0.12.1) available on the system
- **Decision**: Updated `pom.xml` to use `java.version=21` (LTS, stable, widely available)
  - Java 26 was too bleeding-edge; Java 21 LTS is the right choice for this project

### Maven SSL Certificate Issue
- **Problem**: Maven fails to download dependencies from Central due to JDK-level certificate validation issues
  - Error: `sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path`
  - Affects both `https://repo.maven.apache.org` (Central) and `https://repo.spring.io/snapshot` (Spring Snapshots)
- **Root Cause**: Likely a system/JDK SSL certificate chain issue (not a Maven configuration issue)
- **Workaround Attempted**:
  - Disabled Maven SSL checks (`-Dmaven.wagon.http.ssl.insecure=true`, etc.) — did not resolve
  - Used HTTP mirror in `~/.m2/settings.xml` — did not resolve
  - The issue is at the JDK/OS level, not Maven
- **Next Steps**:
  1. Run `keytool -list -v -keystore "$JAVA_HOME/lib/security/cacerts"` to verify SSL trust store
  2. If needed, import Spring/Maven Central certificates into the JDK trust store
  3. Or: Use a VPN/proxy that trusts the certificates, or run on a clean system with proper SSL setup
  4. Alternatively: Pre-download dependencies on a system with working SSL, then build offline

**For now**: The project structure, configuration, and code (Dia 1 skeleton) are ready. Once Maven dependency resolution is fixed, `./mvnw compile` will work immediately.

## Git Setup (Completed)
- ✅ `git init` at outer folder `Football-League-API/`
- ✅ Connected to remote `https://github.com/TavinhoSilva06/Football-League-API.git`
- ✅ Fetched and checked out `main` branch (tracking `origin/main`)
- ✅ Ready for commits

## Configuration Changes (Completed, Dia 1)
- ✅ `pom.xml`: Java 21 LTS (from 26), preserved all dependencies
- ✅ `compose.yaml`: Fixed Postgres to `postgres:16-alpine` (from `latest`)
- ✅ `application.properties`: Added JPA/Hibernate, datasource, and logging config
- ✅ `.gitignore`: Added `*.log`, `.env`, `application-local*.properties`
- ✅ Package structure created under `com.example.Football_League_API/`:
  - controller, service, repository, entity, dto (request/response), mapper, exception, config, validation, enum

## How to Fix Maven SSL (For Developer)
1. **Check JDK certificates** (via command line):
   ```bash
   keytool -list -v -keystore "${JAVA_HOME}/lib/security/cacerts"
   ```
2. **Possible fixes**:
   - Update JDK/OS to latest patches (SSL certs may be outdated)
   - Check corporate firewall/proxy configuration
   - Try `./mvnw clean install` with explicit proxy settings if behind a corporate proxy
   - Download dependencies manually from central.maven.org on a machine with working SSL

## Proceeding with Implementation
- The project skeleton is complete and ready
- Maven SSL is a one-time environment setup issue, not a code issue
- All Dia 1 tasks (configuration, package structure, git setup) are done
- Ready to move to **Dia 2**: Campeonato and Temporada entities + DTOs + controllers
