# digit_assignment – Advocate Registry

**DIGIT Certified Developer Assignment:** Advocate Registry module.

| Item | Value |
|------|--------|
| **Module name** | digit_assignment |
| **Base package** | digit.academy.tutorial |
| **API spec** | advocate-api-0.1.0.yaml |
| **Application** | Port **8081**, context-path `/advocate` |

---

## APIs

The OpenAPI spec **advocate-api-0.1.0.yaml** defines three operations:

| Method | Path | Description |
|--------|------|-------------|
| POST | /advocate/v1/_create | Register a new advocate |
| POST | /advocate/v1/_update | Update advocate |
| POST | /advocate/v1/_search | Search advocates |

Request/response schemas (RequestInfo, AdvocateRequest, AdvocateResponse, AdvocateSearchRequest, etc.) are in the same YAML under `components.schemas`.

**Swagger UI:** When the app is running: **http://localhost:8081/advocate/swagger-ui/index.html** (or **http://localhost:8081/advocate/swagger-ui.html**).

---

## Project layout

```
digit_assignment/
├── advocate-api-0.1.0.yaml    # OpenAPI spec
├── src/main/java/digit/academy/tutorial/
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/main/     # Flyway DDL (eg_advocate)
├── build/
│   └── digit_assignment.jar   # Deliverable JAR
├── workflow/
│   └── workflow_config.json   # ADVOCATE_REGISTRATION (PRD-based)
├── mdms_data/pg/              # Tenant pg: Advocate, tenant, common-masters (MDMS + IDGen)
├── masters/
│   ├── AdvocateType.json      # Sample master (PROSECUTOR, PUBLIC_DEFENDER, etc.)
│   └── master_schema_advocate_type.json
├── persister/
│   └── persister_config.yaml  # save-advocate-application, update-advocate-application
├── indexer/
│   └── indexer_config.yaml    # Bonus
└── api_test/
    ├── Advocate_Registry.postman_collection.json
    └── Advocate_Registry.postman_environment.json
```

---

## Prerequisites

| Component | Requirement |
|-----------|--------------|
| Java | 17 |
| PostgreSQL | DB `digit_assignment`, port 5432 |
| Kafka | localhost:9092 |
| egov-mdms-service | port 8094 (run **before** IDGen and User) |
| egov-idgen | port 8285 |
| egov-user | port 8089 (for workflow _transition) |
| egov-workflow-v2 | port 8280 |
| egov-persister | port 8082 |

Default DB credentials in `application.properties`: `postgres` / `postgres`.

---

## Run order

Follow these steps **in this order**; later steps depend on earlier ones.

| Step | Action | Service / port | Notes |
|------|--------|----------------|-------|
| 1 | Create database | PostgreSQL **5432** | DB name: `digit_assignment`. |
| 2 | Start Kafka and create topics | Kafka **9092** | Topics: `save-advocate-application`, `update-advocate-application`, `save-wf-businessservice`, `update-wf-businessservice`. |
| 3 | Run MDMS | egov-mdms-service **8094** | Run **first** (before IDGen and User). Point at this repo’s `mdms_data`. |
| 4 | Run IDGen | egov-idgen **8285** | Use its own DB (e.g. `rainmaker_new`). Set `mdms.service.host=http://localhost:8094/`. |
| 5 | Run User service | egov-user **8089** | Set `--mdms.host=http://localhost:8094` and `--egov.mdms.host=http://localhost:8094`. Required for workflow _transition. |
| 6 | Run Workflow and register business service | egov-workflow-v2 **8280** | Use `workflow/workflow_config.json`: call _create and _update. |
| 7 | Run Persister | egov-persister **8082** | Set `egov.persist.yml.repo.path` to this repo’s `persister/persister_config.yaml`. |
| 8 | Build this app | — | `mvn clean package -DskipTests` (from project root). |
| 9 | Run this app | Advocate Registry **8081** | `java -jar build/digit_assignment.jar`. |
| 10 | Test APIs | — | Use **api_test/** (Postman) or Swagger UI: **http://localhost:8081/advocate/swagger-ui/index.html** |

For detailed setup and troubleshooting, refer to the [DIGIT Backend Developer Guide](https://core.digit.org/guides/developer-guide/backend-developer-guide).

---

## Testing the API (api_test)

The **api_test/** folder contains a Postman collection and environment to test the three Advocate APIs. Use them after the app is running (step 9).

### Files

| File | Purpose |
|------|---------|
| **Advocate_Registry.postman_collection.json** | Requests: Create Advocate, Update Advocate, Search Advocate |
| **Advocate_Registry.postman_environment.json** | Environment **Advocate Registry Local** with variables for host, tenant, and application number |

### Variables (environment)

| Variable | Default | Description |
|---------|---------|-------------|
| `hostWithPort` | `http://localhost:8081` | Base URL of the Advocate Registry app |
| `tenantId` | `pg` | Tenant ID for requests |
| `applicationNumber` | *(empty)* | Set from Create response; used by Update and Search |
| `authToken` | *(empty)* | Optional; leave empty for local testing |

### Steps to test

1. **Import in Postman**
   - **Collection:** File → Import → select `api_test/Advocate_Registry.postman_collection.json`
   - **Environment:** File → Import → select `api_test/Advocate_Registry.postman_environment.json`

2. **Select environment**  
   In the top-right dropdown, choose **Advocate Registry Local**.

3. **Run in order**
   - **Create Advocate** — Run first. From the response, copy the value of `advocates[0].applicationNumber`.
   - **Set variable** — In the environment, set `applicationNumber` to that value (or use a test script to save it automatically).
   - **Update Advocate** — Uses `applicationNumber` and sends workflow action VERIFY.
   - **Search Advocate** — Uses `applicationNumber` (and tenantId) to search.

4. **Optional:** If Create returns an error about user/UUID, create a CITIZEN user via egov-user (port 8089) first and set that user’s `uuid` in the Create request body under `RequestInfo.userInfo.uuid`.

**Swagger UI:** Alternatively, open **http://localhost:8081/advocate/swagger-ui/index.html** to try the same APIs from the browser.
