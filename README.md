{\rtf1\ansi\ansicpg1252\cocoartf2867
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
{\*\expandedcolortbl;;}
\paperw11900\paperh16840\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0

\f0\fs24 \cf0 # UninaSwap \'96 Progetto Object Oriented (Java + Maven + PostgreSQL)\
\
Applicativo desktop sviluppato in **Java** con build tramite **Apache Maven**, con persistenza dati su **PostgreSQL**.  \
Il progetto implementa un marketplace universitario con **annunci** (vendita/scambio/regalo), **offerte**, **notifiche** e **report**.\
\
Repository (Version Control): https://github.com/mariobuffardi/uninaswap\
\
---\
\
## Requisiti\
- **Java (JDK 21)** (il progetto compila con release 21 da `pom.xml`)\
- **Apache Maven** (3.8+)\
- **PostgreSQL** (15+/16+)\
\
## Dipendenze principali\
- PostgreSQL JDBC Driver (42.7.4)\
- FlatLaf (3.7) + MigLayout Swing (11.4.2) per UI\
- JFreeChart (1.5.6) per grafici report\
\
---\
\
## Struttura del progetto\
- `src/main/java` \uc0\u8594  codice sorgente\
  - Main: `App.java`\
- `src/main/resources` \uc0\u8594  risorse (icone, immagini, ecc.)\
  - `images/annunci/...` \uc0\u8594  immagini degli annunci (path nel DB come percorsi relativi)\
- `db/`\
  - `01_schema.sql` \uc0\u8594  creazione schema (tabelle, vincoli, trigger, view, indici)\
  - `02_seed.sql` \uc0\u8594  popolamento dati demo (annunci/offerte/transazioni/foto)\
- `docs/OO_Progettazione.pdf` \uc0\u8594  design class diagram + 2 sequence diagram\
\
---\
\
## Setup Database (PostgreSQL)\
1. Crea un database vuoto (es. **UninaSwap**).\
2. Esegui gli script in questo ordine:\
   - `db/01_schema.sql`\
   - `db/02_seed.sql`\
\
---\
\
## Configurazione Connessione DB\
La connessione a PostgreSQL \'e8 gestita da `DBConnection.java`:\
\
- URL: `jdbc:postgresql://localhost:5432/UninaSwap`\
- User: `postgres`\
- Password: va impostata manualmente in `DBConnection.java` prima dell\'92esecuzione (nel progetto \'e8 vuota per non pubblicare credenziali).\
\
Aprire `src/main/java/it/unina/uninaswap/dao/util/DBConnection.java` e inserire la propria password di PostgreSQL.\
\
---}