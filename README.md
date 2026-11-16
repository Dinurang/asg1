# Sun Travels

Sun Travels is a Spring Boot and Angular application for loading hotel contracts and
searching reservation availability for call-centre agents.

## Features

- Load and view hotel contracts with validity dates, markup, room inventory, rates,
  and maximum occupancy.
- Search by check-in date, number of nights, room count, and adults per room.
- Enforce contract validity, room inventory, and occupancy rules.
- Calculate the customer price as:
  `hotel rate x (1 + markup / 100) x nights x total adults`.
- Keep the browser responsive while requests are in progress and show clear errors.

## Run locally

Start the backend from the project root:

```powershell
mvn spring-boot:run
```

In another terminal, start the Angular application:

```powershell
Set-Location frontend
npm install
npm start
```

Open `http://localhost:4200`. The backend API is available at
`http://localhost:8080/api`.

## API examples

Create a contract:

```json
POST /api/contracts
{
  "hotelName": "Hilton Colombo",
  "validFrom": "2026-01-01",
  "validTo": "2026-12-31",
  "markupPercent": 15,
  "roomTypes": [
    { "name": "Standard", "pricePerPersonPerNight": 35, "rooms": 15, "maxAdults": 2 }
  ]
}
```

Search:

```json
POST /api/search
{
  "checkIn": "2026-06-01",
  "nights": 2,
  "rooms": [{ "adults": 2 }, { "adults": 1 }]
}
```

## Verification

```powershell
mvn test
Set-Location frontend
npm run build
```

The backend uses an H2 file database under `data/` for local persistence. The
database is intentionally not committed.
# asg1
