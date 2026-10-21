# BITES — UNCOMMON DIGITAL — clean consolidated edition

This is the **standalone V2 project** consolidating V1 customer menu/cart/checkout with V2 authentication, restaurant, driver and admin interfaces. **Do not copy it over the old V1 Java source folder.** Previous V1 entity files conflict with V2's `Model.java`.

## Easiest Windows start

1. Extract the ZIP into a **new empty folder** (not into `bites-v1-github`).
2. Start Docker Desktop (Linux containers).
3. Double-click `START-BITES.bat`.
4. Open **http://localhost:8081**. Using port 8081 prevents confusing it with your older V1 on port 8080.
5. If it fails, run `VIEW-LOGS.bat`. `STOP-BITES.bat` stops containers **without deleting data**.

Equivalent PowerShell commands (from this folder):

```powershell
docker compose --progress plain build app
docker compose up -d
docker compose ps
docker compose logs --tail=80 app
```

## Demo accounts

All four use password `BitesDemo2026!` (local demo only):

- `customer@bites.local`
- `restaurant@bites.local`
- `driver@bites.local`
- `admin@bites.local`

The app seeds sample menu items automatically in its **own fresh PostgreSQL volume** (`bites-uncommon-final_bites_pgdata`). It does not modify the previous V1 volume. Checkout persists orders; **there is no live card payment, GPS, or courier integration**. Orders refresh by polling rather than WebSockets. Do not expose this demo with default credentials to the public internet.

## Notes for GitHub

This ZIP is a clean standalone snapshot with no `.git` directory. To replace V1 in your existing Git repository **without keeping conflicting V1 files**, commit your V1 changes first, create a backup, then copy this snapshot over the repository while removing obsolete V1 source files. An easier option is publishing to a new repository. Do not run `git init` inside your old repository.

## Verification and limitations

The V2 source was previously built with Java 21, 9 source files compiled, and two basic security tests passed **after obsolete V1 Java files were removed** (from the user-supplied Docker build log, 21 October 2026). This consolidated archive preserves those V2 Java source files; static checks here validate structure and frontend JS syntax, but full Docker startup and integration tests must be run on your computer. The demo isn't production hardened.
