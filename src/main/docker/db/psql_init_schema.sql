CREATE TABLE bug (
    id integer NOT NULL,
    type character varying(80) NOT NULL,
    base_url character varying(2000) NOT NULL,
    path character varying(2000),
    description character varying(1000),
    time_inserted timestamp without time zone NOT NULL
);

CREATE SEQUENCE bug_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE bug_id_seq OWNED BY bug.id;

ALTER TABLE ONLY bug ALTER COLUMN id SET DEFAULT nextval('bug_id_seq'::regclass);

SELECT pg_catalog.setval('bug_id_seq', 66, true);

ALTER TABLE ONLY bug
    ADD CONSTRAINT bug_pkey PRIMARY KEY (id);
