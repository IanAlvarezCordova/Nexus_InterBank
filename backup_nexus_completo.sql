--
-- PostgreSQL database dump
--

\restrict A2Ij09GC0090f9P3UxKScY7dlDh9RdWcmwVanuEjp235tunaF4bpzT6kKLI6gkq

-- Dumped from database version 15.15
-- Dumped by pg_dump version 15.15

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

ALTER TABLE IF EXISTS ONLY public.tasaintereshistorico DROP CONSTRAINT IF EXISTS fkpuu858y3emncmog6u5h61bfu0;
ALTER TABLE IF EXISTS ONLY public.empresa DROP CONSTRAINT IF EXISTS fkdu3vo65ufxh4u0ivpg7muvyp0;
ALTER TABLE IF EXISTS ONLY public.cuenta DROP CONSTRAINT IF EXISTS fkc1gqul8nvx2l0lbicmpuq0lok;
ALTER TABLE IF EXISTS ONLY public.persona DROP CONSTRAINT IF EXISTS fk550jrw9tdxpowhwx40j8t982w;
ALTER TABLE IF EXISTS ONLY nexus_web.beneficiario DROP CONSTRAINT IF EXISTS fkn0fgsjhvn0213gmvxpufaqsl3;
ALTER TABLE IF EXISTS ONLY public.transaccion DROP CONSTRAINT IF EXISTS ukrjk5soumn6uuftxcc0fn4yug8;
ALTER TABLE IF EXISTS ONLY public.empresa DROP CONSTRAINT IF EXISTS ukfkso2kbttplho71hoeka6px1s;
ALTER TABLE IF EXISTS ONLY public.persona DROP CONSTRAINT IF EXISTS uk9cl9xk76vi8d2lk111xg8xc1h;
ALTER TABLE IF EXISTS ONLY public.cuenta DROP CONSTRAINT IF EXISTS uk6eoclgobf64ak92w7h4vjibj4;
ALTER TABLE IF EXISTS ONLY public.transaccion DROP CONSTRAINT IF EXISTS transaccion_pkey;
ALTER TABLE IF EXISTS ONLY public.tipocuenta DROP CONSTRAINT IF EXISTS tipocuenta_pkey;
ALTER TABLE IF EXISTS ONLY public.tasaintereshistorico DROP CONSTRAINT IF EXISTS tasaintereshistorico_pkey;
ALTER TABLE IF EXISTS ONLY public.representanteempresa DROP CONSTRAINT IF EXISTS representanteempresa_pkey;
ALTER TABLE IF EXISTS ONLY public.persona DROP CONSTRAINT IF EXISTS persona_pkey;
ALTER TABLE IF EXISTS ONLY public.empresa DROP CONSTRAINT IF EXISTS empresa_pkey;
ALTER TABLE IF EXISTS ONLY public.cuenta DROP CONSTRAINT IF EXISTS cuenta_pkey;
ALTER TABLE IF EXISTS ONLY public.cliente DROP CONSTRAINT IF EXISTS cliente_pkey;
ALTER TABLE IF EXISTS ONLY nexus_web.usuarioweb DROP CONSTRAINT IF EXISTS usuarioweb_pkey;
ALTER TABLE IF EXISTS ONLY nexus_web.usuarioweb DROP CONSTRAINT IF EXISTS uk_qutd6niaar1y48wqneuvd3k4b;
ALTER TABLE IF EXISTS ONLY nexus_web.beneficiario DROP CONSTRAINT IF EXISTS beneficiario_pkey;
ALTER TABLE IF EXISTS ONLY nexus_ventanilla.empleado DROP CONSTRAINT IF EXISTS uk_oqf74jqhm1ebgyhxm1hpi47a1;
ALTER TABLE IF EXISTS ONLY nexus_ventanilla.empleado DROP CONSTRAINT IF EXISTS empleado_pkey;
ALTER TABLE IF EXISTS nexus_web.usuarioweb ALTER COLUMN usuariowebid DROP DEFAULT;
ALTER TABLE IF EXISTS nexus_web.beneficiario ALTER COLUMN beneficiarioid DROP DEFAULT;
ALTER TABLE IF EXISTS nexus_ventanilla.empleado ALTER COLUMN empleadoid DROP DEFAULT;
DROP TABLE IF EXISTS public.transaccion;
DROP TABLE IF EXISTS public.tipocuenta;
DROP TABLE IF EXISTS public.tasaintereshistorico;
DROP TABLE IF EXISTS public.representanteempresa;
DROP TABLE IF EXISTS public.persona;
DROP TABLE IF EXISTS public.empresa;
DROP TABLE IF EXISTS public.cuenta;
DROP TABLE IF EXISTS public.cliente;
DROP SEQUENCE IF EXISTS nexus_web.usuarioweb_usuariowebid_seq;
DROP TABLE IF EXISTS nexus_web.usuarioweb;
DROP SEQUENCE IF EXISTS nexus_web.beneficiario_beneficiarioid_seq;
DROP TABLE IF EXISTS nexus_web.beneficiario;
DROP SEQUENCE IF EXISTS nexus_ventanilla.empleado_empleadoid_seq;
DROP TABLE IF EXISTS nexus_ventanilla.empleado;
DROP SCHEMA IF EXISTS nexus_web;
DROP SCHEMA IF EXISTS nexus_ventanilla;
DROP SCHEMA IF EXISTS nexus_transacciones;
DROP SCHEMA IF EXISTS nexus_cuentas;
DROP SCHEMA IF EXISTS nexus_clientes;
--
-- Name: nexus_clientes; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA nexus_clientes;


