--
-- PostgreSQL database dump
--

-- Dumped from database version 16.9 (Postgres.app)
-- Dumped by pg_dump version 16.9 (Postgres.app)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: studente; Type: TABLE DATA; Schema: public; Owner: postgres
--

SET SESSION AUTHORIZATION DEFAULT;

ALTER TABLE public.studente DISABLE TRIGGER ALL;

INSERT INTO public.studente VALUES ('000000001', 's1@example.com', 'Mario', 'Rossi', 'pass1', 'M', true, true);
INSERT INTO public.studente VALUES ('000000002', 's2@example.com', 'Luisa', 'Bianchi', 'pass2', 'F', true, true);
INSERT INTO public.studente VALUES ('000000003', 's3@example.com', 'Giorgio', 'Verdi', 'pass3', 'Altro', true, true);


ALTER TABLE public.studente ENABLE TRIGGER ALL;

--
-- Data for Name: annuncio; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.annuncio DISABLE TRIGGER ALL;

INSERT INTO public.annuncio VALUES (2, 'Monitor 24"', 'Full HD', '2026-02-11', 'Vendita', 'Informatica', NULL, false, 90.00, true, true, '000000001');
INSERT INTO public.annuncio VALUES (3, 'Libri Programmazione', 'Scambio libri', '2026-02-11', 'Scambio', 'Libri', 'Libro di basi di dati', false, NULL, true, true, '000000001');
INSERT INTO public.annuncio VALUES (5, 'Cuffie Bluetooth', 'Ottime', '2026-02-11', 'Vendita', 'Informatica', NULL, false, 80.00, true, true, '000000002');
INSERT INTO public.annuncio VALUES (8, 'Calcolatrice', 'Funzionante', '2026-02-11', 'Regalo', 'Altro', NULL, false, NULL, true, true, '000000002');
INSERT INTO public.annuncio VALUES (9, 'Giacca', 'Taglia L', '2026-02-11', 'Vendita', 'Abbigliamento', NULL, false, 45.00, true, true, '000000003');
INSERT INTO public.annuncio VALUES (10, 'Lampada scrivania', 'LED', '2026-02-11', 'Vendita', 'Arredo', NULL, false, 15.00, true, true, '000000003');
INSERT INTO public.annuncio VALUES (11, 'Tastiera meccanica', 'Scambio', '2026-02-11', 'Scambio', 'Informatica', 'Mouse gaming', false, NULL, true, true, '000000003');
INSERT INTO public.annuncio VALUES (12, 'Appunti Analisi', 'PDF stampato', '2026-02-11', 'Regalo', 'Libri', NULL, false, NULL, true, true, '000000003');
INSERT INTO public.annuncio VALUES (13, 'Laptop', 'Condizioni: Ottime
intel i 5
16 gb di ram
500gbssd', '2026-02-11', 'Vendita', 'Informatica', NULL, false, 600.00, true, false, '000000001');
INSERT INTO public.annuncio VALUES (14, 'Jordan 1 High chicago', 'Condizioni ds
taglia 44eu/10us', '2026-02-11', 'Scambio', 'Abbigliamento', 'Jordan 1 High Dark Mocha', false, NULL, true, true, '000000001');
INSERT INTO public.annuncio VALUES (4, 'Felpa', 'Taglia M', '2026-02-11', 'Regalo', 'Abbigliamento', NULL, true, NULL, true, true, '000000001');
INSERT INTO public.annuncio VALUES (1, 'Chitarra classica', 'Buone condizioni', '2026-02-11', 'Vendita', 'Strumenti_musicali', NULL, true, 120.00, true, true, '000000001');
INSERT INTO public.annuncio VALUES (7, 'Scrivania', 'Piccola', '2026-02-11', 'Scambio', 'Arredo', 'Sedia da studio', true, NULL, true, true, '000000002');
INSERT INTO public.annuncio VALUES (6, 'Libro Inglese B2', 'Usato poco', '2026-02-11', 'Vendita', 'Libri', NULL, true, 25.00, true, true, '000000002');


ALTER TABLE public.annuncio ENABLE TRIGGER ALL;

--
-- Data for Name: indirizzo; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.indirizzo DISABLE TRIGGER ALL;

INSERT INTO public.indirizzo VALUES (1, 'Via Roma', 'Napoli', 80100, 10, 'Italia', '000000001');
INSERT INTO public.indirizzo VALUES (2, 'Via Toledo', 'Napoli', 80134, 25, 'Italia', '000000002');
INSERT INTO public.indirizzo VALUES (3, 'Via Partenope', 'Napoli', 80121, 5, 'Italia', '000000003');


ALTER TABLE public.indirizzo ENABLE TRIGGER ALL;

--
-- Data for Name: offerta; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.offerta DISABLE TRIGGER ALL;

INSERT INTO public.offerta VALUES (1, '2026-02-11', 'In_Attesa', NULL, 'ti propongo questo scambio', 'Jordan 1 high purple', 14, '000000002');
INSERT INTO public.offerta VALUES (3, '2026-02-11', 'Accettata', NULL, 'mi serveeeee', NULL, 4, '000000002');
INSERT INTO public.offerta VALUES (2, '2026-02-11', 'Accettata', 120.00, NULL, NULL, 1, '000000002');
INSERT INTO public.offerta VALUES (4, '2026-02-11', 'In_Attesa', 80.00, NULL, NULL, 5, '000000001');
INSERT INTO public.offerta VALUES (7, '2026-02-11', 'In_Attesa', 3.00, NULL, NULL, 10, '000000001');
INSERT INTO public.offerta VALUES (8, '2026-02-11', 'In_Attesa', 600.00, NULL, NULL, 13, '000000003');
INSERT INTO public.offerta VALUES (9, '2026-02-11', 'In_Attesa', NULL, NULL, 'felpa supreme rossa', 14, '000000003');
INSERT INTO public.offerta VALUES (6, '2026-02-11', 'Accettata', NULL, 'ti propongo questo', 'materasso', 7, '000000001');
INSERT INTO public.offerta VALUES (5, '2026-02-11', 'Accettata', 25.00, NULL, NULL, 6, '000000001');


ALTER TABLE public.offerta ENABLE TRIGGER ALL;

--
-- Data for Name: transazione; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.transazione DISABLE TRIGGER ALL;

INSERT INTO public.transazione VALUES (1, '2026-02-11', NULL, 4, 3, '000000001', '000000002');
INSERT INTO public.transazione VALUES (2, '2026-02-11', 120.00, 1, 2, '000000001', '000000002');
INSERT INTO public.transazione VALUES (3, '2026-02-11', NULL, 7, 6, '000000002', '000000001');
INSERT INTO public.transazione VALUES (4, '2026-02-11', 25.00, 6, 5, '000000002', '000000001');


ALTER TABLE public.transazione ENABLE TRIGGER ALL;

--
-- Data for Name: consegna; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.consegna DISABLE TRIGGER ALL;

INSERT INTO public.consegna VALUES (1, NULL, NULL, NULL, NULL, true, false, 1, 2);
INSERT INTO public.consegna VALUES (2, NULL, NULL, NULL, NULL, true, false, 2, 2);
INSERT INTO public.consegna VALUES (3, NULL, NULL, NULL, NULL, true, false, 3, 1);
INSERT INTO public.consegna VALUES (4, NULL, NULL, NULL, NULL, true, false, 4, 1);


ALTER TABLE public.consegna ENABLE TRIGGER ALL;

--
-- Data for Name: foto; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.foto DISABLE TRIGGER ALL;

INSERT INTO public.foto VALUES (1, 'images/annunci/1770832880957_629504a3.jpg', true, 13);
INSERT INTO public.foto VALUES (2, 'images/annunci/1770832880976_11c59060.jpg', false, 13);
INSERT INTO public.foto VALUES (3, 'images/annunci/1770832880976_cf5daa53.jpg', false, 13);
INSERT INTO public.foto VALUES (4, 'images/annunci/1770833464973_87e4eb89.jpeg', true, 14);
INSERT INTO public.foto VALUES (5, 'images/annunci/1770833464980_a2db0f55.jpg', false, 14);


ALTER TABLE public.foto ENABLE TRIGGER ALL;

--
-- Data for Name: recensione; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.recensione DISABLE TRIGGER ALL;

INSERT INTO public.recensione VALUES (1, 'topppp', 'venditore super affidabile', 5, 1, '000000002', '000000001');
INSERT INTO public.recensione VALUES (2, 'mai più', 'mi è arrivata rotta 😭', 1, 2, '000000002', '000000001');
INSERT INTO public.recensione VALUES (3, 'meh', 'vista la comodità di questo materasso, avrei preferito tenermi la scrivania', 2, 3, '000000001', '000000002');


ALTER TABLE public.recensione ENABLE TRIGGER ALL;

--
-- Name: annuncio_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.annuncio_id_seq', 14, true);


--
-- Name: consegna_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.consegna_id_seq', 4, true);


--
-- Name: foto_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.foto_id_seq', 5, true);


--
-- Name: indirizzo_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.indirizzo_id_seq', 3, true);


--
-- Name: offerta_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.offerta_id_seq', 9, true);


--
-- Name: recensione_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.recensione_id_seq', 3, true);


--
-- Name: transazione_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.transazione_id_seq', 4, true);


--
-- PostgreSQL database dump complete
--

