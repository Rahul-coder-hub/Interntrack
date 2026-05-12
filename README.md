# InternTrack - Internship Review Manager

InternTrack is a simple full-stack Java application for managing internship project review records. It uses:

- Core Java and OOP concepts
- JDBC with H2 SQL database
- HTML and CSS frontend
- Built-in Java HTTP server

## Features

- Add internship review records
- View dashboard statistics
- Search interns by name, college, domain, or status
- Update review status
- Delete records
- Store data in a real SQL database

## Run Locally

Double-click:

```text
scripts\run.bat
```

Then open:

```text
http://localhost:8080
```

## Access From Another System

For another laptop/mobile on the same Wi-Fi:

1. First run this once as administrator:

```text
scripts\allow-firewall.bat
```

2. Start the app:

```text
scripts\run.bat
```

3. Keep the black command window open.
4. Share the `Network link` printed in that window.

You can also find your IPv4 address manually:

```powershell
ipconfig
```

On the other system, open:

```text
http://YOUR_IPV4_ADDRESS:8080
```

Example:

```text
http://192.168.1.5:8080
```

Important: `192.168.x.x` links work only on the same Wi-Fi/LAN. If your friend is in a different place or on mobile data, use a hosting platform or a tunneling tool such as ngrok/Cloudflare Tunnel.

## SQL

The database schema is in:

```text
database/schema.sql
```

The app runs these SQL statements through JDBC when it starts.
