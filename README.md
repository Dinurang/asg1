# Sun Travels Contract and Reservation System

Sun Travels is a two-part application for a travel agency that receives hotel
contracts on paper, applies a markup, and sells rooms through a call centre.
The application replaces manual calculations with a searchable contract system.

The project contains:

- A Java 21 Spring Boot REST API.
- An Angular reservation-agent and contract-management interface.
- An H2 file database for local persistence.
- Unit tests covering pricing and room availability rules.

## What the system does

### Contract management

The contract screen allows an administrator to load and view:

- Hotel name.
- Contract validity start and end dates.
- Markup percentage.
- Room type name.
- Price per person per night.
- Number of rooms.
- Maximum adults per room.

Contracts are stored in the local H2 database and remain available after the
backend is restarted.

### Reservation search

The reservation-agent screen accepts:

- Check-in date.
- Number of nights.
- One or more rooms.
- Number of adults in each room.

For each matching contract and room type, the application displays:

- Hotel name.
- Room type.
- Calculated marked-up price.
- Available or Not available status.

The price calculation is:

```text
hotel rate x (1 + markup percentage / 100) x number of nights x total adults
```

Availability requires all of the following:

1. The requested stay is within the contract validity period.
2. The hotel has enough rooms for the requested number of rooms.
3. Every requested room's adult count is within the room type's maximum occupancy.

## Project structure

```text
asg1/
├── pom.xml                         Spring Boot/Maven configuration
├── README.md                       This guide
├── .gitignore
├── src/
│   ├── main/java/com/suntravels/
│   │   ├── SunTravelsApplication.java
│   │   ├── api/                    REST error handling
│   │   ├── contract/               Contract entities, API, and service
│   │   └── search/                 Reservation search and pricing
│   ├── main/resources/
│   │   └── application.yml         Server and H2 configuration
│   └── test/java/                  Backend tests
└── frontend/
    ├── package.json                Angular scripts and dependencies
    ├── package-lock.json           Reproducible npm dependency versions
    ├── angular.json
    ├── proxy.conf.json             Routes /api to localhost:8080
    └── src/
        └── app/                    Angular UI
```

Generated directories such as `target/`, `frontend/node_modules/`,
`frontend/dist/`, `frontend/.angular/`, and `data/` are intentionally ignored
by Git.

## Requirements

Install these on the office PC if permitted:

- Git.
- JDK 21 or a newer compatible JDK.
- Apache Maven 3.9 or newer.
- Node.js 20 or newer, including npm.

The current development machine was validated with Java 25, Maven 3.9, and
Node.js 22. Angular is installed locally in `frontend/node_modules`, so a
global Angular CLI installation is not required.

Check installed tools in PowerShell:

```powershell
java -version
mvn -version
node --version
npm --version
git --version
```

## Fresh setup on Windows

### 1. Get the source code

Using HTTPS:

```powershell
git clone https://github.com/Dinurang/asg1.git
Set-Location asg1
```

If the repository was copied as a ZIP, extract it, open PowerShell in the
project folder, and skip `git clone`.

Confirm that the project root contains `pom.xml` and `frontend\package.json`:

```powershell
Get-ChildItem
Get-ChildItem frontend
```

### 2. Build and test the backend

From the project root:

```powershell
mvn clean test
```

This downloads Maven dependencies into the user's Maven cache, compiles the
Java source, and runs the unit tests.

If `mvn` is blocked but Maven Wrapper files are available in a future checkout,
use `.\mvnw.cmd clean test`. This repository currently uses the installed Maven
command.

### 3. Install and build the frontend

```powershell
Set-Location frontend
npm ci
npm run build
```

Use `npm ci` on a clean machine because it installs the exact versions in
`frontend/package-lock.json`. Use `npm install` only when intentionally
changing dependencies.

Return to the project root when needed:

```powershell
Set-Location ..
```

### 4. Run the application

Open two PowerShell windows.

Window 1, from the project root:

```powershell
mvn spring-boot:run
```

Wait until Spring Boot reports that it started on port 8080.

Window 2:

```powershell
Set-Location frontend
npm start
```

Open this address in a browser:

```text
http://localhost:4200
```

The Angular development proxy sends browser requests beginning with `/api` to
the backend at `http://localhost:8080`.

Stop either process with `Ctrl+C`.

## Office PC and administrator restrictions

### If software installation is blocked

Ask the administrator or IT team to provide:

- A supported JDK 21+ installation.
- Maven 3.9+.
- Node.js 20+.
- Git.
- Access to `https://repo.maven.apache.org` and `https://registry.npmjs.org`.

Do not bypass company security controls. If downloads are blocked, an
administrator can download the dependencies on an approved machine and provide
an approved internal artifact mirror or pre-populated Maven/npm caches.