--
-- Name: nexus_cuentas; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA nexus_cuentas;


--
-- Name: nexus_transacciones; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA nexus_transacciones;


--
-- Name: nexus_ventanilla; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA nexus_ventanilla;


--
-- Name: nexus_web; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA nexus_web;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: empleado; Type: TABLE; Schema: nexus_ventanilla; Owner: -
--

CREATE TABLE nexus_ventanilla.empleado (
    empleadoid integer NOT NULL,
    activo boolean,
    apellidos character varying(255) NOT NULL,
    nombres character varying(255) NOT NULL,
    contrasenahash character varying(255) NOT NULL,
    rol character varying(255) NOT NULL,
    sucursalid integer NOT NULL,
    usuario character varying(255) NOT NULL
);


--
-- Name: empleado_empleadoid_seq; Type: SEQUENCE; Schema: nexus_ventanilla; Owner: -
--

CREATE SEQUENCE nexus_ventanilla.empleado_empleadoid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: empleado_empleadoid_seq; Type: SEQUENCE OWNED BY; Schema: nexus_ventanilla; Owner: -
--

ALTER SEQUENCE nexus_ventanilla.empleado_empleadoid_seq OWNED BY nexus_ventanilla.empleado.empleadoid;


--
-- Name: beneficiario; Type: TABLE; Schema: nexus_web; Owner: -
--

CREATE TABLE nexus_web.beneficiario (
    beneficiarioid integer NOT NULL,
    alias character varying(255),
    fecharegistro timestamp(6) without time zone,
    nombretitular character varying(255) NOT NULL,
    numerocuentadestino character varying(255) NOT NULL,
    tipocuenta character varying(255),
    usuariowebid integer NOT NULL
);


--
-- Name: beneficiario_beneficiarioid_seq; Type: SEQUENCE; Schema: nexus_web; Owner: -
--

CREATE SEQUENCE nexus_web.beneficiario_beneficiarioid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: beneficiario_beneficiarioid_seq; Type: SEQUENCE OWNED BY; Schema: nexus_web; Owner: -
--

ALTER SEQUENCE nexus_web.beneficiario_beneficiarioid_seq OWNED BY nexus_web.beneficiario.beneficiarioid;


--
-- Name: usuarioweb; Type: TABLE; Schema: nexus_web; Owner: -
--

CREATE TABLE nexus_web.usuarioweb (
    usuariowebid integer NOT NULL,
    clienteidcore integer NOT NULL,
    emailcontacto character varying(255) NOT NULL,
    estado character varying(255) NOT NULL,
    fecharegistro timestamp(6) without time zone,
    intentosfallidos integer,
    contrasenahash character varying(255) NOT NULL,
    ultimoacceso timestamp(6) without time zone,
    usuario character varying(255) NOT NULL
);


