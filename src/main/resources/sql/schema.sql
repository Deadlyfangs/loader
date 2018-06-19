--Contents
CREATE TABLE contents (
  id           BIGINT NOT NULL,
  content      VARCHAR(1024),
  creationDate TIMESTAMP WITH TIME ZONE
);

CREATE SEQUENCE contents_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

ALTER SEQUENCE contents_id_seq OWNED BY contents.id;
ALTER TABLE ONLY contents
  ALTER COLUMN id SET DEFAULT nextval('contents_id_seq' :: REGCLASS);

ALTER TABLE ONLY contents
  ADD CONSTRAINT contents_pkey PRIMARY KEY (id);







