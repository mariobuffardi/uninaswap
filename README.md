# UninaSwap – Progetto Object Oriented (Java + Maven + PostgreSQL)

Applicativo desktop sviluppato in **Java** con build tramite **Apache Maven**, con persistenza dati su **PostgreSQL**.  
Il progetto implementa un marketplace universitario con **annunci** (vendita/scambio/regalo), **offerte**, **notifiche** e **report**.

Repository (Version Control): https://github.com/mariobuffardi/uninaswap

---

## Requisiti
- **Java (JDK 21)** (il progetto compila con release 21 da `pom.xml`)
- **Apache Maven** (3.8+)
- **PostgreSQL** (15+/16+)

## Dipendenze principali
- PostgreSQL JDBC Driver (42.7.4)
- FlatLaf (3.7) + MigLayout Swing (11.4.2) per UI
- JFreeChart (1.5.6) per grafici report

---

## Struttura del progetto
- `src/main/java` → codice sorgente
  - Main: `App.java`
- `src/main/resources` → risorse (icone, immagini, ecc.)
  - `images/annunci/...` → immagini degli annunci (path nel DB come percorsi relativi)
- `db/`
  - `01_schema.sql` → creazione schema (tabelle, vincoli, trigger, view, indici)
  - `02_seed.sql` → popolamento dati demo (annunci/offerte/transazioni/foto)
- `docs/OO_Progettazione.pdf` → design class diagram + 2 sequence diagram

---

## Setup Database (PostgreSQL)
1. Crea un database vuoto (es. **UninaSwap**).
2. Esegui gli script in questo ordine:
   - `db/01_schema.sql`
   - `db/02_seed.sql`

> Nota: i path delle foto nel DB sono del tipo `images/annunci/...` e le immagini sono incluse nel progetto in `src/main/resources/images/annunci`.

---

## Configurazione Connessione DB
La connessione a PostgreSQL è gestita da `DBConnection.java`:

- URL: `jdbc:postgresql://localhost:5432/UninaSwap`
- User: `postgres`
- Password: va impostata manualmente in `DBConnection.java` prima dell’esecuzione (nel progetto è vuota per non pubblicare credenziali).

Aprire `src/main/java/it/unina/uninaswap/dao/util/DBConnection.java` e inserire la propria password di PostgreSQL.