--
-- Name: usuarioweb_usuariowebid_seq; Type: SEQUENCE; Schema: nexus_web; Owner: -
--

CREATE SEQUENCE nexus_web.usuarioweb_usuariowebid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: usuarioweb_usuariowebid_seq; Type: SEQUENCE OWNED BY; Schema: nexus_web; Owner: -
--

ALTER SEQUENCE nexus_web.usuarioweb_usuariowebid_seq OWNED BY nexus_web.usuarioweb.usuariowebid;


--
-- Name: cliente; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cliente (
    clienteid integer NOT NULL,
    estado character varying(15) NOT NULL,
    fecharegistro date NOT NULL,
    tipocliente character varying(1) NOT NULL
);


--
-- Name: cliente_clienteid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.cliente ALTER COLUMN clienteid ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.cliente_clienteid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: cuenta; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cuenta (
    cuentaid integer NOT NULL,
    clienteid integer NOT NULL,
    estado character varying(15) NOT NULL,
    fechaapertura date NOT NULL,
    numerocuenta character varying(20) NOT NULL,
    saldo numeric(38,2) NOT NULL,
    sucursalidapertura integer NOT NULL,
    tipocuentaid integer NOT NULL
);


--
-- Name: cuenta_cuentaid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.cuenta ALTER COLUMN cuentaid ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.cuenta_cuentaid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: empresa; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.empresa (
    correoelectronico character varying(100) NOT NULL,
    razonsocial character varying(150) NOT NULL,
    ruc character varying(13) NOT NULL,
    telefono character varying(20) NOT NULL,
    clienteid integer NOT NULL
);


--
-- Name: persona; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.persona (
    apellidos character varying(100) NOT NULL,
    direccion character varying(200) NOT NULL,
    fechanacimiento date NOT NULL,
    nombres character varying(100) NOT NULL,
    numeroidentificacion character varying(13) NOT NULL,
    tipoidentificacion character varying(15) NOT NULL,
    clienteid integer NOT NULL
);


--
-- Name: representanteempresa; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.representanteempresa (
    clienteempresaid integer NOT NULL,
    empresaid integer NOT NULL,
    estado character varying(15) NOT NULL,
    fechafin date,
    fechainicio date NOT NULL,
    personaid integer NOT NULL,
    rol character varying(50) NOT NULL
);


--
-- Name: representanteempresa_clienteempresaid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.representanteempresa ALTER COLUMN clienteempresaid ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.representanteempresa_clienteempresaid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tasaintereshistorico; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tasaintereshistorico (
    tasaintereshistoricoid integer NOT NULL,
    fechafinvigencia date,
    fechainiciovigencia date NOT NULL,
    tasamensual double precision NOT NULL,
    tipocuentaid integer NOT NULL
);


--
-- Name: tasaintereshistorico_tasaintereshistoricoid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.tasaintereshistorico ALTER COLUMN tasaintereshistoricoid ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tasaintereshistorico_tasaintereshistoricoid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tipocuenta; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tipocuenta (
    tipocuentaid integer NOT NULL,
    descripcion character varying(200) NOT NULL,
    estado character varying(15) NOT NULL,
    nombre character varying(50) NOT NULL,
    tipoamortizacion character varying(20) NOT NULL
);


--
-- Name: tipocuenta_tipocuentaid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.tipocuenta ALTER COLUMN tipocuentaid ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tipocuenta_tipocuentaid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: transaccion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.transaccion (
    transaccion_id integer NOT NULL,
    cuenta_destino character varying(255),
    cuenta_origen character varying(255),
    descripcion character varying(255),
    estado character varying(255),
    fecha_ejecucion timestamp(6) without time zone,
    id_banco_destino integer,
    id_banco_origen integer,
    instruction_id character varying(255),
    mensaje_error character varying(255),
    monto numeric(38,2),
    referencia character varying(255),
    rol_transaccion character varying(255),
    version bigint
);


--
-- Name: transaccion_transaccion_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.transaccion ALTER COLUMN transaccion_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.transaccion_transaccion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: empleado empleadoid; Type: DEFAULT; Schema: nexus_ventanilla; Owner: -
--