### If PowerShell script execution is blocked

This project does not require a PowerShell profile or custom script. Run
commands directly in `cmd.exe`, or ask IT to allow the standard Node and Maven
commands. Avoid changing the machine-wide execution policy without approval.

### If Maven dependency downloads are blocked

The backend needs Maven Central access on the first build. Ask for the Maven
repository URL to be allow-listed or configure the company's approved Maven
mirror in `%USERPROFILE%\.m2\settings.xml`.

After dependencies are cached, offline verification can be attempted:

```powershell
mvn -o test
```

Offline mode works only if every required dependency is already cached.

### If npm registry access is blocked

Ask for the approved npm mirror, then configure it for the current user:

```powershell
npm config set registry https://<approved-company-npm-mirror>/
Set-Location frontend
npm ci
```

Do not commit a company-specific registry URL into this repository.

## REST API

Base URL:

```text
http://localhost:8080/api
```

### List contracts

```text
GET /contracts
```

### Create a contract

```text
POST /contracts
Content-Type: application/json
```

```json
{
  "hotelName": "Hilton Colombo",
  "validFrom": "2026-01-01",
  "validTo": "2026-12-31",
  "markupPercent": 15,
  "roomTypes": [
    {
      "name": "Standard",
      "pricePerPersonPerNight": 35,
      "rooms": 15,
      "maxAdults": 2
    },
    {
      "name": "Sea View",
      "pricePerPersonPerNight": 55,
      "rooms": 8,
      "maxAdults": 3
    }
  ]
}
```

### Update or delete a contract

```text
PUT    /contracts/{id}
DELETE /contracts/{id}
```

### Search contracts

```text
POST /search
Content-Type: application/json
```

```json
{
  "checkIn": "2026-06-01",
  "nights": 2,
  "rooms": [
    { "adults": 2 },
    { "adults": 1 }
  ]
}
```

For the sample Standard room, the result price is:

```text
35 x 1.15 x 2 nights x 3 adults = 241.50
```

## API smoke tests without extra tools

After starting the backend, use PowerShell:

```powershell
Invoke-RestMethod `
  -Uri http://localhost:8080/api/contracts `
  -Method Get
```

Create a sample contract:

```powershell
$body = @{
  hotelName = "Hilton Colombo"
  validFrom = "2026-01-01"
  validTo = "2026-12-31"
  markupPercent = 15
  roomTypes = @(
    @{
      name = "Standard"
      pricePerPersonPerNight = 35
      rooms = 15
      maxAdults = 2
    }
  )
} | ConvertTo-Json -Depth 5

Invoke-RestMethod `
  -Uri http://localhost:8080/api/contracts `
  -Method Post `
  -ContentType "application/json" `
  -Body $body
```

## Verification commands

Run these from the project root:

```powershell
mvn clean test
Set-Location frontend
npm ci
npm run build
Set-Location ..
```

Expected results:

- Maven reports `BUILD SUCCESS`.
- Backend tests report zero failures.
- Angular reports `Application bundle generation complete`.

## Common problems

### `mvn` is not recognized

Install Maven and add its `bin` directory to the user PATH, then open a new
terminal. Confirm with `mvn -version`.

### `java` is not recognized

Install a JDK, not only a JRE. Configure `JAVA_HOME` to the JDK folder and add
`%JAVA_HOME%\bin` to PATH, then open a new terminal.

### Port 8080 or 4200 is already in use

Find the process:

```powershell
Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
Get-NetTCPConnection -LocalPort 4200 -ErrorAction SilentlyContinue
```

Stop only a process that you have identified and are authorized to stop, or
change the application port in `src/main/resources/application.yml` and the
Angular proxy target accordingly.

### The browser shows an empty contract list

This is expected on a new installation. Open **Manage contracts**, save a
contract, then perform a reservation search.

### The frontend cannot reach `/api`

Ensure both processes are running. The backend must be on port 8080 and the
frontend must be started with `npm start`, which uses `proxy.conf.json`.

### The database needs to be reset

Stop the backend first. The local database is stored under `data/`. Delete only
the specific files inside that directory if a reset is required, then restart
the backend. Existing contract data will be lost.

## Git workflow

Check changes:

```powershell
git status
git diff --stat
```

Stage and commit:

```powershell
git add .gitignore README.md pom.xml src frontend
git commit -m "Implement Sun Travels contract and reservation system" `
  -m "Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>"
```

Push to the configured remote:

```powershell
git push origin main
```

If authentication is blocked on the office PC, ask IT to approve GitHub access
or use the organization's approved Git credentials/token process. Never put a
password, token, or secret in source code or commit history.