ALTER TABLE ONLY nexus_ventanilla.empleado ALTER COLUMN empleadoid SET DEFAULT nextval('nexus_ventanilla.empleado_empleadoid_seq'::regclass);


--
-- Name: beneficiario beneficiarioid; Type: DEFAULT; Schema: nexus_web; Owner: -
--

ALTER TABLE ONLY nexus_web.beneficiario ALTER COLUMN beneficiarioid SET DEFAULT nextval('nexus_web.beneficiario_beneficiarioid_seq'::regclass);


--
-- Name: usuarioweb usuariowebid; Type: DEFAULT; Schema: nexus_web; Owner: -
--

ALTER TABLE ONLY nexus_web.usuarioweb ALTER COLUMN usuariowebid SET DEFAULT nextval('nexus_web.usuarioweb_usuariowebid_seq'::regclass);


--
-- Data for Name: empleado; Type: TABLE DATA; Schema: nexus_ventanilla; Owner: -
--

COPY nexus_ventanilla.empleado (empleadoid, activo, apellidos, nombres, contrasenahash, rol, sucursalid, usuario) FROM stdin;
1	t	Sistema	Administrador	$2a$10$RDVXz57eqROgqjp1TSVV6OjT6fFxxsRyXf7K6U/GsgBYHgmhQ7dOy	ADMIN	1	admin
\.


--
-- Data for Name: beneficiario; Type: TABLE DATA; Schema: nexus_web; Owner: -
--

COPY nexus_web.beneficiario (beneficiarioid, alias, fecharegistro, nombretitular, numerocuentadestino, tipocuenta, usuariowebid) FROM stdin;
\.


--
-- Data for Name: usuarioweb; Type: TABLE DATA; Schema: nexus_web; Owner: -
--

COPY nexus_web.usuarioweb (usuariowebid, clienteidcore, emailcontacto, estado, fecharegistro, intentosfallidos, contrasenahash, ultimoacceso, usuario) FROM stdin;
1	1	stephani.rivera@novaseguroslatam.com	ACTIVO	\N	\N	$2a$10$irgfxc8UF3Kt4RgfnsLE4emLKfzgLGhqBfnb16jCRMGAMGXK94voK	2025-12-21 20:01:14.892485	STEPHI
2	2	rivera1@gmail.com	ACTIVO	\N	\N	$2a$10$pssNLEPQQbtY0AM4AU3t/.KsTQhGIV.urZwuSNxCQTVN0K2aOQ2YK	2025-12-21 20:20:43.654176	ERIVERA
\.


--
-- Data for Name: cliente; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cliente (clienteid, estado, fecharegistro, tipocliente) FROM stdin;
2	ACTIVO	2025-12-21	P
1	ACTIVA	2025-12-21	P
\.


--
-- Data for Name: cuenta; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cuenta (cuentaid, clienteid, estado, fechaapertura, numerocuenta, saldo, sucursalidapertura, tipocuentaid) FROM stdin;
2	1	ACTIVA	2025-12-21	2024120001	460.00	1	1
1	1	ACTIVA	2025-12-21	6345271089	30.00	1	1
3	2	ACTIVA	2025-12-21	2024120002	6009.00	1	1
\.


--
-- Data for Name: empresa; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.empresa (correoelectronico, razonsocial, ruc, telefono, clienteid) FROM stdin;
\.


--
-- Data for Name: persona; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.persona (apellidos, direccion, fechanacimiento, nombres, numeroidentificacion, tipoidentificacion, clienteid) FROM stdin;
rivera espinosa	carapungo	2000-01-01	stephani jamilette	1726325614	CEDULA	1
Rivera Vernaza	Calderon	2000-01-01	Edison Lenin	0502748692	CEDULA	2
\.


--
-- Data for Name: representanteempresa; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.representanteempresa (clienteempresaid, empresaid, estado, fechafin, fechainicio, personaid, rol) FROM stdin;
\.


--
-- Data for Name: tasaintereshistorico; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.tasaintereshistorico (tasaintereshistoricoid, fechafinvigencia, fechainiciovigencia, tasamensual, tipocuentaid) FROM stdin;
\.


--
-- Data for Name: tipocuenta; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.tipocuenta (tipocuentaid, descripcion, estado, nombre, tipoamortizacion) FROM stdin;
1	Cuenta de Ahorros	ACTIVO	AHORROS	MENSUAL
2	Cuenta Corriente	ACTIVO	CORRIENTE	MENSUAL
\.


--
-- Data for Name: transaccion; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.transaccion (transaccion_id, cuenta_destino, cuenta_origen, descripcion, estado, fecha_ejecucion, id_banco_destino, id_banco_origen, instruction_id, mensaje_error, monto, referencia, rol_transaccion, version) FROM stdin;
1	6345271089	2024120001	Transferencia Exitosa	COMPLETED	2025-12-21 19:30:13.240806	2	2	da194c11-e6b0-4010-91cc-f18f5be95b10	\N	34.00	da194c11-e6b0-4010-91cc-f18f5be95b10	DEBITO	1
2	2024120002	2024120001	Transferencia Exitosa	COMPLETED	2025-12-21 19:33:13.495295	2	2	c428663d-61c3-4078-be87-d00756462608	\N	4.00	c428663d-61c3-4078-be87-d00756462608	DEBITO	1
3	2024120001	2024120002	Transferencia Exitosa	COMPLETED	2025-12-21 19:39:49.382754	2	2	19951e4b-1adb-4a6c-8eae-40b412a081d4	\N	4.00	19951e4b-1adb-4a6c-8eae-40b412a081d4	DEBITO	1
\.


--
-- Name: empleado_empleadoid_seq; Type: SEQUENCE SET; Schema: nexus_ventanilla; Owner: -
--

SELECT pg_catalog.setval('nexus_ventanilla.empleado_empleadoid_seq', 1, true);


--
-- Name: beneficiario_beneficiarioid_seq; Type: SEQUENCE SET; Schema: nexus_web; Owner: -
--

SELECT pg_catalog.setval('nexus_web.beneficiario_beneficiarioid_seq', 1, false);


--
-- Name: usuarioweb_usuariowebid_seq; Type: SEQUENCE SET; Schema: nexus_web; Owner: -
--

SELECT pg_catalog.setval('nexus_web.usuarioweb_usuariowebid_seq', 2, true);


--
-- Name: cliente_clienteid_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.cliente_clienteid_seq', 2, true);


--
-- Name: cuenta_cuentaid_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.cuenta_cuentaid_seq', 3, true);


--
-- Name: representanteempresa_clienteempresaid_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.representanteempresa_clienteempresaid_seq', 1, false);


--
-- Name: tasaintereshistorico_tasaintereshistoricoid_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tasaintereshistorico_tasaintereshistoricoid_seq', 1, false);


--
-- Name: tipocuenta_tipocuentaid_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tipocuenta_tipocuentaid_seq', 2, true);


--
-- Name: transaccion_transaccion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.transaccion_transaccion_id_seq', 3, true);


--
-- Name: empleado empleado_pkey; Type: CONSTRAINT; Schema: nexus_ventanilla; Owner: -
--

ALTER TABLE ONLY nexus_ventanilla.empleado
    ADD CONSTRAINT empleado_pkey PRIMARY KEY (empleadoid);


--
-- Name: empleado uk_oqf74jqhm1ebgyhxm1hpi47a1; Type: CONSTRAINT; Schema: nexus_ventanilla; Owner: -
--

ALTER TABLE ONLY nexus_ventanilla.empleado
    ADD CONSTRAINT uk_oqf74jqhm1ebgyhxm1hpi47a1 UNIQUE (usuario);


--
-- Name: beneficiario beneficiario_pkey; Type: CONSTRAINT; Schema: nexus_web; Owner: -
--

ALTER TABLE ONLY nexus_web.beneficiario
    ADD CONSTRAINT beneficiario_pkey PRIMARY KEY (beneficiarioid);


--
-- Name: usuarioweb uk_qutd6niaar1y48wqneuvd3k4b; Type: CONSTRAINT; Schema: nexus_web; Owner: -
--

ALTER TABLE ONLY nexus_web.usuarioweb
    ADD CONSTRAINT uk_qutd6niaar1y48wqneuvd3k4b UNIQUE (usuario);


--
-- Name: usuarioweb usuarioweb_pkey; Type: CONSTRAINT; Schema: nexus_web; Owner: -
--

ALTER TABLE ONLY nexus_web.usuarioweb
    ADD CONSTRAINT usuarioweb_pkey PRIMARY KEY (usuariowebid);


--
-- Name: cliente cliente_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT cliente_pkey PRIMARY KEY (clienteid);


--
-- Name: cuenta cuenta_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cuenta
    ADD CONSTRAINT cuenta_pkey PRIMARY KEY (cuentaid);


--
-- Name: empresa empresa_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.empresa
    ADD CONSTRAINT empresa_pkey PRIMARY KEY (clienteid);


--
-- Name: persona persona_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.persona
    ADD CONSTRAINT persona_pkey PRIMARY KEY (clienteid);


--
-- Name: representanteempresa representanteempresa_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.representanteempresa
    ADD CONSTRAINT representanteempresa_pkey PRIMARY KEY (clienteempresaid);


--
-- Name: tasaintereshistorico tasaintereshistorico_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tasaintereshistorico
    ADD CONSTRAINT tasaintereshistorico_pkey PRIMARY KEY (tasaintereshistoricoid);


--
-- Name: tipocuenta tipocuenta_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tipocuenta
    ADD CONSTRAINT tipocuenta_pkey PRIMARY KEY (tipocuentaid);


--
-- Name: transaccion transaccion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.transaccion
    ADD CONSTRAINT transaccion_pkey PRIMARY KEY (transaccion_id);


--
-- Name: cuenta uk6eoclgobf64ak92w7h4vjibj4; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cuenta
    ADD CONSTRAINT uk6eoclgobf64ak92w7h4vjibj4 UNIQUE (numerocuenta);


--
-- Name: persona uk9cl9xk76vi8d2lk111xg8xc1h; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.persona
    ADD CONSTRAINT uk9cl9xk76vi8d2lk111xg8xc1h UNIQUE (numeroidentificacion);


--
-- Name: empresa ukfkso2kbttplho71hoeka6px1s; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.empresa
    ADD CONSTRAINT ukfkso2kbttplho71hoeka6px1s UNIQUE (ruc);


--
-- Name: transaccion ukrjk5soumn6uuftxcc0fn4yug8; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.transaccion
    ADD CONSTRAINT ukrjk5soumn6uuftxcc0fn4yug8 UNIQUE (instruction_id);


--
-- Name: beneficiario fkn0fgsjhvn0213gmvxpufaqsl3; Type: FK CONSTRAINT; Schema: nexus_web; Owner: -
--

ALTER TABLE ONLY nexus_web.beneficiario
    ADD CONSTRAINT fkn0fgsjhvn0213gmvxpufaqsl3 FOREIGN KEY (usuariowebid) REFERENCES nexus_web.usuarioweb(usuariowebid);


--
-- Name: persona fk550jrw9tdxpowhwx40j8t982w; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.persona
    ADD CONSTRAINT fk550jrw9tdxpowhwx40j8t982w FOREIGN KEY (clienteid) REFERENCES public.cliente(clienteid);


--
-- Name: cuenta fkc1gqul8nvx2l0lbicmpuq0lok; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cuenta
    ADD CONSTRAINT fkc1gqul8nvx2l0lbicmpuq0lok FOREIGN KEY (tipocuentaid) REFERENCES public.tipocuenta(tipocuentaid);


--
-- Name: empresa fkdu3vo65ufxh4u0ivpg7muvyp0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.empresa
    ADD CONSTRAINT fkdu3vo65ufxh4u0ivpg7muvyp0 FOREIGN KEY (clienteid) REFERENCES public.cliente(clienteid);


--
-- Name: tasaintereshistorico fkpuu858y3emncmog6u5h61bfu0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tasaintereshistorico
    ADD CONSTRAINT fkpuu858y3emncmog6u5h61bfu0 FOREIGN KEY (tipocuentaid) REFERENCES public.tipocuenta(tipocuentaid);


--
-- PostgreSQL database dump complete
--

\unrestrict A2Ij09GC0090f9P3UxKScY7dlDh9RdWcmwVanuEjp235tunaF4bpzT6kKLI6gkq

